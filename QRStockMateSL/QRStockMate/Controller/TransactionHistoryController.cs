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
	[SwaggerTag("Endpoints related to transactions management.")]
	public class TransactionHistoryController : ControllerBase {
		private readonly ITransactionHistoryService _transactionHistoryService;
		private readonly IMapper _mapper;

		public TransactionHistoryController(ITransactionHistoryService transactionHistoryService, IMapper mapper) {
			_transactionHistoryService = transactionHistoryService;
			_mapper = mapper;
		}


		[SwaggerOperation(Summary = "Get all transaction history", Description = "Retrieves all transaction history.")]
		[SwaggerResponse(StatusCodes.Status200OK, "OK", typeof(IEnumerable<TransactionHistoryModel>))]
		[SwaggerResponse(StatusCodes.Status404NotFound, "Not Found", typeof(void))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpGet, MapToApiVersion(1.0)]
		public async Task<ActionResult<IEnumerable<TransactionHistoryModel>>> Get() {
			try {
				var th = await _transactionHistoryService.GetAll();

				if (th is null) return NotFound();//404

				return Ok(_mapper.Map<IEnumerable<TransactionHistory>, IEnumerable<TransactionHistoryModel>>(th)); //200
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Create transaction history", Description = "Creates a new transaction history.")]
		[SwaggerResponse(StatusCodes.Status201Created, "Created", typeof(TransactionHistory))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpPost, MapToApiVersion(1.0)]
		public async Task<IActionResult> Post([FromBody] TransactionHistoryModel value) {

			try {
				var th = _mapper.Map<TransactionHistoryModel, TransactionHistory>(value);

				th.Created = DateTime.Now;

				await _transactionHistoryService.Create(th);

				return CreatedAtAction("Get", new { id = th.Id }, th);
			}
			catch (Exception e) {

				return BadRequest(e.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Update transaction history", Description = "Updates an existing transaction history.")]
		[SwaggerResponse(StatusCodes.Status204NoContent, "No Content", typeof(void))]
		[SwaggerResponse(StatusCodes.Status404NotFound, "Not Found", typeof(void))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpPut, MapToApiVersion(1.0)]
		public async Task<ActionResult<TransactionHistoryModel>> Put([FromBody] TransactionHistoryModel model) {
			try {
				var th = _mapper.Map<TransactionHistoryModel, TransactionHistory>(model);

				th.Created = DateTime.Now;

				if (th is null) return NotFound();//404

				await _transactionHistoryService.Update(th);

				return NoContent(); //202
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Delete transaction history", Description = "Deletes an existing transaction history.")]
		[SwaggerResponse(StatusCodes.Status204NoContent, "No Content", typeof(void))]
		[SwaggerResponse(StatusCodes.Status404NotFound, "Not Found", typeof(void))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpDelete, MapToApiVersion(1.0)]
		public async Task<IActionResult> Delete([FromBody] TransactionHistoryModel model) {
			try {
				var th = _mapper.Map<TransactionHistoryModel, TransactionHistory>(model);

				if (th is null) return NotFound();//404

				//await _context_storage.DeleteImage(user.Url);
				await _transactionHistoryService.Delete(th);

				return NoContent(); //202
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}

		[SwaggerOperation(Summary = "Get transaction history by code", Description = "Retrieves transaction history by code.")]
		[SwaggerResponse(StatusCodes.Status200OK, "OK", typeof(IEnumerable<TransactionHistoryModel>))]
		[SwaggerResponse(StatusCodes.Status404NotFound, "Not Found", typeof(void))]
		[SwaggerResponse(StatusCodes.Status400BadRequest, "Bad Request", typeof(void))]
		[HttpGet("History/{code}"), MapToApiVersion(1.0)]
		public async Task<ActionResult<IEnumerable<TransactionHistoryModel>>> GetHistory(string code) {
			try {
				var th = await _transactionHistoryService.GetTransactionHistoryByCode(code);

				if (th is null) return NotFound();//404

				return Ok(_mapper.Map<IEnumerable<TransactionHistory>, IEnumerable<TransactionHistoryModel>>(th)); //200
			}
			catch (Exception ex) {

				return BadRequest(ex.Message);//400
			}
		}




	}
}
