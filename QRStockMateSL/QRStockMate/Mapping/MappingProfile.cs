using AutoMapper;
using QRStockMate.AplicationCore.Entities;
using QRStockMate.Model;

namespace CleanArquitecture.Api.Mapping
{
    public class MappingProfile : Profile
    {
        public MappingProfile()
        {
            //User
            CreateMap<User, UserModel>();
            CreateMap<UserModel, User>();

            //Company
            CreateMap<Company, CompanyModel>();
            CreateMap<CompanyModel, Company>();

            //Item
            CreateMap<Item, ItemModel>();
            CreateMap<ItemModel, Item>();

            //TransactionHistory
            CreateMap<TransactionHistory, TransactionHistoryModel>();
            CreateMap<TransactionHistoryModel, TransactionHistory>();

            //Warehouse
            CreateMap<Warehouse, WarehouseModel>();
            CreateMap<WarehouseModel, Warehouse>();
        }
    }
}
