using EasyNetQ.AutoSubscribe;
using Newtonsoft.Json;
using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Repositories;
using QRStockMate.AplicationCore.Interfaces.Services;
using QRStockMate.DTOs;
using System.Text;

namespace QRStockMate.QueueService {

	public class VehicleLocationUpdatedConsumer : IConsumerService<Vehicle> {
		private readonly IVehicleService _vehicleService;

		public VehicleLocationUpdatedConsumer(IVehicleService vehicleService) {
			_vehicleService = vehicleService;
		}

		public async Task Consume(Vehicle vehicle) {
			try {
				var url = $"https://localhost:7220/api/v1/Vehicle/UpdateLocationQueue/{vehicle.Id}";

				var requestBody = JsonConvert.SerializeObject(vehicle.Location);

				var httpRequest = new HttpRequestMessage(HttpMethod.Put, url);
				httpRequest.Content = new StringContent(requestBody, Encoding.UTF8, "application/json");

				using (var httpClient = new HttpClient()) {
					var response = await httpClient.SendAsync(httpRequest);
					if (response.IsSuccessStatusCode) Console.WriteLine("Location updated successfully");
					else Console.WriteLine($"Failed to update location: {response.StatusCode}");
				}
			} catch (Exception ex) {
				// Manejar excepciones aquí
				Console.WriteLine($"Error processing message: {ex.Message}");
			}
		}
	}
}