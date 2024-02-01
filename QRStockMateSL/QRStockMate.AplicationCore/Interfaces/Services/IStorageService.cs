using QRStockMate.AplicationCore.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace QRStockMate.AplicationCore.Interfaces.Services
{
    public interface IStorageService
    {
        Task<string> UploadImage(Stream archivo, string name);
        Task DeleteImage(string url);
		Task<string> UploadFile(Stream archivo, string name, TypeFile type);
		Task DeleteFile(string url, TypeFile type);
	}
}
