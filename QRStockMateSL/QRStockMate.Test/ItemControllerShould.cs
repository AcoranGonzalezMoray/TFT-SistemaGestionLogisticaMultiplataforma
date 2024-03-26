

using AutoMapper;
using FluentAssertions;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Moq;
using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Services;
using QRStockMate.Controller;

namespace QRStockMate.Test {
	public class ItemControllerShould {
		private Mock<IItemService> _itemServiceMock;
		private Mock<IWarehouseService> _warehouseServiceMock;
		private Mock<IStorageService> _storageServiceMock;
		private Mock<IMapper> _mapperMock;
		private ItemController _controller;

		[SetUp]
		public void SetUp() {
			// Inicializar los mocks de las dependencias
			_itemServiceMock = new Mock<IItemService>();
			_warehouseServiceMock = new Mock<IWarehouseService>();
			_storageServiceMock = new Mock<IStorageService>();
			_mapperMock = new Mock<IMapper>();

			// Instanciar el controlador ItemController con los mocks de las dependencias
			_controller = new ItemController(_itemServiceMock.Object, _mapperMock.Object, _storageServiceMock.Object, _warehouseServiceMock.Object);
		}

		[Test]
		public async Task GetItemsByName_Returns_OkResult_If_Items_Found() {
			// Arrange
			string itemName = "exampleName";
			var items = new List<Item> { new Item { Name = itemName } };

			_itemServiceMock.Setup(x => x.getItems(itemName)).ReturnsAsync(items);

			// Act
			var response = await _controller.GetItemsByName(itemName);

			// Assert
			response.Result.Should().BeOfType<OkObjectResult>();
		}

		[Test]
		public async Task GetItemsByName_Returns_NotFound_If_No_Items_Found() {
			// Arrange
			string itemName = "nonExistentName";

			_itemServiceMock.Setup(x => x.getItems(itemName)).ReturnsAsync((IEnumerable<Item>)null);

			// Act
			var response = await _controller.GetItemsByName(itemName);

			// Assert
			response.Result.Should().BeOfType<NotFoundResult>();
		}

		[Test]
		public async Task UpdateImage_Returns_OkResult_If_Item_Found_And_Image_Updated_Successfully() {
			// Arrange
			int itemId = 1;
			var item = new Item { Id = itemId };
			var image = new FormFile(Stream.Null, 0, 0, "image", "example.jpg");
			string imageUrl = "https://example.com/image.jpg";

			_itemServiceMock.Setup(x => x.GetById(itemId)).ReturnsAsync(item);
			_storageServiceMock.Setup(x => x.DeleteImage(item.Url)).Returns(Task.CompletedTask);
			_storageServiceMock.Setup(x => x.UploadImage(It.IsAny<Stream>(), It.IsAny<string>())).ReturnsAsync(imageUrl);

			// Act
			var response = await _controller.UpdateImage(itemId, image);

			// Assert
			response.Should().BeOfType<OkResult>();
		}

		[Test]
		public async Task UpdateImage_Returns_NotFound_If_Item_Not_Found() {
			// Arrange
			int itemId = 1;
			var image = new FormFile(Stream.Null, 0, 0, "image", "example.jpg");

			_itemServiceMock.Setup(x => x.GetById(itemId)).ReturnsAsync((Item)null);

			// Act
			var response = await _controller.UpdateImage(itemId, image);

			// Assert
			response.Should().BeOfType<NotFoundResult>();
		}

		[Test]
		public async Task UpdateImage_Returns_BadRequest_If_Exception_Occurs() {
			// Arrange
			int itemId = 1;
			var item = new Item { Id = itemId };
			var image = new FormFile(Stream.Null, 0, 0, "image", "example.jpg");
			string errorMessage = "Error updating image";

			_itemServiceMock.Setup(x => x.GetById(itemId)).ReturnsAsync(item);
			_storageServiceMock.Setup(x => x.DeleteImage(item.Url)).Returns(Task.CompletedTask);
			_storageServiceMock.Setup(x => x.UploadImage(It.IsAny<Stream>(), It.IsAny<string>())).ThrowsAsync(new Exception(errorMessage));

			// Act
			var response = await _controller.UpdateImage(itemId, image);

			// Assert
			response.Should().BeOfType<BadRequestObjectResult>();
			((BadRequestObjectResult)response).Value.Should().Be(errorMessage);
		}
	}
}