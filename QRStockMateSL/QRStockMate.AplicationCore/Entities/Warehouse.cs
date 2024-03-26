namespace QRStockMate.AplicationCore.Entities {
	public class Warehouse {
		public int Id { get; set; }
		public string Name { get; set; }
		public string Location { get; set; }
		public string Organization { get; set; }
		public int IdAdministrator { get; set; }
		public string IdItems { get; set; }
		public string Url { get; set; }
		public double Latitude { get; set; }  // Propiedad para almacenar la latitud
		public double Longitude { get; set; } // Propiedad para almacenar la longitud
	}
}
