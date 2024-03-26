using QRStockMate.AplicationCore.Entities;

namespace QRStockMate.AplicationCore.Interfaces.Services {
	public interface IItemService : IBaseService<Item> {

		public Task<IEnumerable<Item>> getItems(string name);

		//Aún no hay funciones específicas

	}
}
