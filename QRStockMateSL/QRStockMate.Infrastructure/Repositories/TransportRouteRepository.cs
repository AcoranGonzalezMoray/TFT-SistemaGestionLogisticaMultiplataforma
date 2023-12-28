using Microsoft.EntityFrameworkCore;
using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Repositories;
using QRStockMate.Infrastructure.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace QRStockMate.Infrastructure.Repositories
{

	public class TransportRouteRepository : BaseRepository<TransportRoute>, ITransportRouteRepository
	{
		private readonly ApplicationDbContext _context;
		public TransportRouteRepository(ApplicationDbContext context) : base(context)
		{
			_context = context;
		}

		public async Task<DateTime> FinishRoute(int id)
		{
			var route = await this.GetById(id);

			route.ArrivalTime = DateTime.Now;

			await this.Update(route);

			return route.ArrivalTime;
		}

		public async Task<IEnumerable<TransportRoute>> GetTransportRoutesByCode(string code)
		{
			var routes = await  _context.TransportRoutes.Where(r => r.Code == code).ToListAsync();
			return routes;
		}

		public async Task<DateTime> InitRoute(int id)
		{
			var route = await this.GetById(id);

			route.DepartureTime = DateTime.Now;

			await this.Update(route);

			return route.DepartureTime;
		}
	}
}