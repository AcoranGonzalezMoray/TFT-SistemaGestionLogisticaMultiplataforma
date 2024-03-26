using AutoMapper;
using Azure;
using FluentAssertions;
using Microsoft.AspNetCore.Mvc;
using Moq;
using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Repositories;
using QRStockMate.AplicationCore.Interfaces.Services;
using QRStockMate.Controller;
using QRStockMate.DTOs;
using QRStockMate.Services;
using System.Collections.Generic;
using System.Dynamic;
using static Microsoft.EntityFrameworkCore.DbLoggerCategory.Database;

namespace QRStockMate.Test
{
    public class CompanyControllerShould
    {
        private Mock<ICompanyService> _companyServiceMock;
        private Mock<IVehicleService> _vehicleServiceMock;
        private Mock<IMapper> _mapperMock;

        private CompanyController _controller;

        [SetUp]
        public void Setup()
        {
            _companyServiceMock = new Mock<ICompanyService>();
            _vehicleServiceMock = new Mock<IVehicleService>();
            _mapperMock = new Mock<IMapper>();

            _controller = new CompanyController(_companyServiceMock.Object, _vehicleServiceMock.Object, _mapperMock.Object);
        }

        [Test]
        public async Task GetEmployees_Returns_NotFound_If_No_Employees_Found()
        {
            // Arrange
            var company = new Company { Code = "companyCode" };
            _companyServiceMock.Setup(x => x.getEmployees(company.Code)).ReturnsAsync((IEnumerable<User>)null);

            // Act
            var response = await _controller.GetEmployees(company);

            // Assert
            response.Result.Should().BeOfType<NotFoundResult>();
        }

        [Test]
        public async Task GetEmployees_Returns_OkResult_If_Employees_Found()
        {
            // Arrange
            var company = new Company { Code = "companyCode" };
            var users = new List<User>();
            _companyServiceMock.Setup(x => x.getEmployees(company.Code)).ReturnsAsync(users);

            // Act
            var response = await _controller.GetEmployees(company);

            // Assert
            response.Result.Should().BeOfType<OkObjectResult>();
        }

        [Test]
        public async Task GetVehicles_Returns_NotFound_If_No_Vehicles_Found()
        {
            // Arrange
            var code = "companyCode";
            _vehicleServiceMock.Setup(x => x.GetVehiclesByCode(code)).ReturnsAsync((IEnumerable<Vehicle>)null);

            // Act
            var response = await _controller.GetVehicles(code);

            // Assert
            response.Result.Should().BeOfType<NotFoundResult>();
        }

        [Test]
        public async Task GetVehicles_Returns_OkResult_If_Vehicles_Found()
        {
            // Arrange
            var code = "companyCode";
            var vehicles = new List<Vehicle>();

            _vehicleServiceMock.Setup(x => x.GetVehiclesByCode(code)).ReturnsAsync(vehicles);

            // Act
            var response = await _controller.GetVehicles(code);

            // Assert
            response.Result.Should().BeOfType<OkObjectResult>();
        }
        [Test]
        public async Task GetWarehouses_Returns_BadRequest_If_No_Warehouses_Exist_For_Company()
        {
            // Arrange
            var company = new Company { Code = "companyCode", WarehouseId = "" };

            // Act
            var response = await _controller.GetWarehouses(company);

            // Assert
            response.Result.Should().BeOfType<BadRequestObjectResult>();
        }

        // Test para el escenario donde no se encuentran almacenes para la empresa
        [Test]
        public async Task GetWarehouses_Returns_NotFound_If_No_Warehouses_Found()
        {
            // Arrange
            var company = new Company { Code = "companyCode", WarehouseId = "warehouseId" };
            _companyServiceMock.Setup(x => x.getWarehouses(company.Code)).ReturnsAsync((IEnumerable<Warehouse>)null);

            // Act
            var response = await _controller.GetWarehouses(company);

            // Assert
            response.Result.Should().BeOfType<NotFoundResult>();
        }

        // Test para el escenario donde se encuentran almacenes para la empresa
        [Test]
        public async Task GetWarehouses_Returns_OkResult_With_WarehouseModels()
        {
            // Arrange
            var company = new Company { Code = "companyCode", WarehouseId = "warehouseId" };
            var warehouses = new List<Warehouse> { /* Crear almacenes para la prueba */ };
            _companyServiceMock.Setup(x => x.getWarehouses(company.Code)).ReturnsAsync(warehouses);

            // Act
            var response = await _controller.GetWarehouses(company);

            // Assert
            response.Result.Should().BeOfType<OkObjectResult>();
            var okObjectResult = response.Result.As<OkObjectResult>();
            okObjectResult.Value.Should().BeEquivalentTo(_mapperMock.Object.Map<IEnumerable<Warehouse>, IEnumerable<WarehouseModel>>(warehouses));
        }
    }
}