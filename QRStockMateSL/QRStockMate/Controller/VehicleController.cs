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
	public class VehicleController : ControllerBase
	{
		private readonly IMapper _mapper;
		private readonly IVehicleService _vehicleService;

		public VehicleController(IMapper mapper, IVehicleService vehicleService)
		{
			_mapper = mapper;
			_vehicleService = vehicleService;
		}

		//------------------------ Sentencias ------------------------------

		[HttpGet]
		public async Task<ActionResult<IEnumerable<VehicleModel>>> Get()
		{
			try
			{
				var Vehicles = await _vehicleService.GetAll();

				if (Vehicles is null) return NotFound();//404

				return Ok(_mapper.Map<IEnumerable<Vehicle>, IEnumerable<VehicleModel>>(Vehicles)); //200
			}
			catch (Exception ex)
			{

				return BadRequest(ex.Message);//400
			}
		}

		[HttpPost]
		public async Task<IActionResult> Post([FromBody] VehicleModel value)
		{
			try
			{
				var Vehicle = _mapper.Map<VehicleModel, Vehicle>(value);

				await _vehicleService.Create(Vehicle);

				return CreatedAtAction("Get", new { id = Vehicle.Id }, Vehicle);
			}
			catch (Exception e)
			{

				return BadRequest(e.Message);//400
			}
		}

		[HttpPut]
		public async Task<ActionResult<VehicleModel>> Put([FromBody] VehicleModel model)
		{
			try
			{
				var Vehicle = _mapper.Map<VehicleModel, Vehicle>(model);

				if (Vehicle is null) return NotFound();//404

				await _vehicleService.Update(Vehicle);

				return NoContent(); //202
			}
			catch (Exception ex)
			{

				return BadRequest(ex.Message);//400
			}
		}

		[HttpDelete]
		public async Task<IActionResult> Delete([FromBody] VehicleModel model)
		{
			try
			{
				var Vehicle = _mapper.Map<VehicleModel, Vehicle>(model);
				if (Vehicle is null) return NotFound(); //404

				await _vehicleService.Delete(Vehicle);

				return NoContent(); //202
			}
			catch (Exception ex)
			{

				return BadRequest(ex.Message);//400
			}
		}


		[HttpPut("UpdateLocation/{Id}")]
		public async Task<ActionResult> UpdateLocation(int Id, [FromBody] string location)
		{
			try
			{
				var Vehicle = await _vehicleService.GetById(Id);
				if (Vehicle is null) return NotFound();//404

				Vehicle.Location = location;

				await _vehicleService.Update(Vehicle);

				return NoContent(); //202
			}
			catch (Exception ex)
			{

				return BadRequest(ex.Message);//400
			}
		}

		[HttpGet("GetLocation/{Id}")]
		public async Task<ActionResult<VehicleModel>> GetLocation(int Id)
		{
			try
			{
				var Vehicle = await _vehicleService.GetById(Id);
				if (Vehicle is null) return NotFound();//404
				return Ok(Vehicle);
			}
			catch (Exception ex)
			{

				return BadRequest(ex.Message);//400
			}
		}
	}
}
