using Asp.Versioning;
using AutoMapper;
using Microsoft.AspNetCore.Mvc;
using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Services;
using QRStockMate.DTOs;
using Swashbuckle.AspNetCore.Annotations;

namespace QRStockMate.Controller {
	[Route("api/[controller]")]
	[ApiController]
	[ApiVersion("1.0")]
	[ApiVersion("2.0")]
	[Route("api/v{version:apiVersion}/[controller]")]
	[SwaggerTag("Endpoints related to message management.")]
	public class MessageController : ControllerBase {
		private readonly IMessageService _messageService;
		private readonly IStorageService _context_storage;
		private readonly IUserService _userService;
		private readonly IMapper _mapper;

		public MessageController(IMessageService messageService, IStorageService context_storage, IUserService userService, IMapper mapper) {
			_messageService = messageService;
			_context_storage = context_storage;
			_userService = userService;
			_mapper = mapper;
		}


		//FUNCIONES BASICAS
		[SwaggerOperation(Summary = "Get all messages", Description = "Retrieves all messages.")]
		[SwaggerResponse(StatusCodes.Status200OK, "OK", typeof(IEnumerable<MessageModel>))]
		[SwaggerResponse(StatusCodes.Status404NotFound, "Not Found", typeof(void))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpGet, MapToApiVersion("1.0")]
		public async Task<ActionResult<IEnumerable<MessageModel>>> Get() {
			try {
				var message = await _messageService.GetAll();

				if (message is null) return NotFound();//404

				return Ok(_mapper.Map<IEnumerable<Message>, IEnumerable<MessageModel>>(message)); //200
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Create a new message", Description = "Creates a new message.")]
		[SwaggerResponse(StatusCodes.Status201Created, "Created", typeof(MessageModel))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpPost, MapToApiVersion("1.0")]
		public async Task<IActionResult> Post([FromBody] MessageModel value) {

			try {
				var message = _mapper.Map<MessageModel, Message>(value);

				message.SentDate = DateTime.Now;

				await _messageService.Create(message);

				return CreatedAtAction("Get", new { id = message.Id }, message);
			}
			catch (Exception e) {

				return BadRequest(e.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Update an existing message", Description = "Updates an existing message.")]
		[SwaggerResponse(StatusCodes.Status204NoContent, "No Content", typeof(void))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpPut, MapToApiVersion("1.0")]
		public async Task<ActionResult<MessageModel>> Put([FromBody] MessageModel model) {
			try {
				var message = _mapper.Map<MessageModel, Message>(model);

				message.SentDate = DateTime.Now;

				if (message is null) return NotFound();//404

				await _messageService.Update(message);

				return NoContent(); //202
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Delete a message", Description = "Deletes a message.")]
		[SwaggerResponse(StatusCodes.Status204NoContent, "No Content", typeof(void))]
		[SwaggerResponse(StatusCodes.Status404NotFound, "Not Found", typeof(void))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpDelete, MapToApiVersion("1.0")]
		public async Task<IActionResult> Delete([FromBody] MessageModel model) {
			try {
				var message = _mapper.Map<MessageModel, Message>(model);

				if (message is null) return NotFound();//404

				//await _context_storage.DeleteImage(user.Url);
				await _messageService.Delete(message);

				return NoContent(); //202
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Get messages by code", Description = "Retrieves messages by code.")]
		[SwaggerResponse(StatusCodes.Status200OK, "OK", typeof(IEnumerable<Message>))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpGet("MessageByCode/{code}"), MapToApiVersion("1.0")]
		public async Task<ActionResult<IEnumerable<Message>>> GetMessagesByCode(string code) {
			try {
				var messages = await _messageService.GetMessageByCode(code);


				return Ok(_mapper.Map<IEnumerable<Message>, IEnumerable<MessageModel>>(messages)); //200
			}
			catch (Exception ex) {
				return BadRequest(ex.Message); //400
			}


		}

		[SwaggerOperation(Summary = "Upload a file", Description = "Uploads a file.")]
		[SwaggerResponse(StatusCodes.Status200OK, "OK", typeof(void))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpPost("UploadFile/"), MapToApiVersion("1.0")]
		public async Task<IActionResult> UploadFile([FromForm] IFormFile file, [FromForm] MessageModel model) {
			try {
				//var message = _mapper.Map<MessageModel, Message>(model);

				Stream file_stream = file.OpenReadStream();
				string urlAudio = await _context_storage.UploadFile(file_stream, file.FileName, model.Type);

				model.SentDate = DateTime.Now;
				model.Content = urlAudio;
				var message = _mapper.Map<MessageModel, Message>(model);
				await _messageService.Create(message);
				return Ok();
			}
			catch (Exception e) {

				return BadRequest(e.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Delete conversation", Description = "Deletes a conversation between two users.")]
		[SwaggerResponse(StatusCodes.Status204NoContent, "No Content", typeof(void))]
		[SwaggerResponse(StatusCodes.Status404NotFound, "Not Found", typeof(void))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpDelete("DeleteConversation"), MapToApiVersion("1.0")]
		public async Task<IActionResult> DeleteConversation([FromBody] string user) {
			try {
				var userA = await _userService.GetById(int.Parse(user.Split(";")[0]));
				var userB = await _userService.GetById(int.Parse(user.Split(";")[1]));

				if (userA == null || userB == null)
					return NotFound();

				var messages = await _messageService.GetMessageByCode(userA.Code);

				var messagesToDelete = messages
					.Where(item =>
						(item.SenderContactId == userA.Id && item.ReceiverContactId == userB.Id) ||
						(item.ReceiverContactId == userA.Id && item.SenderContactId == userB.Id))
					.ToList();

				foreach (var item in messagesToDelete) {
					if (Uri.IsWellFormedUriString(item.Content, UriKind.Absolute)) {
						// Es una URL válida, puedes proceder con la eliminación
						await _context_storage.DeleteFile(item.Content, item.Type);
					}
				}

				await _messageService.DeleteRange(messagesToDelete);
				return NoContent();
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);
			}
		}

		[SwaggerOperation(Summary = "Delete conversation by Angular", Description = "Deletes a conversation between two users (Angular version).")]
		[SwaggerResponse(StatusCodes.Status204NoContent, "No Content", typeof(void))]
		[SwaggerResponse(StatusCodes.Status404NotFound, "Not Found", typeof(void))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpDelete("DeleteConversation/{param1}/{param2}"), MapToApiVersion("2.0")]
		public async Task<IActionResult> DeleteConversationByAngular(string param1, string param2) {
			try {
				var userA = await _userService.GetById(int.Parse(param1));
				var userB = await _userService.GetById(int.Parse(param2));

				if (userA == null || userB == null)
					return NotFound();

				var messages = await _messageService.GetMessageByCode(userA.Code);

				var messagesToDelete = messages
					.Where(item =>
						(item.SenderContactId == userA.Id && item.ReceiverContactId == userB.Id) ||
						(item.ReceiverContactId == userA.Id && item.SenderContactId == userB.Id))
					.ToList();

				foreach (var item in messagesToDelete) {
					if (Uri.IsWellFormedUriString(item.Content, UriKind.Absolute)) {
						// Es una URL válida, puedes proceder con la eliminación
						await _context_storage.DeleteFile(item.Content, item.Type);
					}
				}

				await _messageService.DeleteRange(messagesToDelete);
				return NoContent();
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);
			}
		}

		[SwaggerOperation(Summary = "Get new messages", Description = "Retrieves new messages for the specified user.")]
		[SwaggerResponse(StatusCodes.Status200OK, "OK", typeof(List<Message>))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpGet("NewMessage/{format}"), MapToApiVersion("1.0")]
		public async Task<ActionResult<List<Message>>> GetNewMessage(string format) {
			try {
				var messages = await _messageService.GetMessageByCode(format.Split(";")[0]);
				Console.WriteLine(format.Split(";")[0]);

				if (messages is null) return NotFound();//404
				var messagesToMe = messages
					.Where(item =>
						(item.SenderContactId == int.Parse(format.Split(";")[1])) ||
						(item.ReceiverContactId == int.Parse(format.Split(";")[1])))
					.ToList();



				return Ok(messagesToMe); //200
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}
	}

}
