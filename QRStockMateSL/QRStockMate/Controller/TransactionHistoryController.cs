using AutoMapper;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using QRStockMate.AplicationCore.Entities;
using QRStockMate.AplicationCore.Interfaces.Services;
using QRStockMate.Model;
using QRStockMate.Services;
using System.Transactions;

namespace QRStockMate.Controller
{
    [Route("api/[controller]")]
    [ApiController]
    public class TransactionHistoryController : ControllerBase
    {
        private readonly ITransactionHistoryService _transactionHistoryService;
        private readonly IMapper _mapper;

        public TransactionHistoryController(ITransactionHistoryService transactionHistoryService, IMapper mapper)
        {
            _transactionHistoryService = transactionHistoryService;
            _mapper = mapper;
        }




        //FUNCIONES BASICAS

        [HttpGet]
        public async Task<ActionResult<IEnumerable<TransactionHistoryModel>>> Get()
        {
            try
            {
                var th = await _transactionHistoryService.GetAll();

                if (th is null) return NotFound();//404

                return Ok(_mapper.Map<IEnumerable<TransactionHistory>, IEnumerable<TransactionHistoryModel>>(th)); //200
            }
            catch (Exception ex)
            {

                return BadRequest(ex.Message);//400
            }
        }

        [HttpPost]
        public async Task<IActionResult> Post([FromBody] TransactionHistoryModel value)
        {

            try
            {
                var th= _mapper.Map<TransactionHistoryModel, TransactionHistory>(value);

                th.Created = DateTime.Now;

                await _transactionHistoryService.Create(th);

                return CreatedAtAction("Get", new { id = th.Id }, th);
            }
            catch (Exception e)
            {

                return BadRequest(e.Message);//400
            }
        }

        [HttpPut]
        public async Task<ActionResult<TransactionHistoryModel>> Put([FromBody] TransactionHistoryModel model)
        {
            try
            {
                var th = _mapper.Map<TransactionHistoryModel, TransactionHistory>(model);

                th.Created = DateTime.Now;

                if (th is null) return NotFound();//404

                await _transactionHistoryService.Update(th);

                return NoContent(); //202
            }
            catch (Exception ex)
            {

                return BadRequest(ex.Message);//400
            }
        }

        [HttpDelete]
        public async Task<IActionResult> Delete([FromBody] TransactionHistoryModel model)
        {
            try
            {
                var th = _mapper.Map<TransactionHistoryModel, TransactionHistory>(model);

                if (th is null) return NotFound();//404

                //await _context_storage.DeleteImage(user.Url);
                await _transactionHistoryService.Delete(th);

                return NoContent(); //202
            }
            catch (Exception ex)
            {

                return BadRequest(ex.Message);//400
            }
        }










    }
}
