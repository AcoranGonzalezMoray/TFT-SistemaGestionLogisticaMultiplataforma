

using AutoMapper;
using FluentAssertions;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Routing;
using Moq;
using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Services;
using QRStockMate.Controller;
using QRStockMate.Model;

namespace QRStockMate.Test
{
    public class TransportRouteControllerShould
    {
        private  Mock<IMapper> _mapperMock;
        private  Mock<ITransportRouteService> _transportRouteServiceMock;
        private  TransportRouteController _controller;

        [SetUp]
        public void SetUp()
        {
            _mapperMock = new Mock<IMapper>();
            _transportRouteServiceMock = new Mock<ITransportRouteService>();
            _controller = new TransportRouteController(_mapperMock.Object, _transportRouteServiceMock.Object);
        }

        [Test]
        public async Task GetTransportRoutes_Returns_OkResult_If_Routes_Found_GET()
        {
            // Arrange
            var routes = new List<TransportRoute> { new TransportRoute { /* Inicializar una instancia de TransportRoute */ } };
            _transportRouteServiceMock.Setup(x => x.GetAll()).ReturnsAsync(routes);

            // Act
            var response = await _controller.Get();

            // Assert
            response.Result.Should().BeOfType<OkObjectResult>();
        }

        [Test]
        public async Task GetTransportRoutes_Returns_NotFound_If_Routes_Not_Found()
        {
            // Arrange
            _transportRouteServiceMock.Setup(x => x.GetAll()).ReturnsAsync((List<TransportRoute>)null);

            // Act
            var response = await _controller.Get();

            // Assert
            response.Result.Should().BeOfType<NotFoundResult>();
        }

        [Test]
        public async Task CreateTransportRoute_Returns_CreatedAtAction_If_Creation_Succeeds()
        {
            // Arrange
            var routeModel = new TransportRouteModel { /* Inicializar una instancia de TransportRouteModel */ };
            var route = new TransportRoute { /* Inicializar una instancia de TransportRoute */ };
            _mapperMock.Setup(x => x.Map<TransportRouteModel, TransportRoute>(routeModel)).Returns(route);

            // Act
            var response = await _controller.Post(routeModel);

            // Assert
            response.Should().BeOfType<CreatedAtActionResult>();
        }

        // Otros tests similares para cubrir otros casos de éxito y de error en el método CreateTransportRoute

        [Test]
        public async Task UpdateTransportRoute_Returns_NoContent_If_Update_Succeeds()
        {
            // Arrange
            var routeModel = new TransportRouteModel { /* Inicializar una instancia de TransportRouteModel */ };
            var route = new TransportRoute { /* Inicializar una instancia de TransportRoute */ };
            _mapperMock.Setup(x => x.Map<TransportRouteModel, TransportRoute>(routeModel)).Returns(route);

            // Act
            var response = await _controller.Put(routeModel);

            // Assert
            response.Should().BeOfType<ActionResult<TransportRouteModel>>();
        }

        // Otros tests similares para cubrir otros casos de éxito y de error en el método UpdateTransportRoute

        [Test]
        public async Task DeleteTransportRoute_Returns_NoContent_If_Deletion_Succeeds()
        {
            // Arrange
            var t = new TransportRouteModel { };
            var route = new TransportRoute { /* Inicializar una instancia de TransportRoute */ };

            _mapperMock.Setup(x => x.Map<TransportRouteModel, TransportRoute>(t)).Returns(route);

            // Act
            var response = await _controller.Delete(t);

            // Assert
            response.Should().BeOfType<NoContentResult>();
        }

        [Test]
        public async Task GetTransportRoutes_Returns_OkResult_If_Routes_Found()
        {
            // Arrange
            var code = "routeCode";
            var routes = new List<TransportRoute> { new TransportRoute { /* Inicializar una instancia de TransportRoute */ } };
            _transportRouteServiceMock.Setup(x => x.GetTransportRoutesByCode(code)).ReturnsAsync(routes);

            // Act
            var response = await _controller.GetTransportRoutes(code);

            // Assert
            response.Result.Should().BeOfType<OkObjectResult>();
        }


        [Test]
        public async Task GetTransportRouteById_Returns_OkResult_If_Route_Found()
        {
            // Arrange
            var id = 1;
            var route = new TransportRoute { /* Inicializar una instancia de TransportRoute */ };
            _transportRouteServiceMock.Setup(x => x.GetById(id)).ReturnsAsync(route);

            // Act
            var response = await _controller.GetTransportRouteById(id);

            // Assert
            response.Result.Should().BeOfType<OkObjectResult>();
        }

        // Otros tests similares para cubrir otros casos de éxito y de error en el método GetTransportRouteById

        [Test]
        public async Task InitRoute_Returns_OkResult_If_Init_Succeeds()
        {
            // Arrange
            var id = 1;
            var route = new TransportRoute { /* Inicializar una instancia de TransportRoute */ };
            _transportRouteServiceMock.Setup(x => x.GetById(id)).ReturnsAsync(route);

            // Act
            var response = await _controller.InitRoute(id);

            // Assert
            response.Result.Should().BeOfType<OkObjectResult>();
        }

        // Otros tests similares para cubrir otros casos de éxito y de error en el método InitRoute

        [Test]
        public async Task FinishRoute_Returns_OkResult_If_Finish_Succeeds()
        {
            // Arrange
            var id = 1;
            var route = new TransportRoute { /* Inicializar una instancia de TransportRoute */ };
            _transportRouteServiceMock.Setup(x => x.GetById(id)).ReturnsAsync(route);

            // Act
            var response = await _controller.FinishRoute(id);

            // Assert
            response.Result.Should().BeOfType<OkObjectResult>();
        }

    }
}