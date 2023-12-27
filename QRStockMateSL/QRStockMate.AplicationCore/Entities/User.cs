using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace QRStockMate.AplicationCore.Entities
{
    public class User
    {
        public int Id { get; set; }
        public string Name { get; set; }
        public string Email { get; set; }
        public string Password { get; set; }
        public string Phone { get; set; }
        public string Code { get; set; }
        public string Url { get; set; }
        public RoleUser Role { get; set; }

    }

    public enum RoleUser
    { 
        Director,
        Administrator,
        InventoryTechnician,
        User
    }
}
