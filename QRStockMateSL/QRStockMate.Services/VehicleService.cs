using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Repositories;
using QRStockMate.AplicationCore.Interfaces.Services;

namespace QRStockMate.Services {

	public class VehicleService : BaseService<Vehicle>, IVehicleService {
		private readonly IVehicleRepository _VehicleRepository;
		public VehicleService(IBaseRepository<Vehicle> _Repository, IVehicleRepository VehicleRepository) : base(_Repository) {
			_VehicleRepository = VehicleRepository;
		}


		public async Task<IEnumerable<Vehicle>> GetVehiclesByCode(string code) {
			return await _VehicleRepository.GetVehiclesByCode(code);
		}
	}
}
