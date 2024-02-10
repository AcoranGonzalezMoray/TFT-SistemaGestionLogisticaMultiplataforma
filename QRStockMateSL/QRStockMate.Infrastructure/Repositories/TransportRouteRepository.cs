using Microsoft.EntityFrameworkCore;
using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Repositories;
using QRStockMate.AplicationCore.Interfaces.Services;
using QRStockMate.Infrastructure.Data;
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace QRStockMate.Infrastructure.Repositories
{

	public class TransportRouteRepository : BaseRepository<TransportRoute>, ITransportRouteRepository
	{
		private readonly ApplicationDbContext _context;
		private readonly IWarehouseRepository _warehouseRepository;
		private readonly IItemRepository _itemRepository;

		public TransportRouteRepository(ApplicationDbContext context, IWarehouseRepository warehouseRepository, IItemRepository itemRepository):base(context) 
		{
			_context = context;
			_warehouseRepository = warehouseRepository;
			_itemRepository = itemRepository;
		}

		public async Task<DateTime> FinishRoute(int id)
		{
			var route = await this.GetById(id);

			route.ArrivalTime = DateTime.Now;
			route.Status = RoleStatus.Finalized;

			var listItem = route.Palets.Replace("[", "").Replace("]", "").Replace("{", "").Replace("}", "").Split(";").ToList();
			Console.WriteLine("Lista de items:");

			for (int i = 0; i < listItem.Count; i++)
			{
				var item = listItem[i];
				Console.WriteLine($"Índice: {i}, Elemento: {item}");
				listItem[i] = item.Replace(",", "").Trim();
				Console.WriteLine($"Elemento actualizado: {listItem[i]}");
			}


			await MoveItem(listItem,int.Parse(route.StartLocation), int.Parse(route.EndLocation) );

			return route.ArrivalTime;
		}

		private async Task MoveItem(IEnumerable<string> listItem, int startId, int endId) {
            foreach (var item in listItem)
            {
				if (item.Contains("="))
				{
					var idItem = int.Parse(item.Split("=")[0]);
					var stockItem = int.Parse(item.Split("=")[1].Split(":")[1]);
					var itemResult = await _context.Items.Where(item => item.Id == idItem).FirstOrDefaultAsync();

					Console.WriteLine($"{idItem},{itemResult.Id}");
					if (itemResult != null)
					{
						itemResult.Stock = itemResult.Stock - stockItem;
						await _itemRepository.Update(itemResult);

						var newItem = new Item();
						newItem.Stock = stockItem;
						newItem.WeightPerUnit = itemResult.WeightPerUnit;
						newItem.Name = itemResult.Name;
						newItem.WarehouseId = endId;
						newItem.Location = "PLEASE UPDATE LOCATION";
						newItem.Url = "";

						//Proceso de añadir
						await _warehouseRepository.AddItem(endId, newItem);
					}
				}
			}
        }

		public async Task<IEnumerable<TransportRoute>> GetTransportRoutesByCode(string code)
		{
			var routes = await  _context.TransportRoutes.Where(r => r.Code == code).ToListAsync();
			return routes;
		}

		public async Task<DateTime> InitRoute(int id)
		{
			var route = await this.GetById(id);

			route.DepartureTime = DateTime.Now;
			route.Status = RoleStatus.OnRoute;

			await this.Update(route);

			return route.DepartureTime;
		}
	}
}