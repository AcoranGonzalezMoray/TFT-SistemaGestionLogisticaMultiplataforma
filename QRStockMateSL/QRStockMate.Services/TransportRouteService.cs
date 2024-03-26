using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Repositories;
using QRStockMate.AplicationCore.Interfaces.Services;

namespace QRStockMate.Services {
	public class TransportRouteService : BaseService<TransportRoute>, ITransportRouteService {
		private readonly ITransportRouteRepository _transportRouteRepository;
		public TransportRouteService(IBaseRepository<TransportRoute> _Repository, ITransportRouteRepository transportRouteRepository) : base(_Repository) {
			_transportRouteRepository = transportRouteRepository;
		}

		public async Task<DateTime> FinishRoute(int id) {
			return await _transportRouteRepository.FinishRoute(id);
		}

		public async Task<IEnumerable<TransportRoute>> GetTransportRoutesByCode(string code) {
			return await _transportRouteRepository.GetTransportRoutesByCode(code);
		}

		public async Task<DateTime> InitRoute(int id) {
			return await _transportRouteRepository.InitRoute(id);
		}
	}
}
