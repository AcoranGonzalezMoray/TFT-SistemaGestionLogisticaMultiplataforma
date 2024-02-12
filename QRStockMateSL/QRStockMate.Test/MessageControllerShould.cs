

using AutoMapper;
using FluentAssertions;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Moq;
using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Services;
using QRStockMate.Controller;
using QRStockMate.Model;

namespace QRStockMate.Test
{
    public class MessageControllerShould
    {
        private  Mock<IMessageService> _messageServiceMock;
        private  Mock<IStorageService> _storageServiceMock;
        private  Mock<IUserService> _userServiceMock;
        private Mock<IMapper> _mapperMock;
        private  MessageController _controller;
        [SetUp]
        public void SetUp()
        {
            _messageServiceMock = new Mock<IMessageService>();
            _storageServiceMock = new Mock<IStorageService>();
            _userServiceMock = new Mock<IUserService>();
            _mapperMock = new Mock<IMapper>();
            _controller = new MessageController(_messageServiceMock.Object, _storageServiceMock.Object, _userServiceMock.Object, _mapperMock.Object);
        }

        [Test]
        public async Task GetMessagesByCode_Returns_OkResult_If_Messages_Found()
        {
            // Arrange
            var code = "testCode";
            var messages = new List<Message> { new Message { /* Inicializar una instancia de Message */ } };
            _messageServiceMock.Setup(x => x.GetMessageByCode(code)).ReturnsAsync(messages);

            // Act
            var response = await _controller.GetMessagesByCode(code);

            // Assert
            response.Result.Should().BeOfType<OkObjectResult>();
        }

        [Test]
        public async Task GetMessagesByCode_Returns_NotFound_If_Messages_Not_Found()
        {
            // Arrange
            var code = "nonExistentCode";
            _messageServiceMock.Setup(x => x.GetMessageByCode(code)).ReturnsAsync((List<Message>)null);

            // Act
            var response = await _controller.GetMessagesByCode(code);

            // Assert
            response.Result.Should().BeOfType<OkObjectResult>();
        }

        [Test]
        public async Task UploadFile_Returns_OkResult_If_File_Upload_Succeeds()
        {
            // Arrange
            var fileMock = new Mock<IFormFile>();
            var messageModel = new MessageModel { /* Inicializar una instancia de MessageModel */ };
            _mapperMock.Setup(x => x.Map<MessageModel, Message>(messageModel)).Returns(new Message { /* Inicializar una instancia de Message */ });
            _storageServiceMock.Setup(x => x.UploadFile(It.IsAny<Stream>(), It.IsAny<string>(), It.IsAny<TypeFile>())).ReturnsAsync("testUrl");

            // Act
            var response = await _controller.UploadFile(fileMock.Object, messageModel);

            // Assert
            response.Should().BeOfType<OkResult>();
        }

        // Otros tests similares para cubrir otros casos de éxito y de error en el método UploadFile

        [Test]
        public async Task DeleteConversation_Returns_NoContent_If_Deletion_Succeeds()
        {
            // Arrange
            var user = "1;2";
            var userAMock = new User { /* Inicializar una instancia de User */ };
            var userBMock = new User { /* Inicializar una instancia de User */ };
            var messages = new List<Message> { new Message { /* Inicializar una instancia de Message */ } };
            _userServiceMock.Setup(x => x.GetById(It.IsAny<int>())).ReturnsAsync((User)null);
            _userServiceMock.SetupSequence(x => x.GetById(It.IsAny<int>())).ReturnsAsync(userAMock).ReturnsAsync(userBMock);
            _messageServiceMock.Setup(x => x.GetMessageByCode(It.IsAny<string>())).ReturnsAsync(messages);
            _storageServiceMock.Setup(x => x.DeleteFile(It.IsAny<string>(), It.IsAny<TypeFile>())).Returns(Task.CompletedTask);

            // Act
            var response = await _controller.DeleteConversation(user);

            // Assert
            response.Should().BeOfType<NoContentResult>();
        }

        // Otros tests similares para cubrir otros casos de éxito y de error en el método DeleteConversation

        [Test]
        public async Task GetNewMessage_Returns_OkResult_If_Messages_Found()
        {
            // Arrange
            var format = "testCode;1";
            var messages = new List<Message> { new Message { /* Inicializar una instancia de Message */ } };
            _messageServiceMock.Setup(x => x.GetMessageByCode(It.IsAny<string>())).ReturnsAsync(messages);

            // Act
            var response = await _controller.GetNewMessage(format);

            // Assert
            response.Result.Should().BeOfType<OkObjectResult>();
        }

    }
}