using Asp.Versioning.ApiExplorer;
using Microsoft.AspNetCore.Mvc.ApiExplorer;
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
			var text = new StringBuilder("An example application with OpenAPI, Swashbuckle, and API versioning.");
			var info = new OpenApiInfo() {
				Title = "Example API",
				Version = description.ApiVersion.ToString(),
				Contact = new OpenApiContact() { Name = "Bill Mei", Email = "bill.mei@somewhere.com" },
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