using Microsoft.IdentityModel.Tokens;
using QRStockMate.AplicationCore.Interfaces.Repositories;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;

namespace QRStockMate.Utility
{
    public class JwtTokenRepository: IJwtTokenRepository
    {
        public readonly IConfiguration configuration;

        public JwtTokenRepository(IConfiguration _configuration)
        {

            configuration = _configuration;
        }
        public string GenToken(string email, string passwd)
        {

            var claims = new[]
            {
                new Claim("email", email),
                new Claim("password", passwd),
            };

            var llave = new SymmetricSecurityKey(
                Encoding.UTF8.GetBytes(configuration.GetSection("ConfigJwt:Key").Get<string>() ?? string.Empty));

            var credentials = new SigningCredentials(llave, SecurityAlgorithms.HmacSha256);

            var token = new JwtSecurityToken(
                issuer: null,
                audience: null,
                claims,
                expires: DateTime.Now.AddMinutes(60),
                signingCredentials: credentials
                );

            return new JwtSecurityTokenHandler().WriteToken(token);

        }
    }
}
