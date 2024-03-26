using Asp.Versioning;
using AutoMapper;
using Microsoft.AspNetCore.Mvc;
using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Services;
using QRStockMate.DTOs;
using Swashbuckle.AspNetCore.Annotations;

namespace QRStockMate.Controller {
	[ApiController]
	[ApiVersion(1.0)]
	[Route("api/v{version:apiVersion}/[controller]")]
	[SwaggerTag("Endpoints related to vehicle management.")]
	public class VehicleController : ControllerBase {
		private readonly IMapper _mapper;
		private readonly IVehicleService _vehicleService;

		public VehicleController(IMapper mapper, IVehicleService vehicleService) {
			_mapper = mapper;
			_vehicleService = vehicleService;
		}

		[SwaggerOperation(Summary = "Get all vehicles", Description = "Retrieves all vehicles.")]
		[SwaggerResponse(StatusCodes.Status200OK, "OK", typeof(IEnumerable<VehicleModel>))]
		[SwaggerResponse(StatusCodes.Status404NotFound, "Not Found", typeof(void))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpGet, MapToApiVersion(1.0)]
		public async Task<ActionResult<IEnumerable<VehicleModel>>> Get() {
			try {
				var Vehicles = await _vehicleService.GetAll();

				if (Vehicles is null) return NotFound();//404

				return Ok(_mapper.Map<IEnumerable<Vehicle>, IEnumerable<VehicleModel>>(Vehicles)); //200
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Create vehicle", Description = "Creates a new vehicle.")]
		[SwaggerResponse(StatusCodes.Status201Created, "Created", typeof(Vehicle))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpPost, MapToApiVersion(1.0)]
		public async Task<IActionResult> Post([FromBody] VehicleModel value) {
			try {
				var Vehicle = _mapper.Map<VehicleModel, Vehicle>(value);

				await _vehicleService.Create(Vehicle);

				return CreatedAtAction("Get", new { id = Vehicle.Id }, Vehicle);
			}
			catch (Exception e) {

				return BadRequest(e.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Update vehicle", Description = "Updates an existing vehicle.")]
		[SwaggerResponse(StatusCodes.Status204NoContent, "No Content", typeof(void))]
		[SwaggerResponse(StatusCodes.Status404NotFound, "Not Found", typeof(void))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpPut, MapToApiVersion(1.0)]
		public async Task<ActionResult<VehicleModel>> Put([FromBody] VehicleModel model) {
			try {
				var Vehicle = _mapper.Map<VehicleModel, Vehicle>(model);

				if (Vehicle is null) return NotFound();//404

				await _vehicleService.Update(Vehicle);

				return NoContent(); //202
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Delete vehicle", Description = "Deletes an existing vehicle.")]
		[SwaggerResponse(StatusCodes.Status204NoContent, "No Content", typeof(void))]
		[SwaggerResponse(StatusCodes.Status404NotFound, "Not Found", typeof(void))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpDelete, MapToApiVersion(1.0)]
		public async Task<IActionResult> Delete([FromBody] VehicleModel model) {
			try {
				var Vehicle = _mapper.Map<VehicleModel, Vehicle>(model);
				if (Vehicle is null) return NotFound(); //404

				await _vehicleService.Delete(Vehicle);

				return NoContent(); //202
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Update vehicle location", Description = "Updates the location of a vehicle.")]
		[SwaggerResponse(StatusCodes.Status204NoContent, "No Content", typeof(void))]
		[SwaggerResponse(StatusCodes.Status404NotFound, "Not Found", typeof(void))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpPut("UpdateLocation/{Id}"), MapToApiVersion(1.0)]
		public async Task<ActionResult> UpdateLocation(int Id, [FromBody] string location) {
			try {
				var Vehicle = await _vehicleService.GetById(Id);
				if (Vehicle is null) return NotFound();//404

				Vehicle.Location = location;

				await _vehicleService.Update(Vehicle);

				return NoContent(); //202
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Get vehicle location", Description = "Retrieves the location of a vehicle.")]
		[SwaggerResponse(StatusCodes.Status200OK, "OK", typeof(Vehicle))]
		[SwaggerResponse(StatusCodes.Status404NotFound, "Not Found", typeof(void))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpGet("GetLocation/{Id}"), MapToApiVersion(1.0)]
		public async Task<ActionResult<VehicleModel>> GetLocation(int Id) {
			try {
				var Vehicle = await _vehicleService.GetById(Id);
				if (Vehicle is null) return NotFound();//404
				return Ok(Vehicle);
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}
	}
}
