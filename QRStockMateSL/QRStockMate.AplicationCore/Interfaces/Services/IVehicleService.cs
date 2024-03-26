using QRStockMate.AplicationCore.Entities;

namespace QRStockMate.AplicationCore.Interfaces.Services {
	public interface IVehicleService : IBaseService<Vehicle> {
		public Task<IEnumerable<Vehicle>> GetVehiclesByCode(string code);
	}
}
