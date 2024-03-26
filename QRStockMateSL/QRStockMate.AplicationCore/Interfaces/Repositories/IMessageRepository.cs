using QRStockMate.AplicationCore.Entities;

namespace QRStockMate.AplicationCore.Interfaces.Repositories {
	public interface IMessageRepository : IBaseRepository<Message> {
		public Task<IEnumerable<Message>> GetMessageByCode(string code);

	}
}
