using QRStockMate.AplicationCore.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace QRStockMate.AplicationCore.Interfaces.Repositories
{
	public interface IMessageRepository : IBaseRepository<Message>
	{
		public Task<IEnumerable<Message>> GetMessageByCode(string code);

	}
}
