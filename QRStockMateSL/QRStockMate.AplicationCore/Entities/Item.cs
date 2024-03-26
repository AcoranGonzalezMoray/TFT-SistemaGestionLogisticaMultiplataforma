using System.ComponentModel.DataAnnotations.Schema;

namespace QRStockMate.AplicationCore.Entities {
	public class Item {
		public int Id { get; set; }

		public string Name { get; set; }

		public int WarehouseId { get; set; }

		public string Location { get; set; }

		public int Stock { get; set; }

		public string Url { get; set; }

		[Column(TypeName = "decimal(7,2)")]
		public decimal WeightPerUnit { get; set; }
	}
}
