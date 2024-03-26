using QRStockMate.AplicationCore.Entities;

namespace QRStockMate.AplicationCore.Interfaces.Services {
	public interface ICommunicationService : IBaseService<Communication> {
		public Task<IEnumerable<Communication>> GetCommunicationsByCode(string code);
	}
}
