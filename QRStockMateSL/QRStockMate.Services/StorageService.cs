using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Repositories;
using QRStockMate.AplicationCore.Interfaces.Services;


namespace QRStockMate.Services
{
    public class StorageService : IStorageService
    {
        private readonly IStorageRepository _storageRepository;

        public StorageService(IStorageRepository storageRepository)
        {
            _storageRepository = storageRepository;
        }

        public async Task DeleteImage(string url)
        {
            await _storageRepository.DeleteImage(url);
        }

        public async Task<string> UploadImage(Stream archivo, string name)
        {
            return await _storageRepository.UploadImage(archivo, name);
        }

		public async Task DeleteFile(string url, TypeFile type)
		{
			await _storageRepository.DeleteFile(url, type);
		}

		public async Task<string> UploadFile(Stream archivo, string name, TypeFile type)
		{
			return await _storageRepository.UploadFile(archivo, name,type);
		}
	}
}
