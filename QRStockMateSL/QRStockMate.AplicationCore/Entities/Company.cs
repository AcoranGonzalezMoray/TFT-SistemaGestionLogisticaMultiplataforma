using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace QRStockMate.AplicationCore.Entities
{
    public class Company
    {

        public int Id { get; set; }
        public string Name { get; set; }
        public string Director { get; set; }
        public string Location { get; set; }
        public string Code { get; set; }    //XXX-XXX
        public string WarehouseId { get; set; }   //algo;otro...
        public string EmployeeId { get; set; }   //algo;otro...

    }

}
