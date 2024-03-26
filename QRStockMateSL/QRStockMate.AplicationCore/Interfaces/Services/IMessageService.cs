using QRStockMate.AplicationCore.Entities;

namespace QRStockMate.AplicationCore.Interfaces.Services {
	public interface IMessageService : IBaseService<Message> {
		public Task<IEnumerable<Message>> GetMessageByCode(string code);

	}
}
