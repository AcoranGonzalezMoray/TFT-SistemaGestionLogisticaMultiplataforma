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
	public class VehicleRepository : BaseRepository<Vehicle>, IVehicleRepository
	{
		private readonly ApplicationDbContext _context;

		public VehicleRepository(ApplicationDbContext context) : base(context)
		{
			_context = context;
		}


		public async Task<IEnumerable<Vehicle>> GetVehiclesByCode(string code)
		{
			var vehicles = await _context.Vehicles.Where(v => v.Code== code).ToListAsync();

			return vehicles;
		}

	}
}
