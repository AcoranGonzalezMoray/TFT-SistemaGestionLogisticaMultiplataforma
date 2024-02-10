using AutoMapper;
using Azure;
using FluentAssertions;
using Microsoft.AspNetCore.Mvc;
using Moq;
using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Repositories;
using QRStockMate.AplicationCore.Interfaces.Services;
using QRStockMate.Controller;
using QRStockMate.Model;
using QRStockMate.Services;
using System.Dynamic;
using static Microsoft.EntityFrameworkCore.DbLoggerCategory.Database;

namespace QRStockMate.Test
{
	public class UserControllerShould
	{
		private UserController _controller;
		private Mock<IUserService> _userServiceMock;
		private Mock<IStorageService> _storageServiceMock;
		private Mock<IMapper> _mapperMock;
		private Mock<ICompanyService> _companyServiceMock;
		private Mock<IJwtTokenRepository> _jwtTokenUtilityMock;

		[SetUp]
		public void Setup()
		{
			_userServiceMock = new Mock<IUserService>();
			_storageServiceMock = new Mock<IStorageService>();
			_mapperMock = new Mock<IMapper>();
			_companyServiceMock = new Mock<ICompanyService>();
			_jwtTokenUtilityMock = new Mock<IJwtTokenRepository>();

			_controller = new UserController(
				_userServiceMock.Object,
				_storageServiceMock.Object,
				_mapperMock.Object,
				_companyServiceMock.Object,
				_jwtTokenUtilityMock.Object);
		}


		[Test]
		public async Task IniciarSesion_Returns_NotFound_If_User_Not_Found()
		{
			// Arrange
			var email = "nonexistent@example.com";
			var password = "password";
			_userServiceMock.Setup(x => x.getUserByEmailPassword(email, It.IsAny<string>())).ReturnsAsync((User)null);

			// Act
			var response = await _controller.IniciarSesion(email, password);

			// Assert
			response.Should().BeOfType<NotFoundResult>();
		}

		[Test]
		public async Task IniciarSesion_Returns_OkResult_With_User_And_Token_If_User_Found()
		{
			// Arrange
			var email = "existing@example.com";
			var password = "password";
			var user = new User { Email = email, Password = Utility.Utility.EncriptarClave(password) };
			_userServiceMock.Setup(x => x.getUserByEmailPassword(email, It.IsAny<string>())).ReturnsAsync(user);
			_jwtTokenUtilityMock.Setup(x => x.GenToken(email, user.Password)).Returns("testToken");

			// Act
			var response = await _controller.IniciarSesion(email, password);

			// Assert
			response.Should().BeOfType<OkObjectResult>();
			var okObjectResult = response.As<OkObjectResult>();
			var responseObject = okObjectResult.Value as dynamic;

		}



		[Test]
		public async Task Registro_Returns_Conflict_If_User_Already_Exists()
		{
			// Arrange
			var model = new RegistrationModel
			{
				User = new UserModel { Email = "existing@example.com", Password = "password" },
				Company = new CompanyModel { /* Crear una instancia de CompanyModel */ }
			};
			_userServiceMock.Setup(x => x.getUserByEmailPassword(model.User.Email, It.IsAny<string>())).ReturnsAsync(new User());

			// Act
			var response = await _controller.Registro(model);

			// Assert
			response.Should().BeOfType<ConflictResult>();
		}

		[Test]
		public async Task Registro_Returns_BadRequest_If_No_Director_With_Associated_Code()
		{
			// Arrange
			var model = new RegistrationModel
			{
				User = new UserModel { Code = "nonexistentCode", Email = "user@example.com", Password = "password" },
				Company = new CompanyModel { /* Crear una instancia de CompanyModel */ }
			};
			_userServiceMock.Setup(x => x.getDirectorByCode(model.User.Code)).ReturnsAsync((User)null);

			// Act
			var response = await _controller.Registro(model);

			// Assert
			response.Should().BeOfType<BadRequestObjectResult>();
			var badRequestResult = response.As<BadRequestObjectResult>();
			badRequestResult.Value.Should().Be("There is no director with that associated code");
		}

		[Test]
		public async Task Registro_Creates_User_And_Company_With_Generated_Code_Rol_Director()
		{
			// Arrange
			var model = new RegistrationModel
			{
				User = new UserModel { Email = "newuser@example.com", Password = "password", Code = "" },
				Company = new CompanyModel { /* Crear una instancia de CompanyModel */ }
			};

			var user = new User
            {
				// Configura los campos del usuario según sea necesario para la prueba
			};

			var company = new Company
			{
				// Configura los campos de la empresa según sea necesario para la prueba
			};

			_mapperMock.Setup(x => x.Map<UserModel, User>(model.User)).Returns(user);
			_mapperMock.Setup(x => x.Map<CompanyModel, Company>(model.Company)).Returns(company);

			_userServiceMock.Setup(x => x.getUserByEmailPassword(model.User.Email, It.IsAny<string>())).ReturnsAsync((User)null);

			_companyServiceMock.Setup(x => x.Create(company));
			_userServiceMock.Setup(x => x.Create(user));
			// Act
			var response = await _controller.Registro(model);

			// Assert
			response.Should().BeOfType<CreatedAtActionResult>();
			var createdAtActionResult = response.As<CreatedAtActionResult>();
		}

		[Test]
		public async Task GetCompanyByUser_Returns_OkObjectResult_With_CompanyModel()
		{
			// Arrange
			var userModel = new UserModel { Code = "userCode" }; // Usar UserModel en lugar de User
			var company = new Company { Code = "userCode" };
			_userServiceMock.Setup(x => x.getCompany(userModel.Code)).ReturnsAsync(company);
			
			// Act
			var response = await _controller.GetCompanyByUser(userModel); // Pasar userModel en lugar de user

			// Assert
			response.Result.Should().BeOfType<OkObjectResult>();
			var okObjectResult = response.Result as OkObjectResult;
		}


		[Test]
		public async Task GetCompanyByUser_Returns_BadRequest_If_Exception_Occurs()
		{
			// Arrange
			var user = new User { Code = "userCode" };
			var errorMessage = "Error message";
			_userServiceMock.Setup(x => x.getCompany(user.Code)).ThrowsAsync(new Exception(errorMessage));

			// Act
			var response = await _controller.GetCompanyByUser(_mapperMock.Object.Map<User, UserModel>(user));

			// Assert
			response.Result.Should().BeOfType<BadRequestObjectResult>();
			var badRequestResult = response.Result.As<BadRequestObjectResult>();

		}
	}
}