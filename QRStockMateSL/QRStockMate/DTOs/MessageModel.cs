using System;
using Swashbuckle.AspNetCore.Annotations;
using QRStockMate.AplicationCore.Entities;

namespace QRStockMate.DTOs {
	[SwaggerSchema("Message model")]
	public class MessageModel {
		[SwaggerSchema("Message ID")]
		public int Id { get; set; }

		[SwaggerSchema("Code associated with the message")]
		public string Code { get; set; }

		[SwaggerSchema("ID of the sender contact")]
		public int SenderContactId { get; set; }

		[SwaggerSchema("ID of the receiver contact")]
		public int ReceiverContactId { get; set; }

		[SwaggerSchema("Content of the message")]
		public string Content { get; set; }

		[SwaggerSchema("Date and time when the message was sent")]
		public DateTime SentDate { get; set; }

		[SwaggerSchema("Type of the message")]
		public TypeFile Type { get; set; }
	}
}
