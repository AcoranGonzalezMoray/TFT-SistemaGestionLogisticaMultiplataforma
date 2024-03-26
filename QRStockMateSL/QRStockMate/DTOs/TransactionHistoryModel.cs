using System;
using Swashbuckle.AspNetCore.Annotations;
using QRStockMate.AplicationCore.Entities;

namespace QRStockMate.DTOs {
	[SwaggerSchema("Transaction history model")]
	public class TransactionHistoryModel {
		[SwaggerSchema("Transaction ID")]
		public int Id { get; set; }

		[SwaggerSchema("Name of the transaction")]
		public string Name { get; set; }

		[SwaggerSchema("Code associated with the transaction")]
		public string Code { get; set; }

		[SwaggerSchema("Description of the transaction")]
		public string Description { get; set; }

		[SwaggerSchema("Date and time when the transaction was created")]
		public DateTime Created { get; set; }

		[SwaggerSchema("Operation history associated with the transaction")]
		public OperationHistory Operation { get; set; }
	}
}
