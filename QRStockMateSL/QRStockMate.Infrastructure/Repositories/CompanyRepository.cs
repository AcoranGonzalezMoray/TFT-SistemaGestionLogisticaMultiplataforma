using Microsoft.EntityFrameworkCore;
using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Repositories;
using QRStockMate.Infrastructure.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace QRStockMate.Infrastructure.Repositories
{
    public class CompanyRepository : BaseRepository<Company>, ICompanyRepository
    {

        private readonly ApplicationDbContext _context;
        public CompanyRepository(ApplicationDbContext context) : base(context)
        {
            _context = context;
        }

        public async Task<Company> getCompanyByCode(string code)
        {
            return await _context.Companies.Where(a => a.Code == code).FirstOrDefaultAsync();
        }

        public async Task<IEnumerable<User>> getEmployees(string code)
        {
            
            return await _context.Users.Where(a => a.Code == code).ToListAsync();
        }

        public async  Task<IEnumerable<Warehouse>> getWarehouses(string code)
        {
            // 6;7;8;2;
            var company = await this.getCompanyByCode(code);
            var idWarehouse= company.WarehouseId;
            idWarehouse = idWarehouse.TrimEnd(';'); // Elimina el último punto y coma
            List<int> idList = idWarehouse.Split(';').Select(int.Parse).ToList();
            // Imprimir la lista de enteros en la consola
            Console.WriteLine("Lista de enteros:");
            foreach (int id in idList)
            {
                Console.WriteLine(id);
            }

            var warehouses= await _context.Warehouses.Where(w => idList.Contains(w.Id)).ToListAsync();

            return warehouses;
        }
    }
}
 