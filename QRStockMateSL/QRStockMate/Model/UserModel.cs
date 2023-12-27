using QRStockMate.AplicationCore.Entities;

namespace QRStockMate.Model
{
    public class UserModel
    {
        public int Id { get; set; }
        public string Name { get; set; }
        public string Email { get; set; }
        public string Password { get; set; }
        public string Phone { get; set; }
        public string Code { get; set; }
        public string Url { get; set; }
        public RoleUser Role { get; set; }
    }
}
