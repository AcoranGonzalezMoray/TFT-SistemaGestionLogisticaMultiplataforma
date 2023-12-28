﻿using Microsoft.EntityFrameworkCore;
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
    public class TransactionHistoryRepository : BaseRepository<TransactionHistory>, ITransactionHistoryRepository
    {

        private readonly ApplicationDbContext _context;

        public TransactionHistoryRepository(ApplicationDbContext context):base(context)
        {
            _context = context;
        }

        public async Task<IEnumerable<TransactionHistory>> GetTransactionHistoryByCode(string code)
        {
            var transactionHistory = await _context.TransactionsHistory.Where(th=>th.Code == code).ToListAsync();

            return transactionHistory;
        }
    }
}
