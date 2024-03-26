using Swashbuckle.AspNetCore.Annotations;

namespace QRStockMate.DTOs {
	[SwaggerSchema("Vehicle model")]
	public class VehicleModel {
		[SwaggerSchema("ID of the vehicle")]
		public int Id { get; set; }

		[SwaggerSchema("Code associated with the vehicle")]
		public string Code { get; set; }

		[SwaggerSchema("Manufacturer of the vehicle (e.g., Toyota, Ford, etc.)")]
		public string Make { get; set; }

		[SwaggerSchema("Model of the vehicle")]
		public string Model { get; set; }

		[SwaggerSchema("Year of manufacture of the vehicle")]
		public int Year { get; set; }

		[SwaggerSchema("Color of the vehicle")]
		public string Color { get; set; }

		[SwaggerSchema("License plate number of the vehicle")]
		public string LicensePlate { get; set; }

		[SwaggerSchema("Maximum load capacity of the vehicle")]
		public decimal MaxLoad { get; set; }

		[SwaggerSchema("Location of the vehicle")]
		public string Location { get; set; }
	}
}
