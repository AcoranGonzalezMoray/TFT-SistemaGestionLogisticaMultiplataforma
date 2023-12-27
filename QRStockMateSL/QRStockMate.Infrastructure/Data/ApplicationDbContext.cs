using Microsoft.EntityFrameworkCore;
using QRStockMate.AplicationCore.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace QRStockMate.Infrastructure.Data
{
    public class ApplicationDbContext:DbContext
    {

        public ApplicationDbContext(DbContextOptions<ApplicationDbContext> options):base(options)
        {
        }

        public DbSet<User> Users{ get; set; } = null!;
        public DbSet<Company> Companies { get; set; } = null!;

        public DbSet<Item> Items { get; set; } = null!;
        public DbSet<TransactionHistory> TransactionsHistory { get; set; } = null!;
        public DbSet<Warehouse> Warehouses { get; set; } = null!;
    }
}

