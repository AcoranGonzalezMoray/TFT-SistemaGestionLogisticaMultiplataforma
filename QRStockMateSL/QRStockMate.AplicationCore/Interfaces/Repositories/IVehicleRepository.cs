using QRStockMate.AplicationCore.Entities;

namespace QRStockMate.AplicationCore.Interfaces.Repositories {
	public interface IVehicleRepository : IBaseRepository<Vehicle> {
		public Task<IEnumerable<Vehicle>> GetVehiclesByCode(string code);
	}
}
