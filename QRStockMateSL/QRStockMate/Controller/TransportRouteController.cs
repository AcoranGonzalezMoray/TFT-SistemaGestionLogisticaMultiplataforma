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
	[SwaggerTag("Endpoints related to transport route management.")]
	public class TransportRouteController : ControllerBase {
		private readonly IMapper _mapper;
		private readonly ITransportRouteService _TransportRouteService;

		public TransportRouteController(IMapper mapper, ITransportRouteService TransportRouteService) {
			_mapper = mapper;
			_TransportRouteService = TransportRouteService;
		}

		[SwaggerOperation(Summary = "Get all transport routes", Description = "Retrieves all transport routes.")]
		[SwaggerResponse(StatusCodes.Status200OK, "OK", typeof(IEnumerable<TransportRouteModel>))]
		[SwaggerResponse(StatusCodes.Status404NotFound, "Not Found", typeof(void))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpGet, MapToApiVersion("1.0")]
		public async Task<ActionResult<IEnumerable<TransportRouteModel>>> Get() {
			try {
				var TransportRoutes = await _TransportRouteService.GetAll();

				if (TransportRoutes is null) return NotFound();//404

				return Ok(_mapper.Map<IEnumerable<TransportRoute>, IEnumerable<TransportRouteModel>>(TransportRoutes)); //200
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Create transport route", Description = "Creates a new transport route.")]
		[SwaggerResponse(StatusCodes.Status201Created, "Created", typeof(TransportRoute))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpPost, MapToApiVersion("1.0")]
		public async Task<IActionResult> Post([FromBody] TransportRouteModel value) {
			try {
				var TransportRoute = _mapper.Map<TransportRouteModel, TransportRoute>(value);

				await _TransportRouteService.Create(TransportRoute);

				return CreatedAtAction("Get", new { id = TransportRoute.Id }, TransportRoute);
			}
			catch (Exception e) {

				return BadRequest(e.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Update transport route", Description = "Updates an existing transport route.")]
		[SwaggerResponse(StatusCodes.Status204NoContent, "No Content", typeof(void))]
		[SwaggerResponse(StatusCodes.Status404NotFound, "Not Found", typeof(void))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpPut, MapToApiVersion("1.0")]
		public async Task<ActionResult<TransportRouteModel>> Put([FromBody] TransportRouteModel model) {
			try {
				var TransportRoute = _mapper.Map<TransportRouteModel, TransportRoute>(model);

				if (TransportRoute is null) return NotFound();//404

				await _TransportRouteService.Update(TransportRoute);

				return NoContent(); //202
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Delete transport route", Description = "Deletes an existing transport route.")]
		[SwaggerResponse(StatusCodes.Status204NoContent, "No Content", typeof(void))]
		[SwaggerResponse(StatusCodes.Status404NotFound, "Not Found", typeof(void))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpDelete, MapToApiVersion("1.0")]
		public async Task<IActionResult> Delete([FromBody] TransportRouteModel model) {
			try {
				var TransportRoute = _mapper.Map<TransportRouteModel, TransportRoute>(model);
				if (TransportRoute is null) return NotFound(); //404

				await _TransportRouteService.Delete(TransportRoute);

				return NoContent(); //202
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Get transport routes by code", Description = "Retrieves transport routes by code.")]
		[SwaggerResponse(StatusCodes.Status200OK, "OK", typeof(IEnumerable<TransportRouteModel>))]
		[SwaggerResponse(StatusCodes.Status404NotFound, "Not Found", typeof(void))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpGet("TransportRoutes/{code}"), MapToApiVersion("1.0")]
		public async Task<ActionResult<IEnumerable<TransportRouteModel>>> GetTransportRoutes(string code) {
			try {
				var TransportRoutes = await _TransportRouteService.GetTransportRoutesByCode(code);

				if (TransportRoutes is null) return NotFound();//404

				return Ok(_mapper.Map<IEnumerable<TransportRoute>, IEnumerable<TransportRouteModel>>(TransportRoutes)); //200
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Get transport route by id", Description = "Retrieves transport route by id.")]
		[SwaggerResponse(StatusCodes.Status200OK, "OK", typeof(TransportRouteModel))]
		[SwaggerResponse(StatusCodes.Status404NotFound, "Not Found", typeof(void))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpGet("TransportRouteById/{id}"), MapToApiVersion("1.0")]
		public async Task<ActionResult<IEnumerable<TransportRouteModel>>> GetTransportRouteById(int id) {
			try {
				var TransportRoutes = await _TransportRouteService.GetById(id);

				if (TransportRoutes is null) return NotFound();//404

				return Ok(_mapper.Map<TransportRoute, TransportRouteModel>(TransportRoutes)); //200
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Initialize transport route", Description = "Initializes a transport route.")]
		[SwaggerResponse(StatusCodes.Status200OK, "OK", typeof(DateTime))]
		[SwaggerResponse(StatusCodes.Status404NotFound, "Not Found", typeof(void))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpPut("InitRoute/{id}"), MapToApiVersion("1.0")]
		public async Task<ActionResult<DateTime>> InitRoute(int id) {
			try {
				var transportRoute = await _TransportRouteService.GetById(id);
				if (transportRoute is null) return NotFound();//404

				var date = await _TransportRouteService.InitRoute(id);

				return Ok(date); //200
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Finish transport route", Description = "Finishes a transport route.")]
		[SwaggerResponse(StatusCodes.Status200OK, "OK", typeof(DateTime))]
		[SwaggerResponse(StatusCodes.Status404NotFound, "Not Found", typeof(void))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpPut("FinishRoute/{id}"), MapToApiVersion("1.0")]
		public async Task<ActionResult<DateTime>> FinishRoute(int id) {
			try {
				var transportRoute = await _TransportRouteService.GetById(id);
				if (transportRoute is null) return NotFound();//404

				var date = await _TransportRouteService.FinishRoute(id);

				return Ok(date); //200
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}
	}
}
