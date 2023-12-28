using System.ComponentModel.DataAnnotations.Schema;

namespace QRStockMate.Model
{
	public class VehicleModel
	{
		public int Id { get; set; }
		public string Code { get; set; }
		public string Make { get; set; }  // Fabricante (por ejemplo, Toyota, Ford, etc.)
		public string Model { get; set; }  // Modelo del vehículo
		public int Year { get; set; }  // Año de fabricación
		public string Color { get; set; }  // Color del vehículo
		public string LicensePlate { get; set; }  // Matrícula del vehículo
		public decimal MaxLoad { get; set; }  // Carga máxima del vehículo	
		public string Location { get; set; }  // Ubicacion cada cierto tiempo
	}
}
