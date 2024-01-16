using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace QRStockMate.Infrastructure.Migrations
{
    /// <inheritdoc />
    public partial class init_v11 : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.RenameColumn(
                name: "Weight",
                table: "Items",
                newName: "WeightPerUnit");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.RenameColumn(
                name: "WeightPerUnit",
                table: "Items",
                newName: "Weight");
        }
    }
}
