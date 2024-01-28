﻿using System;
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
		Task<string> UploadAudio(Stream archivo, string name);
		Task DeleteAudio(string url);
	}
}
