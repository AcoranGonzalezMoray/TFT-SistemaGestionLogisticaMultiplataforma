using QRStockMate.AplicationCore.Entities;

namespace QRStockMate.AplicationCore.Interfaces.Repositories {
	public interface ICommunicationRepository : IBaseRepository<Communication> {
		public Task<IEnumerable<Communication>> GetCommunicationsByCode(string code);
	}
}
