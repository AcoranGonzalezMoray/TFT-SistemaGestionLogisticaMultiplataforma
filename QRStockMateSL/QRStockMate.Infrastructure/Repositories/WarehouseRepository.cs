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
    public class WarehouseRepository : BaseRepository<Warehouse>, IWarehouseRepository
    {
        private readonly ApplicationDbContext _context;
        private readonly IItemRepository _itemRepository;
        public WarehouseRepository(ApplicationDbContext context, IItemRepository itemRepository) : base(context)
        {
            _context = context;
            _itemRepository = itemRepository;
        }

        public async Task AddItem(int Id, Item Item)
        {
            var warehouse = await this.GetById(Id);

            Item.WarehouseId = warehouse.Id;
            await _itemRepository.Create(Item);

            warehouse.IdItems += $"{Item.Id};";

            await this.Update(warehouse);

        }

        public async Task<User> GetAdministrator(int WarehouseId)
        {
            var Warehouse = await _context.Warehouses.Where(w => w.Id == WarehouseId).FirstOrDefaultAsync();
            return await _context.Users.Where(u => u.Id == Warehouse.IdAdministrator).FirstOrDefaultAsync();
            
        }

        public async Task<IEnumerable<Item>> GetItems(int Id)
        {
            // 6;7;8;2;
            var warehouse = await this.GetById(Id);
            var idItems = warehouse.IdItems;
            idItems = idItems.TrimEnd(';'); // Elimina el último punto y coma
            List<int> idList = idItems.Split(';').Select(int.Parse).ToList();
            var items = await _context.Items.Where(item => idList.Contains(item.Id)).ToListAsync();

            return items;
        }

        public async Task<string> GetLocation(int WarehouseId)
        {
            var Warehouse = await _context.Warehouses.Where(w => w.Id == WarehouseId).FirstOrDefaultAsync();
            return Warehouse.Location;
        }

        public async Task<string> GetName(int WarehouseId)
        {
            var Warehouse = await _context.Warehouses.Where(w => w.Id == WarehouseId).FirstOrDefaultAsync();
            return Warehouse.Name;
        }

        public async Task<string> GetOrganization(int WarehouseId)
        {
            var Warehouse = await _context.Warehouses.Where(w => w.Id == WarehouseId).FirstOrDefaultAsync();
            return Warehouse.Organization;
        }
    }
}
