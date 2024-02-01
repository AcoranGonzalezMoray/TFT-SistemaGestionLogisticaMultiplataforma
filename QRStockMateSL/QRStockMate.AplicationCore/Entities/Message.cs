using System;
using System.Collections.Generic;
using System.Diagnostics.Contracts;
using System.Linq;
using System.Reflection.Metadata;
using System.Text;
using System.Threading.Tasks;

namespace QRStockMate.AplicationCore.Entities
{
	public class Message
	{
		public int Id { get; set; }
		public string code { get; set; }
		public int SenderContactId { get; set; }
		public int ReceiverContactId { get; set; }
		public string Content { get; set; }
		public DateTime SentDate { get; set; }
		public TypeFile Type { get; set; }
	}

	public enum TypeFile
	{
		Text,
		Audio,
		File,
		Image
	}
}
