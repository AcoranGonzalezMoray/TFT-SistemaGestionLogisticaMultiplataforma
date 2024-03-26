using Microsoft.Extensions.Diagnostics.HealthChecks;
using System.Net;

namespace QRStockMate.HealthCheck {
	public class FirebaseHealthCheck : IHealthCheck {
		private readonly HttpClient _httpClient;

		public FirebaseHealthCheck(HttpClient httpClient) {
			_httpClient = httpClient ?? throw new ArgumentNullException(nameof(httpClient));
		}

		public async Task<HealthCheckResult> CheckHealthAsync(HealthCheckContext context, CancellationToken cancellationToken = default) {
			try {
				// Aquí deberías realizar la lógica para verificar la salud de Firebase
				// Por ejemplo, podrías hacer una solicitud HTTP a una URL específica en Firebase

				var response = await _httpClient.GetAsync("https://firebasestorage.googleapis.com/v0/b/qrstockmate.appspot.com/o", cancellationToken);

				// Verificar si la solicitud fue 403
				if (response.StatusCode == HttpStatusCode.Forbidden) {
					return HealthCheckResult.Healthy("Firebase is available.");
				}
				else {
					return HealthCheckResult.Unhealthy($"Firebase is unavailable. Status code: {response.StatusCode}");
				}
			}
			catch (Exception ex) {
				return HealthCheckResult.Unhealthy($"An error occurred while checking Firebase health: {ex.Message}");
			}
		}
	}
}
