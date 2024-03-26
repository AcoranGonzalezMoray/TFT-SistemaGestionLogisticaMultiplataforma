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
	[SwaggerTag("Endpoints related to item management.")]
	public class ItemController : ControllerBase {
		private readonly IItemService _itemService;
		private readonly IWarehouseService _warehouseService;
		private readonly IStorageService _context_storage;
		private readonly IMapper _mapper;

		public ItemController(IItemService itemService, IMapper mapper, IStorageService context_storage, IWarehouseService warehouseService) {
			_warehouseService = warehouseService;
			_itemService = itemService;
			_mapper = mapper;
			_context_storage = context_storage;
		}

		[SwaggerOperation(Summary = "Get all items", Description = "Retrieve all items.")]
		[SwaggerResponse(200, "OK", typeof(IEnumerable<ItemModel>))]
		[SwaggerResponse(404, "Not Found", typeof(string))]
		[SwaggerResponse(400, "Bad Request", typeof(string))]
		[HttpGet, MapToApiVersion("1.0")]
		public async Task<ActionResult<IEnumerable<ItemModel>>> Get() {
			try {
				var items = await _itemService.GetAll();

				if (items is null) return NotFound();//404

				return Ok(_mapper.Map<IEnumerable<Item>, IEnumerable<ItemModel>>(items)); //200
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Create a new item", Description = "Create a new item.")]
		[SwaggerResponse(201, "Created", typeof(ItemModel))]
		[SwaggerResponse(400, "Bad Request", typeof(string))]
		[HttpPost, MapToApiVersion("1.0")]
		public async Task<IActionResult> Post([FromBody] ItemModel value) {
			try {
				var item = _mapper.Map<ItemModel, Item>(value);

				await _itemService.Create(item);

				return CreatedAtAction("Get", new { id = item.Id }, item);
			}
			catch (Exception e) {

				return BadRequest(e.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Update an existing item", Description = "Update an existing item.")]
		[SwaggerResponse(204, "No Content")]
		[SwaggerResponse(404, "Not Found", typeof(string))]
		[SwaggerResponse(400, "Bad Request", typeof(string))]
		[HttpPut, MapToApiVersion("1.0")]
		public async Task<ActionResult<ItemModel>> Put([FromBody] ItemModel model) {
			try {
				var item = _mapper.Map<ItemModel, Item>(model);

				if (item is null) return NotFound();//404

				await _itemService.Update(item);

				return NoContent(); //202
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Delete an existing item", Description = "Delete an existing item.")]
		[SwaggerResponse(204, "No Content")]
		[SwaggerResponse(404, "Not Found", typeof(string))]
		[SwaggerResponse(400, "Bad Request", typeof(string))]
		[HttpDelete, MapToApiVersion("1.0")]
		public async Task<IActionResult> Delete([FromBody] ItemModel model) {
			try {
				var item = _mapper.Map<ItemModel, Item>(model);
				var warehouse = await _warehouseService.GetById(item.WarehouseId);

				warehouse.IdItems = Utility.Utility.RemoveSpecificId(warehouse.IdItems, item.Id);



				if (item is null) return NotFound();//404
				if (Uri.IsWellFormedUriString(item.Url, UriKind.Absolute)) {
					// Es una URL válida, puedes proceder con la eliminación
					await _context_storage.DeleteImage(item.Url);
				}

				await _itemService.Delete(item);
				await _warehouseService.Update(warehouse);




				return NoContent(); //202
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Search items by name", Description = "Retrieve items by name.")]
		[SwaggerResponse(200, "OK", typeof(IEnumerable<ItemModel>))]
		[SwaggerResponse(404, "Not Found", typeof(string))]
		[SwaggerResponse(400, "Bad Request", typeof(string))]
		[HttpGet("Search/{name}"), MapToApiVersion("1.0")]
		public async Task<ActionResult<IEnumerable<ItemModel>>> GetItemsByName(string name) {
			try {
				var items = await _itemService.getItems(name);

				if (items is null) return NotFound();//404

				return Ok(_mapper.Map<IEnumerable<Item>, IEnumerable<ItemModel>>(items)); //200
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Update item image", Description = "Update item image.")]
		[SwaggerResponse(200, "OK")]
		[SwaggerResponse(404, "Not Found", typeof(string))]
		[SwaggerResponse(400, "Bad Request", typeof(string))]
		[HttpPost("UpdateImage"), MapToApiVersion("1.0")]
		public async Task<IActionResult> UpdateImage([FromForm] int itemId, [FromForm] IFormFile image) {
			try {

				var item = await _itemService.GetById(itemId);
				if (item == null) return NotFound();

				if (Uri.IsWellFormedUriString(item.Url, UriKind.Absolute)) {
					// Es una URL válida, puedes proceder con la eliminación
					await _context_storage.DeleteImage(item.Url);
				}
				Stream image_stream = image.OpenReadStream();
				string urlimagen = await _context_storage.UploadImage(image_stream, image.FileName);

				item.Url = urlimagen;

				await _itemService.Update(item);
				return Ok();
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);
			}
		}
	}
}
