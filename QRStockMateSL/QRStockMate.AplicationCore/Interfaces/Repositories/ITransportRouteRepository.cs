using QRStockMate.AplicationCore.Entities;

namespace QRStockMate.AplicationCore.Interfaces.Repositories {
	public interface ITransportRouteRepository : IBaseRepository<TransportRoute> {
		public Task<IEnumerable<TransportRoute>> GetTransportRoutesByCode(string code);

		public Task<DateTime> InitRoute(int id);
		public Task<DateTime> FinishRoute(int id);
	}
}
