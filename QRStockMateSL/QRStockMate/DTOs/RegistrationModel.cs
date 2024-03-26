using Swashbuckle.AspNetCore.Annotations;

namespace QRStockMate.DTOs {
	[SwaggerSchema("Registration model")]
	public class RegistrationModel {
		[SwaggerSchema("User information")]
		public UserModel User { get; set; }

		[SwaggerSchema("Company information")]
		public CompanyModel Company { get; set; }
	}
}
