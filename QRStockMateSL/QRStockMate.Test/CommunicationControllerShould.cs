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
    public class CommunicationControllerShould
    {
        private  Mock<IMapper> _mapperMock;
        private  Mock<ICommunicationService> _communicationServiceMock;
        private  CommunicationController _controller;

        [SetUp]
        public void  SetUp()
        {
            _mapperMock = new Mock<IMapper>();
            _communicationServiceMock = new Mock<ICommunicationService>();
            _controller = new CommunicationController(_mapperMock.Object, _communicationServiceMock.Object);
        }

        [Test]
        public async Task Get_Returns_OkResult_With_Communications()
        {
            // Arrange
            var communications = new List<Communication> { new Communication() };
            _communicationServiceMock.Setup(x => x.GetAll()).ReturnsAsync(communications);
            _mapperMock.Setup(x => x.Map<IEnumerable<Communication>, IEnumerable<CommunicationModel>>(communications))
                .Returns(new List<CommunicationModel> { new CommunicationModel() });

            // Act
            var response = await _controller.Get();

            // Assert
            response.Result.Should().BeOfType<OkObjectResult>();
        }

        [Test]
        public async Task Get_Returns_NotFound_When_No_Communications()
        {
            // Arrange
            _communicationServiceMock.Setup(x => x.GetAll()).ReturnsAsync((List<Communication>)null);

            // Act
            var response = await _controller.Get();

            // Assert
            response.Result.Should().BeOfType<NotFoundResult>();
        }

        [Test]
        public async Task Post_Returns_CreatedAtAction_When_Successful()
        {
            // Arrange
            var communicationModel = new CommunicationModel();
            var communication = new Communication();
            _mapperMock.Setup(x => x.Map<CommunicationModel, Communication>(communicationModel)).Returns(communication);

            // Act
            var response = await _controller.Post(communicationModel);

            // Assert
            response.Should().BeOfType<CreatedAtActionResult>();
        }

        [Test]
        public async Task Post_Returns_BadRequest_When_Exception_Occurs()
        {
            // Arrange
            var communicationModel = new CommunicationModel();
            _mapperMock.Setup(x => x.Map<CommunicationModel, Communication>(communicationModel)).Throws<Exception>();

            // Act
            var response = await _controller.Post(communicationModel);

            // Assert
            response.Should().BeOfType<BadRequestObjectResult>();
        }
    }
}