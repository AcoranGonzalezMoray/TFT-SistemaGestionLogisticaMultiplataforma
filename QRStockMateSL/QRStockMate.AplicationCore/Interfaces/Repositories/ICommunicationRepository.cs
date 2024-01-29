using QRStockMate.AplicationCore.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace QRStockMate.AplicationCore.Interfaces.Repositories
{
	public interface ICommunicationRepository: IBaseRepository<Communication>
	{
		public Task<IEnumerable<Communication>> GetCommunicationsByCode(string code);
	}
}
