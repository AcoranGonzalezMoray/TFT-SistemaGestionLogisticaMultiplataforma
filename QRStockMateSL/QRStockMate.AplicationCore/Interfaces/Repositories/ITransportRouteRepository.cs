using QRStockMate.AplicationCore.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace QRStockMate.AplicationCore.Interfaces.Repositories
{
	public interface ITransportRouteRepository: IBaseRepository<TransportRoute>
	{
		public Task<IEnumerable<TransportRoute>> GetTransportRoutesByCode(string code);

		public Task<DateTime> InitRoute(int id);
		public Task<DateTime> FinishRoute(int id);
	}
}
