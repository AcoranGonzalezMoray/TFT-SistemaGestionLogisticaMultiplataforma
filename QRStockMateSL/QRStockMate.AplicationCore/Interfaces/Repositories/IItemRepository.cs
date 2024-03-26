using QRStockMate.AplicationCore.Entities;

namespace QRStockMate.AplicationCore.Interfaces.Repositories {
	public interface IItemRepository : IBaseRepository<Item> {

		public Task<IEnumerable<Item>> getItems(string name);

		//Aún no hay más funciones específicas

	}
}
