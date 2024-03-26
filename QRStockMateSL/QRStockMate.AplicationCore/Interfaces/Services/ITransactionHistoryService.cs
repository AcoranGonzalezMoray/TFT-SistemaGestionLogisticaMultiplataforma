using QRStockMate.AplicationCore.Entities;

namespace QRStockMate.AplicationCore.Interfaces.Services {
	public interface ITransactionHistoryService : IBaseService<TransactionHistory> {
		public Task<IEnumerable<TransactionHistory>> GetTransactionHistoryByCode(string code);

	}
}
