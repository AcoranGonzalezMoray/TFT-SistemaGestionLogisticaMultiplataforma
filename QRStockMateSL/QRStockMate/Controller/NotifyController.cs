using Asp.Versioning;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using QRStockMate.AplicationCore.Entities;
using QRStockMate.DTOs;
using Swashbuckle.AspNetCore.Annotations;
using System.Net.Mail;
using System.Net;
using System.Text.Json.Serialization;
using System.Web;
using System.Net.Mime;
using QRStockMate.Properties;
public class DefaultNotification {
	[JsonPropertyName("message")] public string? Message { get; set; }
}
namespace QRStockMate.Controller {
	[ApiVersion(1.0)]
	[Route("api/v{version:apiVersion}/[controller]")]
	[SwaggerTag("Endpoints related to notify management.")]
	[ApiController]
	public class NotifyController : ControllerBase {

		[AllowAnonymous]
		[SwaggerOperation(Summary = "Get all notifications", Description = "Retrieve all notifications.")]
		[SwaggerResponse(StatusCodes.Status200OK, "OK", typeof(string))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(string))]
		[HttpPost("email/"), MapToApiVersion(1.0)]
		public async Task<ActionResult<string>> ReceiveNotification(DefaultNotification defaultNotification) {
			try {
				EnviarCorreoElectronico("servidorplexacl@gmail.com", "Webhook QRStockmate notification received", GenerarCuerpoHTML(defaultNotification)); 
				return Ok("Notification received"); // 200 OK
			}
			catch (Exception ex) {
				return BadRequest(ex.Message); // 400 Bad Request
			}
		}
		private async Task EnviarCorreoElectronico(string destinatario, string asunto, string cuerpo) {
			using (var clienteSmtp = new SmtpClient()) {
				clienteSmtp.Host = "smtp.gmail.com"; // Reemplaza con el servidor SMTP que estás utilizando
				clienteSmtp.Port = 587; // Puerto del servidor SMTP (pueden variar dependiendo del proveedor)
				clienteSmtp.UseDefaultCredentials = false;
				clienteSmtp.Credentials = new NetworkCredential("servidorplexacl@gmail.com", Key.ApiKey);
				clienteSmtp.EnableSsl = true;

				ContentType mimeType = new ContentType("text/html");    
				string body = HttpUtility.HtmlDecode(cuerpo);

				AlternateView alternate = AlternateView.CreateAlternateViewFromString(cuerpo, mimeType);
				
				var mensaje = new MailMessage("servidorplexacl@gmail.com", destinatario, asunto, cuerpo);
				mensaje.AlternateViews.Add(alternate);
				await clienteSmtp.SendMailAsync(mensaje);
			}
		}

		private string GenerarCuerpoHTML(DefaultNotification notification) {
			var ruta = "https://cdn-icons-png.flaticon.com/512/5220/5220262.png";
			var cuerpoHTML = $"<!DOCTYPE html>\r\n<html lang=\"en\">\r\n<head>\r\n    <meta charset=\"UTF-8\">\r\n    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\r\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n    <title>Notificación de QRStockMate</title>\r\n</head>\r\n<body style=\"font-family: Arial, sans-serif;\">\r\n    <div style=\"max-width: 600px; margin: 0 auto; padding: 20px;\">\r\n        <table style=\"width: 100%;\">\r\n            <tr>\r\n                <td style=\"text-align: center;\">\r\n                    <img src={ruta} alt=\"Logo qrstockmate\" style=\"max-width: 200px;\">\r\n                </td>\r\n            </tr>\r\n            <tr>\r\n                <td style=\"text-align: center;\">\r\n                    <h1 style=\"margin-top: 20px;\">¡Notificación de QRStockMate recibida!</h1>\r\n                    <p style=\"font-size: 16px;\">Mensaje: {notification.Message}</p>\r\n                    <p style=\"font-size: 14px; color: #666;\">Enviado desde la API de qrstockmate {new DateTime()}</p>\r\n                </td>\r\n            </tr>\r\n        </table>\r\n    </div>\r\n</body>\r\n</html>\r\n";
			return cuerpoHTML;
		}
	}
}
