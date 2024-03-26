using Swashbuckle.AspNetCore.Annotations;

namespace QRStockMate.DTOs {
	[SwaggerSchema("Item model")] // Schema overview description
	public class ItemModel {
		[SwaggerSchema("Item ID")] // Field description
		public int Id { get; set; }

		[SwaggerSchema("Item name")]
		public string Name { get; set; }

		[SwaggerSchema("Warehouse ID")]
		public int WarehouseId { get; set; }

		[SwaggerSchema("Item location")]
		public string Location { get; set; }

		[SwaggerSchema("Stock quantity")]
		public int Stock { get; set; }

		[SwaggerSchema("Item URL")]
		public string Url { get; set; }

		[SwaggerSchema("Weight per unit")]
		public decimal WeightPerUnit { get; set; }
	}
}
