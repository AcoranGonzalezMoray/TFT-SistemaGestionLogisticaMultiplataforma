using QRStockMate.AplicationCore.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace QRStockMate.AplicationCore.Interfaces.Repositories
{
    public interface IWarehouseRepository: IBaseRepository<Warehouse>
    {
        public Task AddItem(int Id, Item Item);
        public Task<IEnumerable<Item>> GetItems(int Id);
        public Task<User> GetAdministrator(int Id);
        public Task<String> GetLocation(int Id);
        public Task<String> GetOrganization(int Id);
        public Task<String> GetName(int Id);
    }
}
