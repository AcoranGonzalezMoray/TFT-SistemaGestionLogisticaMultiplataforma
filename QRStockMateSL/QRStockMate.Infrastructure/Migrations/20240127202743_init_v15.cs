﻿using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace QRStockMate.Infrastructure.Migrations {
	/// <inheritdoc />
	public partial class init_v15 : Migration {
		/// <inheritdoc />
		protected override void Up(MigrationBuilder migrationBuilder) {
			migrationBuilder.AddColumn<int>(
				name: "Type",
				table: "Messages",
				type: "int",
				nullable: false,
				defaultValue: 0);
		}

		/// <inheritdoc />
		protected override void Down(MigrationBuilder migrationBuilder) {
			migrationBuilder.DropColumn(
				name: "Type",
				table: "Messages");
		}
	}
}
