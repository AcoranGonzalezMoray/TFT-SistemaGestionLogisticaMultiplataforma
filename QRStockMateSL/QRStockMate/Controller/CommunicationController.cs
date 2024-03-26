using AutoMapper;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Services;
using QRStockMate.DTOs;
using QRStockMate.Services;
using Swashbuckle.AspNetCore.Annotations;

namespace QRStockMate.Controller
{
	[Route("api/[controller]")]
	[ApiController]
	[SwaggerTag("Endpoints related to communication management.")]
	public class CommunicationController : ControllerBase {


		private readonly IMapper _mapper;
		private readonly ICommunicationService _communicationService;

		public CommunicationController(IMapper mapper, ICommunicationService communicationService)
		{
			_mapper = mapper;
			_communicationService = communicationService;
		}



		//------------------------ Sentencias ------------------------------
		[SwaggerOperation(Summary = "Get all communications", Description = "Retrieve all communications.")]
		[SwaggerResponse(StatusCodes.Status200OK, "OK", typeof(IEnumerable<CommunicationModel>))]
		[SwaggerResponse(StatusCodes.Status404NotFound, "Not Found", typeof(string))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(string))]
		[HttpGet]
		public async Task<ActionResult<IEnumerable<CommunicationModel>>> Get()
		{
			try
			{
				var communications = await _communicationService.GetAll();

				if (communications is null) return NotFound();//404

				return Ok(_mapper.Map<IEnumerable<Communication>, IEnumerable<CommunicationModel>>(communications)); //200
			}
			catch (Exception ex)
			{

				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Create a new communication", Description = "Create a new communication.")]
		[SwaggerResponse(StatusCodes.Status201Created, "Created", typeof(CommunicationModel))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(string))]
		[HttpPost]
		[HttpPost]
		public async Task<IActionResult> Post([FromBody] CommunicationModel value)
		{
			try
			{
				var communication = _mapper.Map<CommunicationModel, Communication>(value);
				value.SentDate = DateTime.Now;
				await _communicationService.Create(communication);

				return CreatedAtAction("Get", new { id = communication.Id }, communication);
			}
			catch (Exception e)
			{

				return BadRequest(e.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Update an existing communication", Description = "Update an existing communication.")]
		[SwaggerResponse(StatusCodes.Status204NoContent, "No Content")]
		[SwaggerResponse(StatusCodes.Status404NotFound, "Not Found", typeof(string))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(string))]
		[HttpPut]
		public async Task<ActionResult<CommunicationModel>> Put([FromBody] CommunicationModel model)
		{
			try
			{
				var communication = _mapper.Map<CommunicationModel, Communication>(model);

				if (communication is null) return NotFound();//404

				await _communicationService.Update(communication);

				return NoContent(); //202
			}
			catch (Exception ex)
			{

				return BadRequest(ex.Message);//400
			}
		}



		[SwaggerOperation(Summary = "Delete an existing communication", Description = "Delete an existing communication.")]
		[SwaggerResponse(StatusCodes.Status204NoContent, "No Content")]
		[SwaggerResponse(StatusCodes.Status404NotFound, "Not Found", typeof(string))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(string))]
		[HttpDelete]
		public async Task<IActionResult> Delete([FromBody] CommunicationModel model)
		{
			try
			{
				var communication = _mapper.Map<CommunicationModel, Communication>(model);
				if (communication is null) return NotFound(); //404

				await _communicationService.Delete(communication);

				return NoContent(); //202
			}
			catch (Exception ex)
			{

				return BadRequest(ex.Message);//400
			}
		}


		[SwaggerOperation(Summary = "Get communications by code", Description = "Retrieve communications by code.")]
		[SwaggerResponse(StatusCodes.Status200OK, "OK", typeof(IEnumerable<CommunicationModel>))]
		[SwaggerResponse(StatusCodes.Status404NotFound, "Not Found", typeof(string))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(string))]
		[HttpGet("GetByCode/{code}")]
		public async Task<ActionResult<IEnumerable<CommunicationModel>>> GetByCode(string code)
		{
			try
			{
				var communications = await _communicationService.GetCommunicationsByCode(code);

				if (communications is null) return NotFound();//404

				return Ok(_mapper.Map<IEnumerable<Communication>, IEnumerable<CommunicationModel>>(communications)); //200
			}
			catch (Exception ex)
			{

				return BadRequest(ex.Message);//400
			}
		}
	}
}
