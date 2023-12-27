using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Services;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace QRStockMate.AplicationCore.Interfaces.Services
{
    public interface ICompanyService:IBaseService<Company>
    {
        public Task<IEnumerable<User>> getEmployees(string code);
        public Task<IEnumerable<Warehouse>> getWarehouses(string code);
        public Task<Company> getCompanyByCode(string code);
    }
}
