using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Repositories;
using QRStockMate.AplicationCore.Interfaces.Services;


namespace QRStockMate.Services
{
    public class UserService : BaseService<User>, IUserService
    {
        private readonly IUserRepository _userRepository;

        public UserService(IBaseRepository<User> _Repository,IUserRepository userRepository):base(_Repository)
        {
            _userRepository = userRepository;
        }

        public async Task DeleteAccount(string code)
        {
            await _userRepository.DeleteAccount(code);
        }

        public async Task<Company> getCompany(string code)
        {
          return  await _userRepository.getCompany(code);
        }

        public async Task<User> getDirectorByCode(string code)
        {
            return await _userRepository.getDirectorByCode(code);
        }

        public async  Task<User> getUserByEmailPassword(string email, string password)
        {
          return await _userRepository.getUserByEmailPassword(email, password);
        }
    }
}
