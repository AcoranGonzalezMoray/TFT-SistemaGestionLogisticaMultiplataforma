using Microsoft.Extensions.Diagnostics.HealthChecks;
using Microsoft.IdentityModel.Tokens;
using System.Net;

namespace QRStockMate.HealthCheck {
	public class UrlHealthCheck : IHealthCheck {
		private readonly HttpClient _httpClient;
		private readonly List<(string Name, string Url)> _urlsToCheck = new List<(string, string)>{
			("HealthCheck Web", "http://localhost:4200"),
			("HealthCheck UI API", "https://localhost:7220/health-ui-api"),
			("HealthCheck UI", "https://localhost:7220/healthchecks-ui"),
		};
		public UrlHealthCheck() {
			_httpClient = new HttpClient();
		}
		public async Task<HealthCheckResult> CheckHealthAsync(
		   HealthCheckContext context,
		   CancellationToken cancellationToken = default) {
			var unavailableEndpoints = new List<string>();
			var availableEndpoints = new List<string>();

			foreach (var (name, url) in _urlsToCheck) {
				try {
					var response = await _httpClient.GetAsync(url, cancellationToken);
					if (!response.IsSuccessStatusCode && response.StatusCode != HttpStatusCode.Unauthorized) {
						unavailableEndpoints.Add($"{name}: {url} - Returned status code {response.StatusCode}");
					}
					else {
						if(availableEndpoints.IsNullOrEmpty()) availableEndpoints.Add($"\n{url.Replace("https://localhost:7270/", "")} - Code:{response.StatusCode}\n");
						else availableEndpoints.Add($"{url.Replace("https://localhost:7270/", "")} - Code:{response.StatusCode}\n");
					}
				}
				catch (Exception ex) {
					unavailableEndpoints.Add($"{name}: {url} - Check failed with error: {ex.Message}");
				}
			}

			if (unavailableEndpoints.Count > 0) {
				return HealthCheckResult.Unhealthy($"Some endpoints are not available (FAIL):{Environment.NewLine}{string.Join("\n", unavailableEndpoints)}");
			}
			else {
				return HealthCheckResult.Healthy($"All endpoints are available (OK):{Environment.NewLine}{string.Join("\n", availableEndpoints)}");
			}
		}
	}
}
