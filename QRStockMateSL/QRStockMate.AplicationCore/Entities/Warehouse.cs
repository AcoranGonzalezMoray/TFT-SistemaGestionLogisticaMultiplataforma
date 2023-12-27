using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace QRStockMate.AplicationCore.Entities
{
    public class Warehouse
    {
        public int Id { get; set; }
        public string Name { get; set; }
        public string Location { get; set; }
        public string Organization { get; set; }
        public int IdAdministrator { get; set; }
        public string IdItems{ get; set; }
        public string Url { get; set; }
    }
}
