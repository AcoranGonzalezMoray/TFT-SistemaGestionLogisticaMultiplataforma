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
    public class WarehouseService : BaseService<Warehouse>, IWarehouseService
    {
        private readonly IWarehouseRepository _WarehouseRepository;
        public WarehouseService(IBaseRepository<Warehouse> _Repository, IWarehouseRepository WarehouseRepository) : base(_Repository)
        {
            _WarehouseRepository = WarehouseRepository;
        }

        public async Task AddItem(int Id, Item Item)
        {
           await  _WarehouseRepository.AddItem(Id, Item);
        }

        public async Task<User> GetAdministrator(int Id)
        {
            return await _WarehouseRepository.GetAdministrator(Id);
        }

        public async Task<IEnumerable<Item>> GetItems(int Id)
        {
           return await _WarehouseRepository.GetItems(Id);
        }

        public async Task<string> GetLocation(int Id)
        {
            return await _WarehouseRepository.GetLocation(Id);
        }

        public Task<string> GetName(int Id)
        {
            return _WarehouseRepository.GetName(Id);
        }

        public Task<string> GetOrganization(int Id)
        {
            return _WarehouseRepository.GetOrganization(Id);
        }
    }
}
