using System;
using Swashbuckle.AspNetCore.Annotations;
using QRStockMate.AplicationCore.Entities;

namespace QRStockMate.DTOs {
	[SwaggerSchema("Transport route model")]
	public class TransportRouteModel {
		[SwaggerSchema("ID of the transport route")]
		public int Id { get; set; }

		[SwaggerSchema("Code associated with the transport route")]
		public string Code { get; set; }

		[SwaggerSchema("Start location of the route")]
		public string StartLocation { get; set; }

		[SwaggerSchema("End location of the route")]
		public string EndLocation { get; set; }

		[SwaggerSchema("Departure time of the route")]
		public DateTime DepartureTime { get; set; }

		[SwaggerSchema("Arrival time of the route")]
		public DateTime ArrivalTime { get; set; }

		[SwaggerSchema("Packaging of a large number of products")]
		public string Palets { get; set; }

		[SwaggerSchema("ID of the vehicle assigned to the route")]
		public int AssignedVehicleId { get; set; }

		[SwaggerSchema("ID of the carrier (driver)")]
		public int CarrierId { get; set; }

		[SwaggerSchema("Date of the transport route")]
		public DateTime Date { get; set; }

		[SwaggerSchema("Status of the transport route")]
		public RoleStatus Status { get; set; }

		[SwaggerSchema("Route details")]
		public string Route { get; set; }
	}
}
