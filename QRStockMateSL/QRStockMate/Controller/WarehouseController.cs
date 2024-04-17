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
	[SwaggerTag("Endpoints related to warehouse management.")]
	public class WarehouseController : ControllerBase {
		private readonly IWarehouseService _warehouseService;
		private readonly ICompanyService _companyService;
		private readonly IItemService _itemService;
		private readonly IStorageService _context_storage;
		private readonly IMapper _mapper;

		public WarehouseController(IWarehouseService warehouseService, IMapper mapper, IStorageService context_storage, ICompanyService companyService, IItemService itemService) {
			_warehouseService = warehouseService;
			_itemService = itemService;
			_context_storage = context_storage;
			_mapper = mapper;
			_companyService = companyService;
		}

		[SwaggerOperation(Summary = "Get all warehouses", Description = "Retrieves all warehouses.")]
		[SwaggerResponse(200, "OK", typeof(IEnumerable<WarehouseModel>))]
		[SwaggerResponse(404, "Not Found", typeof(void))]
		[SwaggerResponse(400, "Bad Request", typeof(void))]
		[HttpGet, MapToApiVersion(1.0)]
		public async Task<ActionResult<IEnumerable<WarehouseModel>>> Get() {
			try {
				var warehouses = await _warehouseService.GetAll();

				if (warehouses is null) return NotFound();//404

				return Ok(_mapper.Map<IEnumerable<Warehouse>, IEnumerable<WarehouseModel>>(warehouses)); //200
			} catch (Exception ex) {
				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Create warehouse", Description = "Creates a new warehouse.")]
		[SwaggerResponse(201, "Created", typeof(WarehouseModel))]
		[SwaggerResponse(400, "Bad Request", typeof(void))]
		[HttpPost("{Id}"), MapToApiVersion(1.0)]
		public async Task<IActionResult> Post(int Id, [FromBody] WarehouseModel value) {
			try {
				var company = await _companyService.GetById(Id);
				if (company is null) return NotFound();

				var warehouse = _mapper.Map<WarehouseModel, Warehouse>(value);
				await _warehouseService.Create(warehouse);

				company.WarehouseId += $"{warehouse.Id};";
				await _companyService.Update(company);

				return CreatedAtAction("Get", new { id = value.Id }, value);
			} catch (Exception e) {
				return BadRequest(e.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Update warehouse", Description = "Updates an existing warehouse.")]
		[SwaggerResponse(204, "No Content", typeof(void))]
		[SwaggerResponse(404, "Not Found", typeof(void))]
		[SwaggerResponse(400, "Bad Request", typeof(void))]
		[HttpPut, MapToApiVersion(1.0)]
		public async Task<ActionResult<UserModel>> Put([FromBody] WarehouseModel model) {
			try {
				var warehouse = _mapper.Map<WarehouseModel, Warehouse>(model);

				if (warehouse is null) return NotFound();//404

				await _warehouseService.Update(warehouse);

				return NoContent(); //204
			} catch (Exception ex) {
				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Delete warehouse", Description = "Deletes an existing warehouse.")]
		[SwaggerResponse(204, "No Content", typeof(void))]
		[SwaggerResponse(404, "Not Found", typeof(void))]
		[SwaggerResponse(400, "Bad Request", typeof(void))]
		[HttpDelete("{idCompany}"), MapToApiVersion(1.0)]
		public async Task<IActionResult> Delete(int idCompany, [FromBody] WarehouseModel model) {
			try {
				var warehouse = _mapper.Map<WarehouseModel, Warehouse>(model);

				var company = await _companyService.GetById(idCompany);

				company.WarehouseId = Utility.Utility.RemoveSpecificId(company.WarehouseId, warehouse.Id);

				if (warehouse is null) return NotFound();//404

				if (Uri.IsWellFormedUriString(warehouse.Url, UriKind.Absolute)) {
					// Es una URL válida, puedes proceder con la eliminación
					await _context_storage.DeleteImage(warehouse.Url);
				}
				var idItems = warehouse.IdItems;
				if (idItems != "") {
					idItems = idItems.TrimEnd(';'); // Elimina el último punto y coma
					List<int> idList = idItems.Split(';').Select(int.Parse).ToList();
					List<Item> itemList = new List<Item>();
					foreach (int b in idList) {
						Item item = await _itemService.GetById(b);
						if (item != null) {
							itemList.Add(item);
						}
					}
					await _itemService.DeleteRange(itemList);
				}

				await _warehouseService.Delete(warehouse);
				await _companyService.Update(company);

				return NoContent(); //204
			} catch (Exception ex) {
				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Update warehouse image", Description = "Updates the profile image of a warehouse.")]
		[SwaggerResponse(200, "OK", typeof(void))]
		[SwaggerResponse(404, "Not Found", typeof(void))]
		[SwaggerResponse(400, "Bad Request", typeof(void))]
		[HttpPost("UpdateImage"), MapToApiVersion(1.0)]
		public async Task<IActionResult> UpdateImage([FromForm] int warehouseId, [FromForm] IFormFile image) {
			try {
				var warehouse = await _warehouseService.GetById(warehouseId);
				if (warehouse == null) return NotFound();

				if (Uri.IsWellFormedUriString(warehouse.Url, UriKind.Absolute)) {
					// Es una URL válida, puedes proceder con la eliminación
					await _context_storage.DeleteImage(warehouse.Url);
				}

				Stream image_stream = image.OpenReadStream();
				string urlimagen = await _context_storage.UploadImage(image_stream, image.FileName);

				warehouse.Url = urlimagen;

				await _warehouseService.Update(warehouse);
				return Ok();
			} catch (Exception ex) {
				return BadRequest(ex.Message);
			}
		}

		[SwaggerOperation(Summary = "Add item to warehouse", Description = "Adds an item to the warehouse.")]
		[SwaggerResponse(201, "Created", typeof(Item))]
		[SwaggerResponse(400, "Bad Request", typeof(void))]
		[HttpPost("AddItem/{Id}"), MapToApiVersion(1.0)]
		public async Task<IActionResult> AddItem(int Id, [FromBody] ItemModel itemModel) {
			try {
				var warehouse = await _warehouseService.GetById(Id);

				if (warehouse == null) return NotFound();

				var items = await _warehouseService.GetItems(Id);

				Console.WriteLine("LLEGA");

				foreach (var local in items) {
					if (local.Location == itemModel.Location) return BadRequest("A product already exists in that location.\r\n");
				}
				var item = _mapper.Map<ItemModel, Item>(itemModel);

				await _warehouseService.AddItem(Id, item);

				return CreatedAtAction("Get", new { id = item.Id }, item);
			} catch (Exception ex) {
				return BadRequest(ex.Message);
			}
		}

		[SwaggerOperation(Summary = "Add item range to warehouses", Description = "Adds multiple items to the warehouses.")]
		[SwaggerResponse(200, "OK", typeof(void))]
		[SwaggerResponse(400, "Bad Request", typeof(void))]
		[HttpPost("AddItemRange/"), MapToApiVersion(1.0)]
		public async Task<IActionResult> AddItemRange([FromBody] ItemModel[] itemModel) {
			try {
				foreach (var _item in itemModel) {
					var warehouse = await _warehouseService.GetById(_item.WarehouseId);

					if (warehouse != null) {
						Console.WriteLine(_item);
						var item = _mapper.Map<ItemModel, Item>(_item);

						await _warehouseService.AddItem(_item.WarehouseId, item);
					}
				}

				return Ok();
			} catch (Exception ex) {
				return BadRequest(ex.Message);
			}
		}

		[SwaggerOperation(Summary = "Get items in warehouse", Description = "Retrieves all items in a warehouse.")]
		[SwaggerResponse(200, "OK", typeof(IEnumerable<ItemModel>))]
		[SwaggerResponse(404, "Not Found", typeof(void))]
		[SwaggerResponse(400, "Bad Request", typeof(void))]
		[HttpGet("GetItems/{Id}"), MapToApiVersion(1.0)]
		public async Task<ActionResult<IEnumerable<ItemModel>>> GetItems(int Id) {
			try {
				var warehouse = await _warehouseService.GetById(Id);

				if (warehouse == null) return NotFound();

				if (String.IsNullOrEmpty(warehouse.IdItems)) return BadRequest("This warehouse don't have item yet.");

				var Items = await _warehouseService.GetItems(warehouse.Id);

				return Ok(_mapper.Map<IEnumerable<Item>, IEnumerable<ItemModel>>(Items));
			} catch (Exception ex) {
				return BadRequest(ex.Message);
			}
		}
	}
}