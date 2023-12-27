using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace QRStockMate.AplicationCore.Entities
{
     public class TransactionHistory
    {
        public int Id { get; set; }
        public string Name { get; set; }
        public string Code { get; set; }
        public string Description { get; set; }
        public DateTime Created { get; set; }
        public OperationHistory Operation { get; set; }

    }

    public enum OperationHistory { 
        Add,
        Post,
        Put,
        Delete
    }
}
