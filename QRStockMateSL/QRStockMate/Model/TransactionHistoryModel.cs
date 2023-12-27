using QRStockMate.AplicationCore.Entities;

namespace QRStockMate.Model
{
    public class TransactionHistoryModel
    {
        public int Id { get; set; }
        public string Name { get; set; }
        public string Code { get; set; }
        public string Description { get; set; }
        public DateTime Created { get; set; }
        public OperationHistory Operation { get; set; }
    }
}
