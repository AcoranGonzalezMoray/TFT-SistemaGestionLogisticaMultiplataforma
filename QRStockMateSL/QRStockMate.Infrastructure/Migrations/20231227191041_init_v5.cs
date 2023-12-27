using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace QRStockMate.Infrastructure.Migrations
{
    /// <inheritdoc />
    public partial class init_v5 : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Items_TransportRoutes_TransportRouteId",
                table: "Items");

            migrationBuilder.DropForeignKey(
                name: "FK_TransportRoutes_Users_CarrierId",
                table: "TransportRoutes");

            migrationBuilder.DropForeignKey(
                name: "FK_TransportRoutes_Vehicles_AssignedVehicleId",
                table: "TransportRoutes");

            migrationBuilder.DropIndex(
                name: "IX_TransportRoutes_AssignedVehicleId",
                table: "TransportRoutes");

            migrationBuilder.DropIndex(
                name: "IX_TransportRoutes_CarrierId",
                table: "TransportRoutes");

            migrationBuilder.DropIndex(
                name: "IX_Items_TransportRouteId",
                table: "Items");

            migrationBuilder.DropColumn(
                name: "CurrentLoad",
                table: "Vehicles");

            migrationBuilder.DropColumn(
                name: "TransportRouteId",
                table: "Items");

            migrationBuilder.AlterColumn<decimal>(
                name: "MaxLoad",
                table: "Vehicles",
                type: "decimal(6,4)",
                nullable: false,
                oldClrType: typeof(decimal),
                oldType: "decimal(18,2)");

            migrationBuilder.AddColumn<string>(
                name: "Palets",
                table: "TransportRoutes",
                type: "nvarchar(max)",
                nullable: false,
                defaultValue: "");

            migrationBuilder.AlterColumn<decimal>(
                name: "Weight",
                table: "Items",
                type: "decimal(6,4)",
                nullable: false,
                oldClrType: typeof(decimal),
                oldType: "decimal(18,2)");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "Palets",
                table: "TransportRoutes");

            migrationBuilder.AlterColumn<decimal>(
                name: "MaxLoad",
                table: "Vehicles",
                type: "decimal(18,2)",
                nullable: false,
                oldClrType: typeof(decimal),
                oldType: "decimal(6,4)");

            migrationBuilder.AddColumn<decimal>(
                name: "CurrentLoad",
                table: "Vehicles",
                type: "decimal(18,2)",
                nullable: false,
                defaultValue: 0m);

            migrationBuilder.AlterColumn<decimal>(
                name: "Weight",
                table: "Items",
                type: "decimal(18,2)",
                nullable: false,
                oldClrType: typeof(decimal),
                oldType: "decimal(6,4)");

            migrationBuilder.AddColumn<int>(
                name: "TransportRouteId",
                table: "Items",
                type: "int",
                nullable: true);

            migrationBuilder.CreateIndex(
                name: "IX_TransportRoutes_AssignedVehicleId",
                table: "TransportRoutes",
                column: "AssignedVehicleId");

            migrationBuilder.CreateIndex(
                name: "IX_TransportRoutes_CarrierId",
                table: "TransportRoutes",
                column: "CarrierId");

            migrationBuilder.CreateIndex(
                name: "IX_Items_TransportRouteId",
                table: "Items",
                column: "TransportRouteId");

            migrationBuilder.AddForeignKey(
                name: "FK_Items_TransportRoutes_TransportRouteId",
                table: "Items",
                column: "TransportRouteId",
                principalTable: "TransportRoutes",
                principalColumn: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_TransportRoutes_Users_CarrierId",
                table: "TransportRoutes",
                column: "CarrierId",
                principalTable: "Users",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_TransportRoutes_Vehicles_AssignedVehicleId",
                table: "TransportRoutes",
                column: "AssignedVehicleId",
                principalTable: "Vehicles",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
