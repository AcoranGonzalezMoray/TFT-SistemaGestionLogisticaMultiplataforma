using AutoMapper;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Services;
using QRStockMate.Model;
using QRStockMate.Services;

namespace QRStockMate.Controller
{
	[Route("api/[controller]")]
	[ApiController]
	public class CommunicationController : ControllerBase
	{


		private readonly IMapper _mapper;
		private readonly ICommunicationService _communicationService;

		public CommunicationController(IMapper mapper, ICommunicationService communicationService)
		{
			_mapper = mapper;
			_communicationService = communicationService;
		}



		//------------------------ Sentencias ------------------------------

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
