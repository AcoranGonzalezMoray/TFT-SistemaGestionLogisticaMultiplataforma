using Asp.Versioning.ApiExplorer;
using CoffeeMachine.Api.HealthCheck;
using EasyNetQ;
using EasyNetQ.DI;
using HealthChecks.ApplicationStatus.DependencyInjection;
using HealthChecks.UI.Client;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Diagnostics.HealthChecks;
using Microsoft.AspNetCore.Mvc.Authorization;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Options;
using Microsoft.IdentityModel.Tokens;
using QRStockMate.AplicationCore.Interfaces.Repositories;
using QRStockMate.AplicationCore.Interfaces.Services;
using QRStockMate.HealthCheck;
using QRStockMate.Infrastructure.Data;
using QRStockMate.Infrastructure.Repositories;
using QRStockMate.QueueService;
using QRStockMate.Services;
using QRStockMate.SwaggerConfig;
using QRStockMate.Utility;
using RabbitMQ.Client;
using Swashbuckle.AspNetCore.SwaggerGen;
using System.Text;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers(opt => {
	var policy = new AuthorizationPolicyBuilder().RequireAuthenticatedUser().Build();
	opt.Filters.Add(new AuthorizeFilter(policy));
});

// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

//SQL-SERVER
builder.Services.AddDbContext<ApplicationDbContext>(opt =>
	opt.UseSqlServer(
		builder.Configuration.GetConnectionString("Conexion")
		)
	);

//Service
//Base
builder.Services.AddScoped(typeof(IBaseRepository<>), typeof(BaseRepository<>));
builder.Services.AddScoped(typeof(IBaseService<>), typeof(BaseService<>));

//User
builder.Services.AddScoped(typeof(IUserService), typeof(UserService));
builder.Services.AddScoped(typeof(IUserRepository), typeof(UserRepository));

//Company
builder.Services.AddScoped(typeof(ICompanyService), typeof(CompanyService));
builder.Services.AddScoped(typeof(ICompanyRepository), typeof(CompanyRepository));

//Item
builder.Services.AddScoped(typeof(IItemService), typeof(ItemService));
builder.Services.AddScoped(typeof(IItemRepository), typeof(ItemRepository));

//TransactionHistory
builder.Services.AddScoped(typeof(ITransactionHistoryService), typeof(TransactionHistoryService));
builder.Services.AddScoped(typeof(ITransactionHistoryRepository), typeof(TransactionHistoryRepository));

//Warehouse
builder.Services.AddScoped(typeof(IWarehouseService), typeof(WarehouseService));
builder.Services.AddScoped(typeof(IWarehouseRepository), typeof(WarehouseRepository));

//Vehicle
builder.Services.AddScoped(typeof(IVehicleService), typeof(VehicleService));
builder.Services.AddScoped(typeof(IVehicleRepository), typeof(VehicleRepository));

//TransportRoute
builder.Services.AddScoped(typeof(ITransportRouteService), typeof(TransportRouteService));
builder.Services.AddScoped(typeof(ITransportRouteRepository), typeof(TransportRouteRepository));

//StorageFirebase
builder.Services.AddScoped(typeof(IStorageService), typeof(StorageService));
builder.Services.AddScoped(typeof(IStorageRepository), typeof(StorageRepository));

//Message
builder.Services.AddScoped(typeof(IMessageService), typeof(MessageService));
builder.Services.AddScoped(typeof(IMessageRepository), typeof(MessageRepository));

//Communication
builder.Services.AddScoped(typeof(ICommunicationService), typeof(CommunicationService));
builder.Services.AddScoped(typeof(ICommunicationRepository), typeof(CommunicationRepository));

//Consumer
var bus = RabbitHutch.CreateBus("host=localhost;username=guest;password=guest", serviceRegister => {
	serviceRegister.Register<ConnectionFactory>(_ => {
		var connectionFactory = new ConnectionFactory();
		return connectionFactory;
	});

	// Configuración del límite máximo de conexiones
	serviceRegister.Register<ConnectionFactory>(_ => {
		var connectionFactory = new ConnectionFactory();
		connectionFactory.HostName = "localhost";
		connectionFactory.UserName = "guest";
		connectionFactory.Password = "guest";
		connectionFactory.DispatchConsumersAsync = true;
		connectionFactory.RequestedConnectionTimeout = TimeSpan.FromSeconds(30);

		// Limitar la cantidad de conexiones
		connectionFactory.RequestedChannelMax = 5;
		return connectionFactory;
	});
});

builder.Services.AddSingleton(bus);

//SwaggerOptions
builder.Services.AddTransient<IConfigureOptions<SwaggerGenOptions>, ConfigureSwaggerOptions>();

//AutoMapper
builder.Services.AddAutoMapper(typeof(Program));

//JWT
builder.Services.AddScoped<IJwtTokenRepository, JwtTokenRepository>();

//CORS
var MyAllowSpecificOrigins = "_myAllowSpecificOrigins";
builder.Services.AddCors(options => {
	options.AddPolicy(name: MyAllowSpecificOrigins,
					  policy => {
						  policy.AllowAnyOrigin()
						  .AllowAnyHeader()
						  .AllowAnyMethod();
					  });
});
var urlPathToData = Path.Combine(Environment.CurrentDirectory, "appsettings.json");

builder.Services.AddHealthChecks()
	.AddApplicationStatus(name: "API status", tags: new[] { "api" })
	.AddDiskStorageHealthCheck(
		setup: diskOptions => diskOptions.AddDrive(driveName: "D:\\", 500),
		name: "Diskstorage " + "D:\\  over of 500MB", tags: new[] { "storage", "memory" })
	.AddProcessAllocatedMemoryHealthCheck(maximumMegabytesAllocated: 400, name: "Process Allocated Memory", tags: new[] { "storage", "process", "memory" })
	.AddSqlServer(builder.Configuration.GetConnectionString("Conexion"), name: "SQL Server", tags: new[] { "sql server", "api" })
	.AddCheck<FirebaseHealthCheck>("Firebase status", tags: new[] { "firebase", "api" })
	.AddCheck("Appsettings file exist", new FileExistenceHealthCheck(urlPathToData), tags: new[] { "file", "persistence", "api" })
	.AddCheck<EndPointHealthCheck>("Endpoint versioning status", tags: new[] { "endpoint", "api" })
	.AddCheck<UrlHealthCheck>("Endpoint code status", tags: new[] { "endpoint", "api" });

builder.Services.AddHealthChecksUI(setupSettings: opt => {
	opt.SetEvaluationTimeInSeconds(15);
	opt.MaximumHistoryEntriesPerEndpoint(60);
	opt.SetApiMaxActiveRequests(1);
	opt.AddHealthCheckEndpoint("HealthCheck API", "/healthcheck");
	opt.AddWebhookNotification("email",
				uri: "https://localhost:7220/api/v1/notify/email",
				payload: "{ \"message\": \"Webhook report for [[LIVENESS]]: [[FAILURE]] - Description: [[DESCRIPTIONS]]\"}",
				restorePayload: "{ \"message\": \"[[LIVENESS]] is back to life\"}");
}).AddInMemoryStorage();

//Json Web Token (JWT)
builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
	.AddJwtBearer(options => {
		options.TokenValidationParameters = new TokenValidationParameters() {
			ValidateIssuer = false,
			ValidateAudience = false,
			ValidateIssuerSigningKey = true,
			IssuerSigningKey = new SymmetricSecurityKey(
				Encoding.UTF8.GetBytes(builder.Configuration["ConfigJwt:Key"] ?? string.Empty)
			)
		};
	});

builder.Services.AddApiVersioning()
	.AddMvc()
	.AddApiExplorer(
	options => {
		options.GroupNameFormat = "'v'VVV";
		options.SubstituteApiVersionInUrl = true;
	});

builder.Services.AddSwaggerGen(c => {
	c.EnableAnnotations();
});

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment()) {
	var descriptions = app.Services.GetRequiredService<IApiVersionDescriptionProvider>();
	app.UseSwagger();
	app.UseSwaggerUI(
		options => {
			foreach (var description in descriptions.ApiVersionDescriptions) {
				var url = $"/swagger/{description.GroupName}/swagger.json";
				var name = description.GroupName.ToUpperInvariant();
				options.SwaggerEndpoint(url, name);
			}
		});
}

app.UseHttpsRedirection();

app.UseCors(MyAllowSpecificOrigins);

app.UseRouting().UseEndpoints(config => config.MapHealthChecksUI());

app.UseHealthChecks("/healthcheck", new HealthCheckOptions {
	Predicate = _ => true,
	ResponseWriter = UIResponseWriter.WriteHealthCheckUIResponse
});

app.UseHealthChecksUI(options => {
	options.UIPath = "/healthchecks-ui";
	options.ApiPath = "/health-ui-api";
});

app.UseAuthorization();

app.MapControllers();

app.Run();