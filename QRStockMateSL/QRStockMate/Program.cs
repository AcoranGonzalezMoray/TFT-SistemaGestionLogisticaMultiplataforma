using Asp.Versioning.ApiExplorer;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;

using Microsoft.AspNetCore.Mvc.Authorization;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Options;
using Microsoft.IdentityModel.Tokens;
using QRStockMate.AplicationCore.Interfaces.Repositories;
using QRStockMate.AplicationCore.Interfaces.Services;
using QRStockMate.Infrastructure.Data;
using QRStockMate.Infrastructure.Repositories;
using QRStockMate.Services;
using QRStockMate.SwaggerConfig;
using QRStockMate.Utility;
using Swashbuckle.AspNetCore.SwaggerGen;
using System;
using System.Text;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

// Se le agrega la seguridad a los controladores para que se le envie el token valido

builder.Services.AddControllers(opt =>
{
    var policy = new AuthorizationPolicyBuilder().RequireAuthenticatedUser().Build();
    opt.Filters.Add(new AuthorizeFilter(policy));

});


//builder.Services.AddControllers();

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


//AutoMapper
builder.Services.AddAutoMapper(typeof(Program));





//JWT
builder.Services.AddScoped<IJwtTokenRepository, JwtTokenRepository>();

//CORS
var MyAllowSpecificOrigins = "_myAllowSpecificOrigins";
builder.Services.AddCors(options =>
{
    options.AddPolicy(name: MyAllowSpecificOrigins,
                      policy =>
                      {
                          policy.AllowAnyOrigin()
                          .AllowAnyHeader()
                          .AllowAnyMethod();
                      });
});


//Json Web Token (JWT)
builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options =>
    {
        options.TokenValidationParameters = new TokenValidationParameters()
        {
            ValidateIssuer = false,
            ValidateAudience = false,
            ValidateIssuerSigningKey = true,
            IssuerSigningKey = new SymmetricSecurityKey(
                Encoding.UTF8.GetBytes(builder.Configuration["ConfigJwt:Key"] ?? string.Empty)
            )
        };
    });


//builder.Services.AddApiVersioning()
//	.AddMvc()
//	.AddApiExplorer(
//    options => {
//        options.GroupNameFormat = "'v'VVV";
//        options.SubstituteApiVersionInUrl = true;
//    });

builder.Services.AddSwaggerGen(c => { 
    c.EnableAnnotations();
});

//builder.Services.AddTransient<IConfigureOptions<SwaggerGenOptions>,ConfigureSwaggerOptions>();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
	//var descriptions = app.Services.GetRequiredService<IApiVersionDescriptionProvider>();
	app.UseSwagger();
	app.UseSwaggerUI();
	//app.UseSwaggerUI(
	//    options => {
	//	    foreach (var description in descriptions.ApiVersionDescriptions) {
	//		    var url = $"/swagger/{description.GroupName}/swagger.json";
	//		    var name = description.GroupName.ToUpperInvariant();
	//		    options.SwaggerEndpoint(url, name);
	//	    }
	//    });
}

app.UseHttpsRedirection();

app.UseCors(MyAllowSpecificOrigins);


app.UseAuthorization();

app.MapControllers();

app.Run();
