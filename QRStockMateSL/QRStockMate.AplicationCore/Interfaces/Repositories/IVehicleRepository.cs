using QRStockMate.AplicationCore.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace QRStockMate.AplicationCore.Interfaces.Repositories
{
	public interface IVehicleRepository:IBaseRepository<Vehicle>
	{
		public Task<IEnumerable<Vehicle>> GetVehiclesByCode(string code);
	}
}
