using Swashbuckle.AspNetCore.Annotations;

namespace QRStockMate.AplicationCore.Entities {
	[SwaggerSchema("Message entity")]
	public class Message {
		[SwaggerSchema("ID of the message")]
		public int Id { get; set; }

		[SwaggerSchema("Code associated with the message")]
		public string code { get; set; }

		[SwaggerSchema("ID of the sender contact")]
		public int SenderContactId { get; set; }

		[SwaggerSchema("ID of the receiver contact")]
		public int ReceiverContactId { get; set; }

		[SwaggerSchema("Content of the message")]
		public string Content { get; set; }

		[SwaggerSchema("Date and time when the message was sent")]
		public DateTime SentDate { get; set; }

		[SwaggerSchema("Type of file attached with the message")]
		public TypeFile Type { get; set; }
	}

	[SwaggerSchema("0:Text, 1: Audio, 2:File, 3:Image")]
	public enum TypeFile {
		Text,

		Audio,

		File,

		Image
	}
}
