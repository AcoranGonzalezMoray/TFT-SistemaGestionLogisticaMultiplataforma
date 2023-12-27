using QRStockMate.AplicationCore.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace QRStockMate.AplicationCore.Interfaces.Repositories
{
    public interface ITransactionHistoryRepository : IBaseRepository<TransactionHistory>
    {
        public Task<IEnumerable<TransactionHistory>> GetTransactionHistoryByCode(string code);
    }
}
