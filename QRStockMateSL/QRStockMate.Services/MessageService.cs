using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Repositories;
using QRStockMate.AplicationCore.Interfaces.Services;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace QRStockMate.Services
{
	public class MessageService: BaseService<Message>, IMessageService
	{
		private readonly IMessageRepository _messageRepository;

		public MessageService(IBaseRepository<Message> _Repository, IMessageRepository messageRepository): base(_Repository) 
		{
			_messageRepository = messageRepository;
		}

		public async Task<IEnumerable<Message>> GetMessageByCode(string code)
		{
			return await _messageRepository.GetMessageByCode(code);
		}
	}
}
