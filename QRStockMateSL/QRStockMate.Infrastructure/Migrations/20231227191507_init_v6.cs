using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace QRStockMate.Infrastructure.Migrations {
	/// <inheritdoc />
	public partial class init_v6 : Migration {
		/// <inheritdoc />
		protected override void Up(MigrationBuilder migrationBuilder) {
			migrationBuilder.AlterColumn<decimal>(
				name: "MaxLoad",
				table: "Vehicles",
				type: "decimal(8,2)",
				nullable: false,
				oldClrType: typeof(decimal),
				oldType: "decimal(6,4)");

			migrationBuilder.AlterColumn<decimal>(
				name: "Weight",
				table: "Items",
				type: "decimal(7,2)",
				nullable: false,
				oldClrType: typeof(decimal),
				oldType: "decimal(6,4)");
		}

		/// <inheritdoc />
		protected override void Down(MigrationBuilder migrationBuilder) {
			migrationBuilder.AlterColumn<decimal>(
				name: "MaxLoad",
				table: "Vehicles",
				type: "decimal(6,4)",
				nullable: false,
				oldClrType: typeof(decimal),
				oldType: "decimal(8,2)");

			migrationBuilder.AlterColumn<decimal>(
				name: "Weight",
				table: "Items",
				type: "decimal(6,4)",
				nullable: false,
				oldClrType: typeof(decimal),
				oldType: "decimal(7,2)");
		}
	}
}
