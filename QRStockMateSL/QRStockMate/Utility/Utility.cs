using System.Security.Cryptography;
using System.Text;

namespace QRStockMate.Utility
{
    public class Utility
    {
        public static string EncriptarClave(string clave)
        {
            StringBuilder sb = new StringBuilder();

            using (SHA256 hash = SHA256.Create())
            {

                Encoding enc = Encoding.UTF8;
                byte[] result = hash.ComputeHash(enc.GetBytes(clave));

                foreach (byte b in result)
                {
                    sb.Append(b.ToString("x2"));
                }
            }

            return sb.ToString();
        }

        public static string GenerateCode()
        {
            // Generar un GUID único y formatearlo como una cadena
            Guid uniqueGuid = Guid.NewGuid();
            string uniqueString = uniqueGuid.ToString().ToUpper();

            // Tomar los primeros 6 caracteres del GUID (o los caracteres que desees)
            string formattedString = uniqueString.Substring(0, 6);

            // Formato 'XXX-XXX' o el formato que necesites
            return $"{formattedString.Substring(0, 3)}-{formattedString.Substring(3)}";
        }
        public static string RemoveSpecificId(string originalString, int idToRemove)
        {
            // Elimina espacios en blanco alrededor de los números
            originalString = originalString.Trim();

            // Divide la cadena original en una lista de IDs
            List<int> idList = originalString.Split(';')
                                            .Where(s => !string.IsNullOrWhiteSpace(s))
                                            .Select(int.Parse)
                                            .ToList();

            // Elimina el ID específico de la lista
            idList.Remove(idToRemove);

            // Convierte la lista actualizada de IDs de nuevo a una cadena
            string updatedString = string.Join(";", idList);
			if (updatedString != "")
			{
				updatedString += ";";
			}
			return updatedString;
        }



    }
}
