using AutoMapper;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Services;
using QRStockMate.Model;

namespace QRStockMate.Controller
{
	[Route("api/[controller]")]
	[ApiController]
	public class TransportRouteController : ControllerBase
	{
		private readonly IMapper _mapper;
		private readonly ITransportRouteService _TransportRouteService;

		public TransportRouteController(IMapper mapper, ITransportRouteService TransportRouteService)
		{
			_mapper = mapper;
			_TransportRouteService = TransportRouteService;
		}

		//------------------------ Sentencias ------------------------------

		[HttpGet]
		public async Task<ActionResult<IEnumerable<TransportRouteModel>>> Get()
		{
			try
			{
				var TransportRoutes = await _TransportRouteService.GetAll();

				if (TransportRoutes is null) return NotFound();//404

				return Ok(_mapper.Map<IEnumerable<TransportRoute>, IEnumerable<TransportRouteModel>>(TransportRoutes)); //200
			}
			catch (Exception ex)
			{

				return BadRequest(ex.Message);//400
			}
		}

		[HttpPost]
		public async Task<IActionResult> Post([FromBody] TransportRouteModel value)
		{
			try
			{
				var TransportRoute = _mapper.Map<TransportRouteModel, TransportRoute>(value);

				await _TransportRouteService.Create(TransportRoute);

				return CreatedAtAction("Get", new { id = TransportRoute.Id }, TransportRoute);
			}
			catch (Exception e)
			{

				return BadRequest(e.Message);//400
			}
		}

		[HttpPut]
		public async Task<ActionResult<TransportRouteModel>> Put([FromBody] TransportRouteModel model)
		{
			try
			{
				var TransportRoute = _mapper.Map<TransportRouteModel, TransportRoute>(model);

				if (TransportRoute is null) return NotFound();//404

				await _TransportRouteService.Update(TransportRoute);

				return NoContent(); //202
			}
			catch (Exception ex)
			{

				return BadRequest(ex.Message);//400
			}
		}

		[HttpDelete]
		public async Task<IActionResult> Delete([FromBody] TransportRouteModel model)
		{
			try
			{
				var TransportRoute = _mapper.Map<TransportRouteModel, TransportRoute>(model);
				if (TransportRoute is null) return NotFound(); //404

				await _TransportRouteService.Delete(TransportRoute);

				return NoContent(); //202
			}
			catch (Exception ex)
			{

				return BadRequest(ex.Message);//400
			}
		}

		[HttpGet("TransportRoutes/{code}")]
		public async Task<ActionResult<IEnumerable<TransportRouteModel>>> GetTransportRoutes(string code)
		{
			try
			{
				var TransportRoutes = await _TransportRouteService.GetTransportRoutesByCode(code);

				if (TransportRoutes is null) return NotFound();//404

				return Ok(_mapper.Map<IEnumerable<TransportRoute>, IEnumerable<TransportRouteModel>>(TransportRoutes)); //200
			}
			catch (Exception ex)
			{

				return BadRequest(ex.Message);//400
			}
		}

		[HttpGet("TransportRouteById/{id}")]
		public async Task<ActionResult<IEnumerable<TransportRouteModel>>> GetTransportRouteById(int id)
		{
			try
			{
				var TransportRoutes = await _TransportRouteService.GetById(id);

				if (TransportRoutes is null) return NotFound();//404

				return Ok(_mapper.Map<TransportRoute, TransportRouteModel>(TransportRoutes)); //200
			}
			catch (Exception ex)
			{

				return BadRequest(ex.Message);//400
			}
		}

		[HttpPut("InitRoute/{id}")]
		public async Task<ActionResult<DateTime>> InitRoute(int id)
		{
			try
			{
				var transportRoute = await _TransportRouteService.GetById(id);
				if (transportRoute is null) return NotFound();//404

				var date = await _TransportRouteService.InitRoute(id);

				return Ok(date); //200
			}
			catch (Exception ex)
			{

				return BadRequest(ex.Message);//400
			}
		}

		[HttpPut("FinishRoute/{id}")]
		public async Task<ActionResult<DateTime>> FinishRoute(int id)
		{
			try
			{
				var transportRoute = await _TransportRouteService.GetById(id);
				if (transportRoute is null) return NotFound();//404

				var date = await _TransportRouteService.FinishRoute(id);

				return Ok(date); //200
			}
			catch (Exception ex)
			{

				return BadRequest(ex.Message);//400
			}
		}
	}
}
