using Swashbuckle.AspNetCore.Annotations;

namespace QRStockMate.AplicationCore.Entities {
	[SwaggerSchema("Company entity")]
	public class Company {
		[SwaggerSchema("ID of the company")]
		public int Id { get; set; }

		[SwaggerSchema("Name of the company")]
		public string Name { get; set; }

		[SwaggerSchema("Director of the company")]
		public string Director { get; set; }

		[SwaggerSchema("Location of the company")]
		public string Location { get; set; }

		[SwaggerSchema("Code associated with the company in the format XXX-XXX")]
		public string Code { get; set; }

		[SwaggerSchema("Warehouse ID associated with the company (semicolon-separated if multiple)")]
		public string WarehouseId { get; set; }

		[SwaggerSchema("Employee ID associated with the company (semicolon-separated if multiple)")]
		public string EmployeeId { get; set; }
	}
}
