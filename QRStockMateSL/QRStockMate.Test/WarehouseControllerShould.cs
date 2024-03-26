using AutoMapper;
using FluentAssertions;
using Microsoft.AspNetCore.Mvc;
using Moq;
using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Services;
using QRStockMate.Controller;
using QRStockMate.DTOs;

namespace QRStockMate.Test {
	public class WarehouseControllerShould {
		private WarehouseController _controller;
		private Mock<IWarehouseService> _warehouseServiceMock;
		private Mock<ICompanyService> _companyServiceMock;
		private Mock<IItemService> _itemServiceMock;
		private Mock<IStorageService> _contextStorageMock;
		private Mock<IMapper> _mapperMock;

		[SetUp]
		public void Setup() {
			_warehouseServiceMock = new Mock<IWarehouseService>();
			_companyServiceMock = new Mock<ICompanyService>();
			_itemServiceMock = new Mock<IItemService>();
			_contextStorageMock = new Mock<IStorageService>();
			_mapperMock = new Mock<IMapper>();

			_controller = new WarehouseController(
				_warehouseServiceMock.Object,
				_mapperMock.Object,
				_contextStorageMock.Object,
				_companyServiceMock.Object,
				_itemServiceMock.Object
			);
		}

		[Test]
		public async Task AddItem_Returns_CreatedAtAction_If_Warehouse_Found() {
			// Arrange
			var warehouseId = 1;
			var itemModel = new ItemModel { /* Inicializar una instancia de ItemModel */ };
			var item = new Item { /* Inicializar una instancia de Item */ };

			_warehouseServiceMock.Setup(x => x.GetById(warehouseId)).ReturnsAsync(new Warehouse());
			_mapperMock.Setup(x => x.Map<ItemModel, Item>(itemModel)).Returns(item);

			// Act
			var response = await _controller.AddItem(warehouseId, itemModel);

			// Assert
			response.Should().BeOfType<CreatedAtActionResult>();
		}

		[Test]
		public async Task AddItem_Returns_NotFoundResult_If_Warehouse_Not_Found() {
			// Arrange
			var warehouseId = 1;
			var itemModel = new ItemModel { /* Inicializar una instancia de ItemModel */ };
			var item = new Item { /* Inicializar una instancia de Item */ };

			_warehouseServiceMock.Setup(x => x.GetById(warehouseId)).ReturnsAsync((Warehouse)null);
			_mapperMock.Setup(x => x.Map<ItemModel, Item>(itemModel)).Returns(item);

			// Act
			var response = await _controller.AddItem(warehouseId, itemModel);

			// Assert
			response.Should().BeOfType<NotFoundResult>();
		}

		[Test]
		public async Task GetItems_Returns_OkResult_If_Warehouse_Found() {
			// Arrange
			var warehouseId = 1;
			var warehouse = new Warehouse { Id = warehouseId, IdItems = "12;" };
			var items = new List<Item> { /* Inicializar una lista de Item */ };

			_warehouseServiceMock.Setup(x => x.GetById(warehouseId)).ReturnsAsync(warehouse);
			_warehouseServiceMock.Setup(x => x.GetItems(warehouse.Id)).ReturnsAsync(items);
			_mapperMock.Setup(x => x.Map<IEnumerable<Item>, IEnumerable<ItemModel>>(items)).Returns(items.Select(i => new ItemModel { /* Configurar propiedades del ItemModel */ }));

			// Act
			var response = await _controller.GetItems(warehouseId);

			// Assert
			response.Result.Should().BeOfType<OkObjectResult>();
		}

		[Test]
		public async Task GetItems_Returns_NotFoundResult_If_Warehouse_Not_Found() {
			// Arrange
			var warehouseId = 1;
			var warehouse = new Warehouse { Id = 2, IdItems = "12;" };
			var items = new List<Item> { /* Inicializar una lista de Item */ };

			_warehouseServiceMock.Setup(x => x.GetById(warehouseId)).ReturnsAsync((Warehouse)null);
			_warehouseServiceMock.Setup(x => x.GetItems(warehouse.Id)).ReturnsAsync(items);
			_mapperMock.Setup(x => x.Map<IEnumerable<Item>, IEnumerable<ItemModel>>(items)).Returns(items.Select(i => new ItemModel { /* Configurar propiedades del ItemModel */ }));

			// Act
			var response = await _controller.GetItems(warehouseId);

			// Assert
			response.Result.Should().BeOfType<NotFoundResult>();
		}

		[Test]
		public async Task GetItems_Returns_BadRequest_If_WarehouseItems_Is_Null() {
			// Arrange
			var warehouseId = 1;
			var warehouse = new Warehouse { Id = 1, IdItems = "" };
			var items = new List<Item> { /* Inicializar una lista de Item */ };

			_warehouseServiceMock.Setup(x => x.GetById(warehouseId)).ReturnsAsync(warehouse);
			_warehouseServiceMock.Setup(x => x.GetItems(warehouse.Id)).ReturnsAsync((List<Item>)null);
			_mapperMock.Setup(x => x.Map<IEnumerable<Item>, IEnumerable<ItemModel>>(items)).Returns(items.Select(i => new ItemModel { /* Configurar propiedades del ItemModel */ }));

			// Act
			var response = await _controller.GetItems(warehouseId);

			// Assert
			response.Result.Should().BeOfType<BadRequestObjectResult>();
			var badRequestResult = response.Result.As<BadRequestObjectResult>();

			badRequestResult.Value.Should().Be("This warehouse don't have item yet.");

		}
	}
}