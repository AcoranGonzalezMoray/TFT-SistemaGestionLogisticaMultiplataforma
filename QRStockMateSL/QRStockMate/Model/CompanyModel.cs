namespace QRStockMate.Model
{
    public class CompanyModel
    {
        public int Id { get; set; }
        public string Name { get; set; }
        public string Director { get; set; }
        public string Location { get; set; }
        public string Code { get; set; }
        public string WarehouseId { get; set; }   //algo;otro;....
        public string EmployeeId { get; set; }   //algo;otro;....
    }
}
