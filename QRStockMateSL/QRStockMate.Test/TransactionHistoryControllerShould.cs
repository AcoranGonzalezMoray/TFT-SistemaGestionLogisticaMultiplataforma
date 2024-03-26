using AutoMapper;
using FluentAssertions;
using Microsoft.AspNetCore.Mvc;
using Moq;
using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Services;
using QRStockMate.Controller;
using QRStockMate.DTOs;

namespace QRStockMate.Test {
	public class TransactionHistoryControllerShould {
		private Mock<ITransactionHistoryService> _transactionHistoryServiceMock;
		private Mock<IMapper> _mapperMock;
		private TransactionHistoryController _controller;

		[SetUp]
		public void Setup() {
			_transactionHistoryServiceMock = new Mock<ITransactionHistoryService>();
			_mapperMock = new Mock<IMapper>();
			_controller = new TransactionHistoryController(_transactionHistoryServiceMock.Object, _mapperMock.Object);
		}

		[Test]
		public async Task Get_Returns_OkResult_With_TransactionHistoryModels() {
			// Arrange
			var transactionHistories = new List<TransactionHistory> { new TransactionHistory() };
			_transactionHistoryServiceMock.Setup(x => x.GetAll()).ReturnsAsync(transactionHistories);
			_mapperMock.Setup(x => x.Map<IEnumerable<TransactionHistory>, IEnumerable<TransactionHistoryModel>>(transactionHistories))
				.Returns(new List<TransactionHistoryModel> { new TransactionHistoryModel() });

			// Act
			var response = await _controller.Get();

			// Assert
			response.Result.Should().BeOfType<OkObjectResult>();
		}

		[Test]
		public async Task Get_Returns_NotFound_When_No_TransactionHistories() {
			// Arrange
			_transactionHistoryServiceMock.Setup(x => x.GetAll()).ReturnsAsync((List<TransactionHistory>)null);

			// Act
			var response = await _controller.Get();

			// Assert
			response.Result.Should().BeOfType<NotFoundResult>();
		}

		// Add more tests for Post, Put, and Delete methods

		[Test]
		public async Task GetHistory_Returns_OkResult_With_TransactionHistoryModels() {
			// Arrange
			var code = "testCode";
			var transactionHistories = new List<TransactionHistory> { new TransactionHistory() };
			_transactionHistoryServiceMock.Setup(x => x.GetTransactionHistoryByCode(code)).ReturnsAsync(transactionHistories);
			_mapperMock.Setup(x => x.Map<IEnumerable<TransactionHistory>, IEnumerable<TransactionHistoryModel>>(transactionHistories))
				.Returns(new List<TransactionHistoryModel> { new TransactionHistoryModel() });

			// Act
			var response = await _controller.GetHistory(code);

			// Assert
			response.Result.Should().BeOfType<OkObjectResult>();
		}

		[Test]
		public async Task GetHistory_Returns_NotFound_When_No_TransactionHistories() {
			// Arrange
			var code = "testCode";
			_transactionHistoryServiceMock.Setup(x => x.GetTransactionHistoryByCode(code)).ReturnsAsync((List<TransactionHistory>)null);

			// Act
			var response = await _controller.GetHistory(code);

			// Assert
			response.Result.Should().BeOfType<NotFoundResult>();
		}
	}
}