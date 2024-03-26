using Asp.Versioning.ApiExplorer;
using Microsoft.Extensions.Options;
using Microsoft.OpenApi.Models;
using Swashbuckle.AspNetCore.SwaggerGen;
using System.Text;

namespace QRStockMate.SwaggerConfig {
	public class ConfigureSwaggerOptions : IConfigureOptions<SwaggerGenOptions> {
		private readonly IApiVersionDescriptionProvider provider;
		public ConfigureSwaggerOptions(IApiVersionDescriptionProvider provider) => this.provider = provider;

		/// <inheritdoc />
		public void Configure(SwaggerGenOptions options) {
			foreach (var description in provider.ApiVersionDescriptions) {
				options.SwaggerDoc(description.GroupName, CreateInfoForApiVersion(description));
			}
		}

		private static OpenApiInfo CreateInfoForApiVersion(ApiVersionDescription description) {
			var text = new StringBuilder("An application made with OpenAPI, Swashbuckle, and API versioning.");
			text.AppendLine();
			text.Append("\n<a href=\"https://localhost:7220/healthchecks-ui\">HealthChecks</a>"); // Appending a simple HTML link
			var info = new OpenApiInfo() {
				Title = "QRStockMate API",
				Version = description.ApiVersion.ToString(),
				Contact = new OpenApiContact() { Name = "Acorán González Moray", Email = "acorangonzalezmoray@gmail.com", Url = new Uri("https://github.com/AcoranGonzalezMoray") },
				License = new OpenApiLicense() { Name = "MIT", Url = new Uri("https://opensource.org/licenses/MIT") }
			};

			if (description.IsDeprecated) {
				text.Append(" This API version has been deprecated.");
			}

			info.Description = text.ToString();

			return info;
		}
	}
}