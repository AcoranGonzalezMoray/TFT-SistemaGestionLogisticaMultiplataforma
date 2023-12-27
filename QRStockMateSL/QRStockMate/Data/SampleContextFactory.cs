using Microsoft.EntityFrameworkCore.Design;
using Microsoft.EntityFrameworkCore;
using QRStockMate.Infrastructure.Data;

namespace CleanArquitecture.Api.Data
{
    public class SampleContextFactory : IDesignTimeDbContextFactory<ApplicationDbContext>
    {
        //IDesignTimeDbContextFactory en C#. Se utiliza en el contexto de Entity Framework 
        //Core para proporcionar una forma de crear una instancia de un contexto de base de
        //datos durante el tiempo de diseño, por ejemplo, cuando se ejecutan comandos de migración desde la línea de comandos
        //mientras que el código en el archivo de inicio se encarga de configurar y utilizar el contexto de la base de datos en tiempo de ejecución.
        public ApplicationDbContext CreateDbContext(string[] args)
        {
            IConfigurationRoot configuration = new ConfigurationBuilder()
                .SetBasePath(Directory.GetCurrentDirectory())
                .AddJsonFile("appsettings.json")
                .Build();
            var builder = new DbContextOptionsBuilder<ApplicationDbContext>();
            var connectionString = configuration.GetConnectionString("Conexion");
            builder.UseSqlServer(connectionString, b => b.MigrationsAssembly("QRStockMate.Infrastructure"));

            return new ApplicationDbContext(builder.Options);
        }
    }
}
