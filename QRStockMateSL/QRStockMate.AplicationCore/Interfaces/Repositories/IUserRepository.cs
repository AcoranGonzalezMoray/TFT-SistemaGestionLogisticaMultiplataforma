
using QRStockMate.AplicationCore.Entities;

namespace QRStockMate.AplicationCore.Interfaces.Repositories
{
    public interface IUserRepository:IBaseRepository<User>
    {
        public Task<Company> getCompany(string  code);
        public Task DeleteAccount(string code);
        public Task<User> getDirectorByCode(string code);
        public Task<User> getUserByEmailPassword(string email, string password);
    }
}
