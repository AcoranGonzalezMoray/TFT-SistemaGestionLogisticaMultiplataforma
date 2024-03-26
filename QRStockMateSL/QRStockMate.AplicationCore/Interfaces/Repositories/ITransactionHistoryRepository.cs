using QRStockMate.AplicationCore.Entities;

namespace QRStockMate.AplicationCore.Interfaces.Repositories {
	public interface ITransactionHistoryRepository : IBaseRepository<TransactionHistory> {
		public Task<IEnumerable<TransactionHistory>> GetTransactionHistoryByCode(string code);
	}
}
