using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace QRStockMate.Infrastructure.Migrations {
	/// <inheritdoc />
	public partial class init_v12 : Migration {
		/// <inheritdoc />
		protected override void Up(MigrationBuilder migrationBuilder) {
			migrationBuilder.AddColumn<string>(
				name: "Route",
				table: "TransportRoutes",
				type: "nvarchar(max)",
				nullable: false,
				defaultValue: "");
		}

		/// <inheritdoc />
		protected override void Down(MigrationBuilder migrationBuilder) {
			migrationBuilder.DropColumn(
				name: "Route",
				table: "TransportRoutes");
		}
	}
}
