namespace QRStockMate.Model
{
    public class WarehouseModel
    {
        public int Id { get; set; }
        public string Name { get; set; }
        public string Location { get; set; }
        public string Organization { get; set; }
        public int IdAdministrator { get; set; }
        public string IdItems { get; set; }
        public string Url { get; set; }
    }
}
