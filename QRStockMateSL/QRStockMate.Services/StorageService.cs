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

		public async Task DeleteAudio(string url)
		{
			await _storageRepository.DeleteAudio(url);
		}

		public async Task<string> UploadAudio(Stream archivo, string name)
		{
			return await _storageRepository.UploadAudio(archivo, name);
		}
	}
}
