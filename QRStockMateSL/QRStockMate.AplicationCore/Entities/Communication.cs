using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace QRStockMate.AplicationCore.Entities
{
	public class Communication
	{
		public int Id { get; set; }
		public string Code { get; set; }
		public string Content { get; set; }
		public DateTime SentDate { get; set; }
	}
}
