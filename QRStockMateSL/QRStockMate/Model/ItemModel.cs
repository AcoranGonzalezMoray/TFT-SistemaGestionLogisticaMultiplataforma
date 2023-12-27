namespace QRStockMate.Model
{
    public class ItemModel
    {
        public int Id { get; set; }

        public string Name { get; set; }

        public int WarehouseId { get; set; }

        public string Location { get; set; }

        public int Stock { get; set; }

        public string Url { get; set; }
    }
}
