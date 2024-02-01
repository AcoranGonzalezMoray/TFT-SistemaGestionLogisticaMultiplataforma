using Firebase.Auth;
using Firebase.Storage;
using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Repositories;

namespace QRStockMate.Infrastructure.Repositories
{
    public class StorageRepository:IStorageRepository
    {
        private readonly string email = "qrstockmate@gmail.com";
        private readonly string clave = "qrstockmate";
        private readonly string ruta = "qrstockmate.appspot.com";
        private readonly string api_key = "AIzaSyBvcdWM5BeNdj9GWtZUV-iRPKmiRRQNNn0";

        public async Task DeleteImage(string name)
        {
            var auth = new FirebaseAuthProvider(new FirebaseConfig(api_key));
            var a = await auth.SignInWithEmailAndPasswordAsync(email, clave);

            var cancellation = new CancellationTokenSource();

            // Parsear la URL
            Uri uri = new Uri(name);

            // Obtener el nombre del archivo
            string fileName = System.IO.Path.GetFileName(uri.LocalPath);


            await new FirebaseStorage(
                ruta,
                new FirebaseStorageOptions
                {
                    AuthTokenAsyncFactory = () => Task.FromResult(a.FirebaseToken),
                    ThrowOnCancel = true
                })
                .Child("Fotos_Perfil")
                .Child(fileName)
                .DeleteAsync();
        }


        public async Task<string> UploadImage(Stream archivo, string name)
        {
            var auth = new FirebaseAuthProvider(new FirebaseConfig(api_key));
            var a = await auth.SignInWithEmailAndPasswordAsync(email, clave);

            var cancellation = new CancellationTokenSource();
            string nameNew = name + DateTime.Now.ToString().Replace("/", "_").Replace(" ", "_");
            var task = new FirebaseStorage(
                ruta,
                new FirebaseStorageOptions
                {
                    AuthTokenAsyncFactory = () => Task.FromResult(a.FirebaseToken),
                    ThrowOnCancel = true
                })
                .Child("Fotos_Perfil")
                .Child(nameNew)
                .PutAsync(archivo, cancellation.Token);


            var downloadURL = await task;


            return downloadURL;
        }


		public async Task DeleteFile(string name, TypeFile type)
		{
			var auth = new FirebaseAuthProvider(new FirebaseConfig(api_key));
			var a = await auth.SignInWithEmailAndPasswordAsync(email, clave);

			var cancellation = new CancellationTokenSource();

			string rute = "";

			switch (type)
			{
				case TypeFile.Audio:
					rute = "Audios";
					break;

				case TypeFile.Image:
					rute = "Images";
					break;

				case TypeFile.File:
					rute = "Files";
					break;
				default:
					rute = "Audios";
					break;
			}

			// Parsear la URL
			Uri uri = new Uri(name);

			// Obtener el nombre del archivo
			string fileName = System.IO.Path.GetFileName(uri.LocalPath);


			await new FirebaseStorage(
				ruta,
				new FirebaseStorageOptions
				{
					AuthTokenAsyncFactory = () => Task.FromResult(a.FirebaseToken),
					ThrowOnCancel = true
				})
				.Child(rute)
				.Child(fileName)
				.DeleteAsync();
		}


		public async Task<string> UploadFile(Stream archivo, string name, TypeFile type)
		{
			var auth = new FirebaseAuthProvider(new FirebaseConfig(api_key));
			var a = await auth.SignInWithEmailAndPasswordAsync(email, clave);


			string rute = "";

			switch (type)
			{
				case TypeFile.Audio:
					rute = "Audios";
					break;

				case TypeFile.Image:
					rute = "Images";
					break;

				case TypeFile.File:
					rute = "Files";
					break;
				default:
					rute = "Audios";
					break;
			}

			var cancellation = new CancellationTokenSource();
			string nameNew = DateTime.Now.ToString().Replace("/", "_").Replace(" ", "_") + "_" +name;
			var task = new FirebaseStorage(
				ruta,
				new FirebaseStorageOptions
				{
					AuthTokenAsyncFactory = () => Task.FromResult(a.FirebaseToken),
					ThrowOnCancel = true
				})
				.Child(rute)
				.Child(nameNew)
				.PutAsync(archivo, cancellation.Token);


			var downloadURL = await task;


			return downloadURL;
		}
	}
}
