using Asp.Versioning;
using AutoMapper;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Repositories;
using QRStockMate.AplicationCore.Interfaces.Services;
using QRStockMate.DTOs;
using Swashbuckle.AspNetCore.Annotations;

namespace QRStockMate.Controller {
	[Route("api/[controller]")]
	[ApiController]
	[ApiVersion("1.0")]
	[ApiVersion("2.0")]
	[Route("api/v{version:apiVersion}/[controller]")]
	[SwaggerTag("Endpoints related to user management.")]

	public class UserController : ControllerBase {
		private readonly IJwtTokenRepository _jwtTokenUtility;
		private readonly IUserService _userService;
		private readonly ICompanyService _companyService;
		private readonly IStorageService _context_storage;
		private readonly IMapper _mapper;
		public UserController(IUserService userService, IStorageService storageService, IMapper mapper, ICompanyService companyService, IJwtTokenRepository jwtTokenUtility) {
			_userService = userService;
			_context_storage = storageService;
			_mapper = mapper;
			_companyService = companyService;
			_jwtTokenUtility = jwtTokenUtility;
		}

		[SwaggerOperation(Summary = "Get all users", Description = "Retrieves all users.")]
		[SwaggerResponse(StatusCodes.Status200OK, "OK", typeof(IEnumerable<UserModel>))]
		[SwaggerResponse(StatusCodes.Status404NotFound, "Not Found", typeof(void))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpGet, MapToApiVersion("1.0")]
		public async Task<ActionResult<IEnumerable<UserModel>>> Get() {
			try {
				var users = await _userService.GetAll();

				if (users is null) return NotFound();//404

				return Ok(_mapper.Map<IEnumerable<User>, IEnumerable<UserModel>>(users)); //200
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Create user", Description = "Creates a new user.")]
		[SwaggerResponse(StatusCodes.Status201Created, "Created", typeof(UserModel))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpPost, MapToApiVersion("1.0")]
		public async Task<IActionResult> Post([FromBody] UserModel value) {

			try {
				var user = _mapper.Map<UserModel, User>(value);

				await _userService.Create(user);

				return CreatedAtAction("Get", new { id = value.Id }, value);
			}
			catch (Exception e) {

				return BadRequest(e.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Update user", Description = "Updates an existing user.")]
		[SwaggerResponse(StatusCodes.Status204NoContent, "No Content", typeof(void))]
		[SwaggerResponse(StatusCodes.Status404NotFound, "Not Found", typeof(void))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpPut, MapToApiVersion("1.0")]
		public async Task<ActionResult<UserModel>> Put([FromBody] UserModel model) {
			try {
				var user = _mapper.Map<UserModel, User>(model);

				if (user is null) return NotFound();//404

				await _userService.Update(user);

				return NoContent(); //202
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Delete user", Description = "Deletes an existing user.")]
		[SwaggerResponse(StatusCodes.Status204NoContent, "No Content", typeof(void))]
		[SwaggerResponse(StatusCodes.Status404NotFound, "Not Found", typeof(void))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpDelete, MapToApiVersion("1.0")]
		public async Task<IActionResult> Delete([FromBody] UserModel model) {
			try {
				var user = _mapper.Map<UserModel, User>(model);

				if (user is null) return NotFound();//404

				if (Uri.IsWellFormedUriString(user.Url, UriKind.Absolute)) {
					// Es una URL válida, puedes proceder con la eliminación
					await _context_storage.DeleteImage(user.Url);
				}
				await _userService.Delete(user);

				return NoContent(); //202
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Sign in", Description = "Signs in a user.")]
		[SwaggerResponse(StatusCodes.Status200OK, "OK", typeof(object))]
		[SwaggerResponse(StatusCodes.Status404NotFound, "Not Found", typeof(void))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[AllowAnonymous]
		[HttpPost("SignIn"), MapToApiVersion("1.0")]
		public async Task<IActionResult> IniciarSesion([FromForm] string email, [FromForm] string password) {
			try {
				var user = await _userService.getUserByEmailPassword(email, Utility.Utility.EncriptarClave(password));
				if (user == null) { return NotFound(); }//404
				var token = _jwtTokenUtility.GenToken(user.Email, user.Password);


				var response = new {
					User = user,
					Token = token
				};


				return Ok(response);//200
			}
			catch (Exception ex) {
				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Sign up", Description = "Signs up a new user.")]
		[SwaggerResponse(StatusCodes.Status201Created, "Created", typeof(object))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[AllowAnonymous]
		[HttpPost("SignUp"), MapToApiVersion("1.0")]
		public async Task<IActionResult> Registro([FromBody] RegistrationModel model) {
			try {
				var user = model.User;
				var company = _mapper.Map<CompanyModel, Company>(model.Company);

				var userE = await _userService.getUserByEmailPassword(user.Email, Utility.Utility.EncriptarClave(user.Password));

				if (userE != null) { return Conflict(); }//409

				user.Password = Utility.Utility.EncriptarClave(user.Password);


				if (user.Code.Length == 0) {
					user.Code = Utility.Utility.GenerateCode();
					user.Role = RoleUser.Director;

					//Al ser director se crea la empresa aqui
					company.Code = user.Code;
					await _companyService.Create(company);

				}
				else {
					var userC = await _userService.getDirectorByCode(user.Code);
					if (userC == null) { return BadRequest("There is no director with that associated code"); }

					user.Role = RoleUser.User;
				}

				var userEntity = _mapper.Map<UserModel, User>(user);
				await _userService.Create(userEntity);

				return CreatedAtAction("Get", new { id = user.Id }, user);
			}
			catch (Exception ex) {
				return BadRequest(ex.Message);
			}
		}

		[SwaggerOperation(Summary = "Get company by user", Description = "Retrieves company by user.")]
		[SwaggerResponse(StatusCodes.Status200OK, "OK", typeof(Company))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpPost("Company"), MapToApiVersion("1.0")]
		public async Task<ActionResult<Company>> GetCompanyByUser([FromBody] UserModel user) {
			try {
				var company = await _userService.getCompany(user.Code);

				return Ok(_mapper.Map<Company, CompanyModel>(company));
			}
			catch (Exception ex) {
				return BadRequest(ex.Message);
			}
		}

		[SwaggerOperation(Summary = "Delete account", Description = "Deletes a user's account.")]
		[SwaggerResponse(StatusCodes.Status204NoContent, "No Content", typeof(void))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpDelete("DeleteAccount"), MapToApiVersion("1.0")]
		public async Task<IActionResult> DeleteAccount([FromBody] UserModel user) {
			try {

				if (user.Role == RoleUser.Director) {
					var userEntity = _mapper.Map<UserModel, User>(user);

					await _userService.DeleteAccount(userEntity.Code);

					return NoContent();
				}
				else {
					return BadRequest("The user have been Director");
				}
			}
			catch (Exception ex) {
				return BadRequest(ex.Message);
			}
		}

		[SwaggerOperation(Summary = "Update user image", Description = "Updates user's profile image.")]
		[SwaggerResponse(StatusCodes.Status200OK, "OK", typeof(void))]
		[SwaggerResponse(StatusCodes.Status404NotFound, "Not Found", typeof(void))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpPost("UpdateImage"), MapToApiVersion("1.0")]
		public async Task<IActionResult> UpdateImage([FromForm] int userId, [FromForm] IFormFile image) {
			try {

				var user = await _userService.GetById(userId);
				if (user == null) return NotFound();

				if (Uri.IsWellFormedUriString(user.Url, UriKind.Absolute)) {
					// Es una URL válida, puedes proceder con la eliminación
					await _context_storage.DeleteImage(user.Url);
				}

				Stream image_stream = image.OpenReadStream();
				string urlimagen = await _context_storage.UploadImage(image_stream, image.FileName);

				user.Url = urlimagen;

				await _userService.Update(user);
				return Ok();
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);
			}
		}

	}



}
