using Microsoft.EntityFrameworkCore;
using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Repositories;
using QRStockMate.Infrastructure.Data;

namespace QRStockMate.Infrastructure.Repositories {
	public class CommunicationRepository : BaseRepository<Communication>, ICommunicationRepository {
		private readonly ApplicationDbContext _context;

		public CommunicationRepository(ApplicationDbContext context) : base(context) {
			_context = context;
		}

		public async Task<IEnumerable<Communication>> GetCommunicationsByCode(string code) {
			return await _context.Communications.Where(c => c.Code == code).ToListAsync();
		}
	}
}
