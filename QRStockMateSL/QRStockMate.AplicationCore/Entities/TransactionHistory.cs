using Swashbuckle.AspNetCore.Annotations;
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

	[SwaggerSchema("0: Add, 1: Post, 2: Put, 3: Delete")]
	public enum OperationHistory { 
        Add,
        Post,
        Put,
        Delete
    }
}
