﻿using Microsoft.EntityFrameworkCore;
using QRStockMate.AplicationCore.Entities;

namespace QRStockMate.Infrastructure.Data {
	public class ApplicationDbContext : DbContext {

		public ApplicationDbContext(DbContextOptions<ApplicationDbContext> options) : base(options) {
		}

		public DbSet<User> Users { get; set; } = null!;
		public DbSet<Company> Companies { get; set; } = null!;

		public DbSet<Item> Items { get; set; } = null!;
		public DbSet<TransactionHistory> TransactionsHistory { get; set; } = null!;
		public DbSet<Warehouse> Warehouses { get; set; } = null!;

		public DbSet<Vehicle> Vehicles { get; set; } = null!;
		public DbSet<TransportRoute> TransportRoutes { get; set; } = null!;

		public DbSet<Message> Messages { get; set; } = null!;
		public DbSet<Communication> Communications { get; set; } = null!;

	}
}

