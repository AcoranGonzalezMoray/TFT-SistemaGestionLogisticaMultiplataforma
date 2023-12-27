
using QRStockMate.AplicationCore.Entities;

namespace QRStockMate.AplicationCore.Interfaces.Services
{
    public interface IUserService:IBaseService<User>
    {
        public Task<Company> getCompany(string code);
        public Task DeleteAccount(string code);
        public Task<User> getDirectorByCode(string code);
        public Task<User> getUserByEmailPassword(string email, string password);
    }
}
