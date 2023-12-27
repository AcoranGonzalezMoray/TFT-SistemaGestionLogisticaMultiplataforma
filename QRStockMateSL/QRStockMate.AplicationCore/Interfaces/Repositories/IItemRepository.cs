using QRStockMate.AplicationCore.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace QRStockMate.AplicationCore.Interfaces.Repositories
{
    public interface IItemRepository: IBaseRepository<Item>
    {

        public Task<IEnumerable<Item>> getItems(string name);

        //Aún no hay más funciones específicas

    }
}
