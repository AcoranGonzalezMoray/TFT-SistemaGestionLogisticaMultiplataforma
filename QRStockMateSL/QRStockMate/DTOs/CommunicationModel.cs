using Swashbuckle.AspNetCore.Annotations;

namespace QRStockMate.DTOs {
	[SwaggerSchema("Communication model")]
	public class CommunicationModel {
		[SwaggerSchema("Communication ID")]
		public int Id { get; set; }

		[SwaggerSchema("Code associated with the communication")]
		public string Code { get; set; }

		[SwaggerSchema("Content of the communication")]
		public string Content { get; set; }

		[SwaggerSchema("Date and time when the communication was sent")]
		public DateTime SentDate { get; set; }
	}
}
