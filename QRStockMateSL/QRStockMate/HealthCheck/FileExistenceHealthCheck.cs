using Microsoft.Extensions.Diagnostics.HealthChecks;

namespace CoffeeMachine.Api.HealthCheck {
	public class FileExistenceHealthCheck(string filePath) : IHealthCheck {
		private readonly string _filePath = filePath ?? throw new ArgumentNullException(nameof(filePath));

		public Task<HealthCheckResult> CheckHealthAsync(HealthCheckContext context, CancellationToken cancellationToken = default) {
			try {
				return Task.FromResult(File.Exists(_filePath) ? HealthCheckResult.Healthy("File exist") : HealthCheckResult.Unhealthy("File does not exist"));
			}
			catch (Exception ex) {
				return Task.FromResult(HealthCheckResult.Unhealthy($"Error checking file existence: {ex.Message}"));
			}
		}
	}
}
