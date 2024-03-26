using QRStockMate.AplicationCore.Entities;

namespace QRStockMate.AplicationCore.Interfaces.Repositories {
	public interface ICompanyRepository : IBaseRepository<Company> {
		public Task<IEnumerable<Warehouse>> getWarehouses(string code);
		public Task<IEnumerable<User>> getEmployees(string code);
		public Task<Company> getCompanyByCode(string code);
	}
}
