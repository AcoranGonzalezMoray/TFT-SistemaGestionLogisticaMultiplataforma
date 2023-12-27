using Microsoft.EntityFrameworkCore;
using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Repositories;
using QRStockMate.Infrastructure.Data;


namespace QRStockMate.Infrastructure.Repositories
{
    public class UserRepository : BaseRepository<User>, IUserRepository
    {

        private readonly ApplicationDbContext _context;
        private readonly IStorageRepository _contextStorage;

        public UserRepository(ApplicationDbContext context, IStorageRepository storage) : base(context)
        {
            _context = context;
           _contextStorage = storage;
        }

        public async Task DeleteAccount(string code)
        {
            var users = await _context.Users.Where(d=>d.Code==code).ToListAsync();
            var company = await _context.Companies.Where(d => d.Code == code).FirstOrDefaultAsync();

            if (company != null && users != null)
            {
                //Borrado Usuarios
                foreach (var user in users)
                {
                     if(user.Url != "") await _contextStorage.DeleteImage(user.Url);
                }

                _context.Users.RemoveRange(users);

                //Lista de Almacenes
                var idWarehouse = company.WarehouseId;
                idWarehouse = idWarehouse.TrimEnd(';'); // Elimina el último punto y coma
                List<int> idWarehouseList = idWarehouse.Split(';').Select(int.Parse).ToList();

                var warehouses = await _context.Warehouses.Where(w => idWarehouseList.Contains(w.Id)).ToListAsync();

                //Lista de Articulos
                var idItems = "";
                foreach (var warehouse in warehouses)
                {
                    idItems += warehouse.IdItems;
                }
                idItems = idItems.TrimEnd(';'); // Elimina el último punto y coma
                List<int> idItemsList = idItems.Split(';').Select(int.Parse).ToList();

                var items = await _context.Items.Where(w => idItemsList.Contains(w.Id)).ToListAsync();

                //Lista de Transacciones
                var transaction = await _context.TransactionsHistory.Where(w => w.Code==code).ToListAsync();

                //Borrado de Transacciones
                _context.TransactionsHistory.RemoveRange(transaction);

                //Borrado de Articulos
                _context.Items.RemoveRange(items);

                //Borrado de Almacen
                _context.Warehouses.RemoveRange(warehouses);

                //Borrado de Compañia
                _context.Companies.Remove(company);

                await _context.SaveChangesAsync();
            }
        }

        public async Task<Company> getCompany(string code)
        {
            return await _context.Companies.Where(d => d.Code == code).FirstOrDefaultAsync();
        }

        public async Task<User> getUserByEmailPassword(string email, string password)
        {
            return await _context.Users.Where(d => d.Email == email && d.Password == password).FirstOrDefaultAsync();
        }

        public async Task<User> getDirectorByCode(string code)
        {
            return await _context.Users.Where(d=>d.Code==code && d.Role==RoleUser.Director).FirstOrDefaultAsync();
        }
    }
}
