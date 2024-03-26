using Swashbuckle.AspNetCore.Annotations;
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

	[SwaggerSchema("0: Director, 1: Administrator, 2: InventoryTechnician, 3: User, 4: Carrier")]
	public enum RoleUser
    { 
        Director,
        Administrator,
        InventoryTechnician,
        User,
		Carrier
	}
}
