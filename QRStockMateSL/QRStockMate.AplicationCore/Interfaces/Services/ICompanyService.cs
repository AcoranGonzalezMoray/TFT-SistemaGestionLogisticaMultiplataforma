using QRStockMate.AplicationCore.Entities;

namespace QRStockMate.AplicationCore.Interfaces.Services {
	public interface ICompanyService : IBaseService<Company> {
		public Task<IEnumerable<User>> getEmployees(string code);
		public Task<IEnumerable<Warehouse>> getWarehouses(string code);
		public Task<Company> getCompanyByCode(string code);
	}
}
