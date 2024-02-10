using AutoMapper;
using Firebase.Auth;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Services;
using QRStockMate.Model;
using QRStockMate.Services;
using static System.Net.Mime.MediaTypeNames;

namespace QRStockMate.Controller
{
	[Route("api/[controller]")]
	[ApiController]
	public class MessageController : ControllerBase
	{
		private readonly IMessageService _messageService ;
		private readonly IStorageService _context_storage;
		private readonly IUserService _userService;
		private readonly IMapper _mapper;

		public MessageController(IMessageService messageService, IStorageService context_storage, IUserService userService, IMapper mapper)
		{
			_messageService = messageService;
			_context_storage = context_storage;
			_userService = userService;
			_mapper = mapper;
		}


		//FUNCIONES BASICAS

		[HttpGet]
		public async Task<ActionResult<IEnumerable<MessageModel>>> Get()
		{
			try
			{
				var message = await _messageService.GetAll();

				if (message is null) return NotFound();//404

				return Ok(_mapper.Map<IEnumerable<Message>, IEnumerable<MessageModel>>(message)); //200
			}
			catch (Exception ex)
			{

				return BadRequest(ex.Message);//400
			}
		}

		[HttpPost]
		public async Task<IActionResult> Post([FromBody] MessageModel value)
		{

			try
			{
				var message = _mapper.Map<MessageModel, Message>(value);

				message.SentDate = DateTime.Now;

				await _messageService.Create(message);

				return CreatedAtAction("Get", new { id = message.Id }, message);
			}
			catch (Exception e)
			{

				return BadRequest(e.Message);//400
			}
		}

		[HttpPut]
		public async Task<ActionResult<MessageModel>> Put([FromBody] MessageModel model)
		{
			try
			{
				var message = _mapper.Map<MessageModel, Message>(model);

				message.SentDate = DateTime.Now;

				if (message is null) return NotFound();//404

				await _messageService.Update(message);

				return NoContent(); //202
			}
			catch (Exception ex)
			{

				return BadRequest(ex.Message);//400
			}
		}

		[HttpDelete]
		public async Task<IActionResult> Delete([FromBody] MessageModel model)
		{
			try
			{
				var message = _mapper.Map<MessageModel, Message>(model);

				if (message is null) return NotFound();//404

				//await _context_storage.DeleteImage(user.Url);
				await _messageService.Delete(message);

				return NoContent(); //202
			}
			catch (Exception ex)
			{

				return BadRequest(ex.Message);//400
			}
		}

		[HttpGet("MessageByCode/{code}")]
		public async Task<ActionResult<IEnumerable<Message>>> GetMessagesByCode(string code) {
			try
			{
				var messages = await _messageService.GetMessageByCode(code);


				return Ok(_mapper.Map<IEnumerable<Message>, IEnumerable<MessageModel>>(messages)); //200
			}
			catch (Exception ex)
			{
				return  BadRequest(ex.Message); //400
			}
		
		
		}

		[HttpPost("UploadFile/")]
		public async Task<IActionResult> UploadFile([FromForm] IFormFile file, [FromForm] MessageModel model)
		{
			try
			{
				//var message = _mapper.Map<MessageModel, Message>(model);

				Stream file_stream = file.OpenReadStream();
				string urlAudio = await _context_storage.UploadFile(file_stream, file.FileName, model.Type);

				model.SentDate = DateTime.Now;
				model.Content = urlAudio;
				var message = _mapper.Map<MessageModel, Message>(model);
				await _messageService.Create(message);
				return Ok();
			}
			catch (Exception e)
			{

				return BadRequest(e.Message);//400
			}
		}


		[HttpDelete("DeleteConversation")]
		public async Task<IActionResult> DeleteConversation([FromBody] string user)
		{
			try
			{
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

				foreach (var item in messagesToDelete)
				{
					if (Uri.IsWellFormedUriString(item.Content, UriKind.Absolute))
					{
						// Es una URL válida, puedes proceder con la eliminación
						await _context_storage.DeleteFile(item.Content, item.Type);
					}
				}

				await _messageService.DeleteRange(messagesToDelete);
				return NoContent();
			}
			catch (Exception ex)
			{

				return BadRequest(ex.Message);
			}
		}


		[HttpGet("NewMessage/{format}")]
		public async Task<ActionResult<List<Message>>> GetNewMessage(string format)
		{
			try
			{
				var messages = await _messageService.GetMessageByCode(format.Split(";")[0]);
				Console.WriteLine(format.Split(";")[0]);

				if (messages is null) return NotFound();//404
				var messagesToMe= messages
					.Where(item =>
						(item.SenderContactId == int.Parse(format.Split(";")[1])) ||
						(item.ReceiverContactId == int.Parse(format.Split(";")[1])))
					.ToList();



				return Ok(messagesToMe); //200
			}
			catch (Exception ex)
			{

				return BadRequest(ex.Message);//400
			}
		}
	}

}
