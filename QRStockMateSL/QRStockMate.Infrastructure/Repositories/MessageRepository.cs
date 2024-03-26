using Microsoft.EntityFrameworkCore;
using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Repositories;
using QRStockMate.Infrastructure.Data;

namespace QRStockMate.Infrastructure.Repositories {
	public class MessageRepository : BaseRepository<Message>, IMessageRepository {
		private readonly ApplicationDbContext _context;

		public MessageRepository(ApplicationDbContext context) : base(context) {
			_context = context;
		}

		public async Task<IEnumerable<Message>> GetMessageByCode(string code) {
			return await _context.Messages.Where(m => m.code == code).ToListAsync();
		}
	}
}
