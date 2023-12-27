using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace QRStockMate.AplicationCore.Entities
{
    public class Item
    {
        public int Id { get; set; }

        public string Name { get; set; }

        public int WarehouseId { get; set; }

        public string Location { get; set; }

        public int Stock { get; set; }

        public string Url { get; set; }

		[Column(TypeName = "decimal(7,2)")]
		public decimal Weight { get; set; }
    }
}
