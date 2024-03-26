using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Repositories;
using QRStockMate.AplicationCore.Interfaces.Services;

namespace QRStockMate.Services {
	public class CompanyService : BaseService<Company>, ICompanyService {
		private readonly ICompanyRepository _companyRepository;
		public CompanyService(IBaseRepository<Company> _Repository, ICompanyRepository companyRepository) : base(_Repository) {
			_companyRepository = companyRepository;
		}

		public async Task<IEnumerable<Warehouse>> getWarehouses(string code) {
			return await _companyRepository.getWarehouses(code);
		}

		public async Task<IEnumerable<User>> getEmployees(string code) {
			return await _companyRepository.getEmployees(code);
		}

		public async Task<Company> getCompanyByCode(string code) {
			return await _companyRepository.getCompanyByCode(code);
		}
	}
}
