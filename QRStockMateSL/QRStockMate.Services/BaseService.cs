using QRStockMate.AplicationCore.Interfaces.Repositories;
using QRStockMate.AplicationCore.Interfaces.Services;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace QRStockMate.Services
{
    public class BaseService<TEntity>:IBaseService<TEntity> where TEntity : class
    {
        private readonly IBaseRepository<TEntity> _tRepository;
        public BaseService(IBaseRepository<TEntity> _Repository)
        {
            _tRepository = _Repository;
        }
        public async Task<TEntity> GetById(int id)
        {
          return await _tRepository.GetById(id);
        }

        public async Task<IEnumerable<TEntity>> GetAll()
        {
            return await _tRepository.GetAll();
        }

        public async Task Delete(TEntity entity)
        {
            await _tRepository.Delete(entity);
        }

        public async Task DeleteRange(IEnumerable<TEntity> entities)
        {
           await _tRepository.DeleteRange(entities);
        }

        public async Task Update(TEntity entity)
        {
            await _tRepository.Update(entity);
        }

        public async Task UpdateRange(IEnumerable<TEntity> entities)
        {
            await _tRepository.UpdateRange(entities); 
        }

        public async Task Create(TEntity entity)
        {
            await _tRepository.Create(entity);
        }
    }
}
