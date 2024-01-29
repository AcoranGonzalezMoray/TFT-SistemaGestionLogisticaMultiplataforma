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
	public  class CommunicationService: BaseService<Communication>, ICommunicationService
	{
		private readonly ICommunicationRepository _communicationRepository;

		public CommunicationService(IBaseRepository<Communication> _Repository, ICommunicationRepository communicationRepository):base(_Repository)
		{
			_communicationRepository = communicationRepository;
		}

		public async Task<IEnumerable<Communication>> GetCommunicationsByCode(string code)
		{
			return await _communicationRepository.GetCommunicationsByCode(code);
		}
	}
}
