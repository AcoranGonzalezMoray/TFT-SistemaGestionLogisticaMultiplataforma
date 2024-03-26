namespace QRStockMate.AplicationCore.Interfaces.Repositories {
	public interface IJwtTokenRepository {
		string GenToken(string email, string passwd);

	}
}
