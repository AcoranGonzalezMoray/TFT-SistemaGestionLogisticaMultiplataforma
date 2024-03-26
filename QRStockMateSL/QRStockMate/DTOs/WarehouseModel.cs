using Swashbuckle.AspNetCore.Annotations;

namespace QRStockMate.DTOs {
	[SwaggerSchema("Model representing a warehouse")]
	public class WarehouseModel {
		[SwaggerSchema("ID of the warehouse")]
		public int Id { get; set; }

		[SwaggerSchema("Name of the warehouse")]
		public string Name { get; set; }

		[SwaggerSchema("Location of the warehouse")]
		public string Location { get; set; }

		[SwaggerSchema("Organization associated with the warehouse")]
		public string Organization { get; set; }

		[SwaggerSchema("ID of the administrator of the warehouse")]
		public int IdAdministrator { get; set; }

		[SwaggerSchema("IDs of items stored in the warehouse")]
		public string IdItems { get; set; }

		[SwaggerSchema("URL associated with the warehouse")]
		public string Url { get; set; }

		[SwaggerSchema("Latitude of the warehouse")]
		public double Latitude { get; set; }

		[SwaggerSchema("Longitude of the warehouse")]
		public double Longitude { get; set; }
	}
}
