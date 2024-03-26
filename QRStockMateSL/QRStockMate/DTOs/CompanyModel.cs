using Swashbuckle.AspNetCore.Annotations;

namespace QRStockMate.DTOs {
	[SwaggerSchema("Company model")] // Schema overview description
	public class CompanyModel {
		[SwaggerSchema("Company ID")] // Field description
		public int Id { get; set; }

		[SwaggerSchema("Company name")]
		public string Name { get; set; }

		[SwaggerSchema("Director's name")]
		public string Director { get; set; }

		[SwaggerSchema("Company location")]
		public string Location { get; set; }

		[SwaggerSchema("Company code")]
		public string Code { get; set; }

		[SwaggerSchema("Warehouse IDs")]
		public string WarehouseId { get; set; }   // Comma-separated list of warehouse IDs

		[SwaggerSchema("Employee IDs")]
		public string EmployeeId { get; set; }   // Comma-separated list of employee IDs
	}
}
