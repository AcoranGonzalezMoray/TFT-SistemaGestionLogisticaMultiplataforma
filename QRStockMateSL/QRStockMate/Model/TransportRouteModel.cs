namespace QRStockMate.Model
{
	public class TransportRouteModel
	{
		public int Id { get; set; }
		public string Code { get; set; }
		public string StartLocation { get; set; }  // Ubicación de inicio de la ruta
		public string EndLocation { get; set; }    // Ubicación de fin de la ruta
		public DateTime DepartureTime { get; set; } // Hora de salida
		public DateTime ArrivalTime { get; set; }   // Hora de llegada
		public string Palets { get; set; } //Empaquetado de gran numero de productos limit : [1;2;4;4;5,2;4;3;] (, palet) (; producto)
		public int AssignedVehicleId { get; set; } // Vehiculo asignados a la ruta
		public int CarrierId { get; set; } // Conductor
	}
}
