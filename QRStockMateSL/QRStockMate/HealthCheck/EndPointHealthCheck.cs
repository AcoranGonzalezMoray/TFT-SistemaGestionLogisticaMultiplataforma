using Asp.Versioning;
using Microsoft.AspNetCore.Mvc.Controllers;
using Microsoft.AspNetCore.Mvc.Infrastructure;
using Microsoft.Extensions.Diagnostics.HealthChecks;
using System.Reflection;

namespace CoffeeMachine.Api.HealthCheck {
	public class EndPointHealthCheck(
		IActionDescriptorCollectionProvider actionDescriptorCollectionProvider,
		IHttpClientFactory httpClientFactory)
		: IHealthCheck {
		private readonly IActionDescriptorCollectionProvider _actionDescriptorCollectionProvider = actionDescriptorCollectionProvider ?? throw new ArgumentNullException(nameof(actionDescriptorCollectionProvider));
		private readonly HttpClient _httpClient = httpClientFactory.CreateClient();

		public async Task<HealthCheckResult> CheckHealthAsync(HealthCheckContext context, CancellationToken cancellationToken = default) {
			var endpoints = GetAllEndpoints();

			var unavailableEndpoints = new List<string>();

			foreach (var endpoint in endpoints) {
				if (!IsEndpointAvailable(endpoint)) {
					unavailableEndpoints.Add(endpoint);
				}
			}

			return unavailableEndpoints != null && unavailableEndpoints.Any()
				? HealthCheckResult.Unhealthy($"Some endpoints are not available (FAIL):{Environment.NewLine}{string.Join("\n", unavailableEndpoints)}")
				: HealthCheckResult.Healthy($"All endpoints are available (OK):{Environment.NewLine}{string.Join("\n", endpoints)}");

		}

		private List<string> GetAllEndpoints() {
			var endpoints = new List<string>();

			var actionDescriptors = _actionDescriptorCollectionProvider.ActionDescriptors.Items;

			foreach (var actionDescriptor in actionDescriptors) {
				if (!(actionDescriptor is ControllerActionDescriptor controllerActionDescriptor))
					continue;

				var routeTemplate = controllerActionDescriptor.AttributeRouteInfo?.Template;
				if (string.IsNullOrEmpty(routeTemplate))
					continue;
				var apiVersionAttributes = controllerActionDescriptor.ControllerTypeInfo.GetCustomAttributes<ApiVersionAttribute>();
				foreach (var apiVersionAttribute in apiVersionAttributes) {
					foreach (var version in apiVersionAttribute.Versions) {
						var endpoint = "https://localhost:7270/" + routeTemplate.Replace("v{version:apiVersion}", $"v{version}");
						if (!endpoints.Contains(endpoint)) {
							endpoints.Add(endpoint);
						}
					}
				}
			}

			return endpoints;
		}

		private bool IsEndpointAvailable(string endpoint) {
			// Obtener todos los descriptores de acción
			var actionDescriptors = _actionDescriptorCollectionProvider.ActionDescriptors.Items;

			// Verificar si hay algún descriptor de acción que coincida con el endpoint dado
			foreach (var actionDescriptor in actionDescriptors) {
				if (actionDescriptor is ControllerActionDescriptor controllerActionDescriptor) {
					var routeTemplate = controllerActionDescriptor.AttributeRouteInfo?.Template;
					if (!string.IsNullOrEmpty(routeTemplate)) {
						// Reemplazar el marcador de posición de versión con una versión específica para formar la ruta del endpoint
						var endpointWithVersion = endpoint.Replace("https://localhost:7270/", string.Empty);
						if (routeTemplate.Contains(endpointWithVersion.Replace("v1.0", "v{version:apiVersion}")) ||
							routeTemplate.Contains(endpointWithVersion.Replace("v2.0", "v{version:apiVersion}"))) {
							return true; // Se encontró una coincidencia, el endpoint está disponible
						}
					}
				}
			}

			return false; // No se encontró ninguna coincidencia, el endpoint no está disponible
		}

	}
}
