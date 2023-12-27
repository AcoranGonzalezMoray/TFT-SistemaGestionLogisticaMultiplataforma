using AutoMapper;
using Microsoft.AspNetCore.Mvc;
using Microsoft.IdentityModel.Tokens;
using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Services;
using QRStockMate.Model;

namespace QRStockMate.Controller
{
    [Route("api/[controller]")]
    [ApiController]
    public class CompanyController : ControllerBase
    {
        private readonly ICompanyService _companyService;
        private readonly IMapper _mapper;

        public CompanyController(ICompanyService companyService, IMapper mapper)
        {
            _companyService = companyService;
            _mapper = mapper;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<CompanyModel>>> Get()
        {
            try
            {
                var companies = await _companyService.GetAll();

                if (companies is null) return NotFound();//404

                return Ok(_mapper.Map<IEnumerable<Company>, IEnumerable<CompanyModel>>(companies)); //200
            }
            catch (Exception ex)
            {

                return BadRequest(ex.Message);//400
            }
        }

        [HttpPost]
        public async Task<IActionResult> Post([FromBody] CompanyModel value)
        {

            try
            {
                var company = _mapper.Map<CompanyModel, Company>(value);

                await _companyService.Create(company);

                return CreatedAtAction("Get", new { id = value.Id }, value);    //Id de Company
            }
            catch (Exception e)
            {

                return BadRequest(e.Message);//400
            }
        }

        [HttpPut]
        public async Task<ActionResult<CompanyModel>> Put([FromBody] CompanyModel model)
        {
            try
            {
                var company = _mapper.Map<CompanyModel, Company>(model);

                if (company is null) return NotFound();//404

                await _companyService.Update(company);

                return NoContent(); //202
            }
            catch (Exception ex)
            {

                return BadRequest(ex.Message);//400
            }
        }

        [HttpDelete]
        public async Task<IActionResult> Delete([FromBody] CompanyModel model)
        {
            try
            {
                var company = _mapper.Map<CompanyModel, Company>(model);

                if (company is null) return NotFound();//404

                await _companyService.Delete(company);

                return NoContent(); //202
            }
            catch (Exception ex)
            {

                return BadRequest(ex.Message);//400
            }
        }

        [HttpPost("Employees")]
        public async Task<ActionResult<IEnumerable<UserModel>>> GetEmployees([FromBody]Company company)
        {
            try
            {
                
                var users = await _companyService.getEmployees(company.Code);

                if (users is null) return NotFound();//404

                return Ok(_mapper.Map<IEnumerable<User>, IEnumerable<UserModel>>(users)); //200
            }
            catch (Exception ex)
            {

                return BadRequest(ex.Message);//400
            }
        }

        [HttpPost("Warehouse")]
        public async Task<ActionResult<IEnumerable<WarehouseModel>>> GetWarehouses([FromBody] Company company)
        {
            try
            {
                if (String.IsNullOrEmpty(company.WarehouseId)) return BadRequest("This company don't have Warehouse yet.");
                var warehouses = await _companyService.getWarehouses(company.Code);

                if (warehouses is null) return NotFound();//404
                
                return Ok(_mapper.Map<IEnumerable<Warehouse>, IEnumerable<WarehouseModel>>(warehouses)); //200
            }
            catch (Exception ex)
            {

                return BadRequest(ex.Message);//400
            }
        }
    }
}
