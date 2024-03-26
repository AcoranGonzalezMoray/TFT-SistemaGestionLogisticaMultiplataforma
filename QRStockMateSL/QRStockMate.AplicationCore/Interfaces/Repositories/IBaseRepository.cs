namespace QRStockMate.AplicationCore.Interfaces.Repositories {
	public interface IBaseRepository<TEntity> where TEntity : class {
		Task<TEntity> GetById(int id);
		Task<IEnumerable<TEntity>> GetAll();
		Task Delete(TEntity entity);
		Task DeleteRange(IEnumerable<TEntity> entities);
		Task Update(TEntity entity);
		Task UpdateRange(IEnumerable<TEntity> entities);
		Task Create(TEntity entity);
	}
}
