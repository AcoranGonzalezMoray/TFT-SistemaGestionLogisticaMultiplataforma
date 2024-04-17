using AutoMapper;
using EasyNetQ;
using FluentAssertions;
using Microsoft.AspNetCore.Mvc;
using Moq;
using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Services;
using QRStockMate.Controller;

namespace QRStockMate.Test {

	public class VehicleControllerShould {
		private Mock<IVehicleService> _vehicleServiceMock;
		private Mock<IMapper> _mapperMock;
		private VehicleController _controller;

		[SetUp]
		public void SetUp() {
			_vehicleServiceMock = new Mock<IVehicleService>();
			_mapperMock = new Mock<IMapper>();
			_controller = new VehicleController(_mapperMock.Object, _vehicleServiceMock.Object, RabbitHutch.CreateBus("host=localhost;username=guest;password=guest"));
		}

		[Test]
		public async Task UpdateLocation_Returns_NoContent_If_Vehicle_Found() {
			// Arrange
			var vehicleId = 1;
			var location = "New Location";
			var vehicle = new Vehicle { Id = vehicleId };
			_vehicleServiceMock.Setup(x => x.GetById(vehicleId)).ReturnsAsync(vehicle);

			// Act
			var response = await _controller.UpdateLocation(vehicleId, location);

			// Assert
			response.Should().BeOfType<NoContentResult>();
		}

		[Test]
		public async Task UpdateLocation_Returns_NotFound_If_Vehicle_Not_Found() {
			// Arrange
			var vehicleId = 1;
			var location = "New Location";
			_vehicleServiceMock.Setup(x => x.GetById(vehicleId)).ReturnsAsync((Vehicle)null);

			// Act
			var response = await _controller.UpdateLocation(vehicleId, location);

			// Assert
			response.Should().BeOfType<NotFoundResult>();
		}

		[Test]
		public async Task GetLocation_Returns_OkResult_If_Vehicle_Found() {
			// Arrange
			var vehicleId = 1;
			var vehicle = new Vehicle { Id = vehicleId };
			_vehicleServiceMock.Setup(x => x.GetById(vehicleId)).ReturnsAsync(vehicle);

			// Act
			var response = await _controller.GetLocation(vehicleId);

			// Assert
			response.Result.Should().BeOfType<OkObjectResult>();
		}

		[Test]
		public async Task GetLocation_Returns_NotFound_If_Vehicle_Not_Found() {
			// Arrange
			var vehicleId = 1;
			_vehicleServiceMock.Setup(x => x.GetById(vehicleId)).ReturnsAsync((Vehicle)null);

			// Act
			var response = await _controller.GetLocation(vehicleId);

			// Assert
			response.Result.Should().BeOfType<NotFoundResult>();
		}
	}
}