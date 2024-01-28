using QRStockMate.AplicationCore.Entities;

namespace QRStockMate.Model
{
	public class MessageModel
	{
		public int Id { get; set; }
		public string code { get; set; }
		public int SenderContactId { get; set; }
		public int ReceiverContactId { get; set; }
		public string Content { get; set; }
		public DateTime SentDate { get; set; }
		public TypeFile Type { get; set; }
	}
}
