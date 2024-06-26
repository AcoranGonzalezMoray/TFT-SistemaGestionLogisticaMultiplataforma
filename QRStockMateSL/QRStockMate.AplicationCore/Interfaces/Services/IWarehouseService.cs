﻿using QRStockMate.AplicationCore.Entities;

namespace QRStockMate.AplicationCore.Interfaces.Services {
	public interface IWarehouseService : IBaseService<Warehouse> {
		public Task AddItem(int Id, Item Item);
		public Task<IEnumerable<Item>> GetItems(int Id);
		public Task<User> GetAdministrator(int Id);
		public Task<String> GetLocation(int Id);
		public Task<String> GetOrganization(int Id);
		public Task<String> GetName(int Id);
	}
}
