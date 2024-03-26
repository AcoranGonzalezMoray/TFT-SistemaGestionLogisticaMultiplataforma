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
	[SwaggerTag("Endpoints related to company management.")]
	public class CompanyController : ControllerBase {
		private readonly ICompanyService _companyService;
		private readonly IVehicleService _vehicleService;
		private readonly IMapper _mapper;

		public CompanyController(ICompanyService companyService, IVehicleService vehicleService, IMapper mapper) {
			_companyService = companyService;
			_vehicleService = vehicleService;
			_mapper = mapper;
		}

		[SwaggerOperation(Summary = "Get all companies", Description = "Retrieve all companies.")]
		[SwaggerResponse(200, "OK", typeof(IEnumerable<CompanyModel>))]
		[SwaggerResponse(404, "Not Found", typeof(string))]
		[SwaggerResponse(400, "Bad Request", typeof(string))]
		[HttpGet, MapToApiVersion(1.0)]
		public async Task<ActionResult<IEnumerable<CompanyModel>>> Get() {
			try {
				var companies = await _companyService.GetAll();

				if (companies is null) return NotFound();//404

				return Ok(_mapper.Map<IEnumerable<Company>, IEnumerable<CompanyModel>>(companies)); //200
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Create a new company", Description = "Create a new company.")]
		[SwaggerResponse(201, "Created", typeof(CompanyModel))]
		[SwaggerResponse(400, "Bad Request", typeof(string))]
		[HttpPost, MapToApiVersion(1.0)]
		public async Task<IActionResult> Post([FromBody] CompanyModel value) {

			try {
				var company = _mapper.Map<CompanyModel, Company>(value);

				await _companyService.Create(company);

				return CreatedAtAction("Get", new { id = value.Id }, value);    //Id de Company
			}
			catch (Exception e) {

				return BadRequest(e.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Update an existing company", Description = "Update an existing company.")]
		[SwaggerResponse(204, "No Content")]
		[SwaggerResponse(404, "Not Found", typeof(string))]
		[SwaggerResponse(400, "Bad Request", typeof(string))]
		[HttpPut, MapToApiVersion(1.0)]
		public async Task<ActionResult<CompanyModel>> Put([FromBody] CompanyModel model) {
			try {
				var company = _mapper.Map<CompanyModel, Company>(model);

				if (company is null) return NotFound();//404

				await _companyService.Update(company);

				return NoContent(); //202
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Delete an existing company", Description = "Delete an existing company.")]
		[SwaggerResponse(204, "No Content")]
		[SwaggerResponse(404, "Not Found", typeof(string))]
		[SwaggerResponse(400, "Bad Request", typeof(string))]
		[HttpDelete, MapToApiVersion(1.0)]
		public async Task<IActionResult> Delete([FromBody] CompanyModel model) {
			try {
				var company = _mapper.Map<CompanyModel, Company>(model);

				if (company is null) return NotFound();//404

				await _companyService.Delete(company);

				return NoContent(); //202
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}
		[SwaggerOperation(Summary = "Get employees of a company", Description = "Retrieve employees of a company.")]
		[SwaggerResponse(200, "OK", typeof(IEnumerable<UserModel>))]
		[SwaggerResponse(404, "Not Found", typeof(string))]
		[SwaggerResponse(400, "Bad Request", typeof(string))]
		[HttpPost("Employees"), MapToApiVersion(1.0)]
		public async Task<ActionResult<IEnumerable<UserModel>>> GetEmployees([FromBody] Company company) {
			try {

				var users = await _companyService.getEmployees(company.Code);

				if (users is null) return NotFound();//404

				return Ok(_mapper.Map<IEnumerable<User>, IEnumerable<UserModel>>(users)); //200
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Get vehicles of a company", Description = "Retrieve vehicles of a company.")]
		[SwaggerResponse(200, "OK", typeof(IEnumerable<VehicleModel>))]
		[SwaggerResponse(404, "Not Found", typeof(string))]
		[SwaggerResponse(400, "Bad Request", typeof(string))]
		[HttpGet("Vehicles/{code}"), MapToApiVersion(1.0)]
		public async Task<ActionResult<IEnumerable<VehicleModel>>> GetVehicles(string code) {
			try {

				var vehicles = await _vehicleService.GetVehiclesByCode(code);

				if (vehicles is null) return NotFound();//404

				return Ok(_mapper.Map<IEnumerable<Vehicle>, IEnumerable<VehicleModel>>(vehicles)); //200
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Get warehouses of a company", Description = "Retrieve warehouses of a company.")]
		[SwaggerResponse(200, "OK", typeof(IEnumerable<WarehouseModel>))]
		[SwaggerResponse(404, "Not Found", typeof(string))]
		[SwaggerResponse(400, "Bad Request", typeof(string))]
		[HttpPost("Warehouse"), MapToApiVersion(1.0)]
		public async Task<ActionResult<IEnumerable<WarehouseModel>>> GetWarehouses([FromBody] Company company) {
			try {
				if (String.IsNullOrEmpty(company.WarehouseId)) return BadRequest("This company don't have Warehouse yet.");
				var warehouses = await _companyService.getWarehouses(company.Code);

				if (warehouses is null) return NotFound();//404

				return Ok(_mapper.Map<IEnumerable<Warehouse>, IEnumerable<WarehouseModel>>(warehouses)); //200
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}
	}
}
