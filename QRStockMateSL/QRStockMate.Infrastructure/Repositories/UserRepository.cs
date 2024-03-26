using Microsoft.EntityFrameworkCore;
using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Repositories;
using QRStockMate.Infrastructure.Data;


namespace QRStockMate.Infrastructure.Repositories {
	public class UserRepository : BaseRepository<User>, IUserRepository {

		private readonly ApplicationDbContext _context;
		private readonly IStorageRepository _contextStorage;

		public UserRepository(ApplicationDbContext context, IStorageRepository storage) : base(context) {
			_context = context;
			_contextStorage = storage;
		}

		public async Task DeleteAccount(string code) {
			var users = await _context.Users.Where(d => d.Code == code).ToListAsync();
			var company = await _context.Companies.Where(d => d.Code == code).FirstOrDefaultAsync();

			if (company != null && users != null) {
				//Lista de Vehiculos
				var vehicles = await _context.Vehicles.Where(d => d.Code == code).ToListAsync();

				//Lista de Rutas
				var routes = await _context.TransportRoutes.Where(d => d.Code == code).ToListAsync();

				//Lista de Comunicaciones
				var communications = await _context.Communications.Where(d => d.Code == code).ToListAsync();

				//Lista de Mensajes
				var messages = await _context.Messages.Where(d => d.code == code).ToListAsync();


				//Lista de Almacenes
				var idWarehouse = company.WarehouseId;
				idWarehouse = idWarehouse.TrimEnd(';'); // Elimina el último punto y coma
				List<int> idWarehouseList = idWarehouse.Split(';').Select(int.Parse).ToList();

				var warehouses = await _context.Warehouses.Where(w => idWarehouseList.Contains(w.Id)).ToListAsync();

				//Lista de Articulos
				var idItems = "";
				foreach (var warehouse in warehouses) {
					idItems += warehouse.IdItems;
				}
				idItems = idItems.TrimEnd(';'); // Elimina el último punto y coma
				List<int> idItemsList = idItems.Split(';').Select(int.Parse).ToList();

				var items = await _context.Items.Where(w => idItemsList.Contains(w.Id)).ToListAsync();

				//Lista de Transacciones
				var transaction = await _context.TransactionsHistory.Where(w => w.Code == code).ToListAsync();


				//Borrado de Comunicaciones
				_context.Communications.RemoveRange(communications);


				//Borrado de Usuarios e imagenes
				foreach (var user in users) {
					if (user.Url != "") await _contextStorage.DeleteImage(user.Url);
				}

				_context.Users.RemoveRange(users);

				//Borrado de Vehiculos
				_context.Vehicles.RemoveRange(vehicles);

				//Borrado de Rutas
				_context.TransportRoutes.RemoveRange(routes);

				//Borrado de Mensjaes
				foreach (var message in messages) {
					if (Uri.IsWellFormedUriString(message.Content, UriKind.Absolute)) {
						// Es una URL válida, puedes proceder con la eliminación
						await _contextStorage.DeleteFile(message.Content, message.Type);
					}

				}

				_context.Messages.RemoveRange(messages);

				//Borrado de Transacciones
				_context.TransactionsHistory.RemoveRange(transaction);

				//Borrado de Articulos e imagenes
				foreach (var item in items) {
					if (Uri.IsWellFormedUriString(item.Url, UriKind.Absolute)) {
						// Es una URL válida, puedes proceder con la eliminación
						await _contextStorage.DeleteImage(item.Url);
					}

				}

				_context.Items.RemoveRange(items);

				//Borrado de Almacen
				foreach (var warehouse in warehouses) {
					if (Uri.IsWellFormedUriString(warehouse.Url, UriKind.Absolute)) {
						// Es una URL válida, puedes proceder con la eliminación
						await _contextStorage.DeleteImage(warehouse.Url);
					}

				}

				_context.Warehouses.RemoveRange(warehouses);

				//Borrado de Compañia
				_context.Companies.Remove(company);

				await _context.SaveChangesAsync();
			}
		}

		public async Task<Company> getCompany(string code) {
			return await _context.Companies.Where(d => d.Code == code).FirstOrDefaultAsync();
		}

		public async Task<User> getUserByEmailPassword(string email, string password) {
			return await _context.Users.Where(d => d.Email == email && d.Password == password).FirstOrDefaultAsync();
		}

		public async Task<User> getDirectorByCode(string code) {
			return await _context.Users.Where(d => d.Code == code && d.Role == RoleUser.Director).FirstOrDefaultAsync();
		}
	}
}
