using Swashbuckle.AspNetCore.Annotations;
using QRStockMate.AplicationCore.Entities;

namespace QRStockMate.DTOs {
	[SwaggerSchema("User model")]
	public class UserModel {
		[SwaggerSchema("ID of the user")]
		public int Id { get; set; }

		[SwaggerSchema("Name of the user")]
		public string Name { get; set; }

		[SwaggerSchema("Email address of the user")]
		public string Email { get; set; }

		[SwaggerSchema("Password of the user")]
		public string Password { get; set; }

		[SwaggerSchema("Phone number of the user")]
		public string Phone { get; set; }

		[SwaggerSchema("Code associated with the user")]
		public string Code { get; set; }

		[SwaggerSchema("URL associated with the user")]
		public string Url { get; set; }

		[SwaggerSchema("Role of the user")]
		public RoleUser Role { get; set; }
	}
}
