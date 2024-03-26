using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Repositories;
using QRStockMate.AplicationCore.Interfaces.Services;

namespace QRStockMate.Services {
	public class TransactionHistoryService : BaseService<TransactionHistory>, ITransactionHistoryService {
		private readonly ITransactionHistoryRepository _transactionHistoryRepository;

		public TransactionHistoryService(IBaseRepository<TransactionHistory> _Repository, ITransactionHistoryRepository transactionHistoryRepository) : base(_Repository) {
			_transactionHistoryRepository = transactionHistoryRepository;
		}

		public async Task<IEnumerable<TransactionHistory>> GetTransactionHistoryByCode(string code) {
			return await _transactionHistoryRepository.GetTransactionHistoryByCode(code);
		}
	}
}
