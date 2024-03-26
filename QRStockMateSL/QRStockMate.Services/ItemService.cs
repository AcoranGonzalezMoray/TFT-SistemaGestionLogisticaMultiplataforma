using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Repositories;
using QRStockMate.AplicationCore.Interfaces.Services;

namespace QRStockMate.Services {
	public class ItemService : BaseService<Item>, IItemService {
		private readonly IItemRepository _itemRepository;

		public ItemService(IBaseRepository<Item> _Repository, IItemRepository itemRepository) : base(_Repository) {
			_itemRepository = itemRepository;
		}

		public async Task<IEnumerable<Item>> getItems(string name) {
			return await _itemRepository.getItems(name);
		}
	}
}
