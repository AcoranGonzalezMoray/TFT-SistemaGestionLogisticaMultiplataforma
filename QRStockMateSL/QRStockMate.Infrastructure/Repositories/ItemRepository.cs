﻿using Microsoft.EntityFrameworkCore;
using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Repositories;
using QRStockMate.Infrastructure.Data;

namespace QRStockMate.Infrastructure.Repositories {
	public class ItemRepository : BaseRepository<Item>, IItemRepository {
		private readonly ApplicationDbContext _context;

		public ItemRepository(ApplicationDbContext context) : base(context) {
			_context = context;
		}

		public async Task<IEnumerable<Item>> getItems(string name) {
			return await _context.Items.Where(a => a.Name.Contains(name)).ToListAsync();
		}
	}
}
