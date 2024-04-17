using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace QRStockMate.AplicationCore.Interfaces.Services {

	public interface IConsumerService<T> {

		Task Consume(T message);
	}
}