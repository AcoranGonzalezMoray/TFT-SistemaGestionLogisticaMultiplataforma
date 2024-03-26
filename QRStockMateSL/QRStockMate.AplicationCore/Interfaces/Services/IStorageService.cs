using QRStockMate.AplicationCore.Entities;

namespace QRStockMate.AplicationCore.Interfaces.Services {
	public interface IStorageService {
		Task<string> UploadImage(Stream archivo, string name);
		Task DeleteImage(string url);
		Task<string> UploadFile(Stream archivo, string name, TypeFile type);
		Task DeleteFile(string url, TypeFile type);
	}
}
