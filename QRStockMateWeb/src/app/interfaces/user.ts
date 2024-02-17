export interface User {
    id: number;
    name: string;
    email: string;
    password: string;
    phone: string;
    code: string;
    url: string;
    role: number|string;
  }
  
  export enum RoleUser {
    Director = 'Director',
    Administrator = 'Administrator',
    InventoryTechnician = 'InventoryTech',
    User = 'User',
    Carrier = 'Carrier'
  }
  
  export function getRoleUser(number: number): string {
    // Usa la sintaxis EnumName.EnumMember para acceder al valor del enumerado
    switch (number) {
      case 0:
        return RoleUser.Director;
      case 1:
        return RoleUser.Administrator;
      case 2:
        return RoleUser.InventoryTechnician;
      case 3:
        return RoleUser.User;
      case 4:
        return RoleUser.Carrier;
      default:
        return 'Unknown'; // Opcional: devuelve un valor predeterminado para números desconocidos
    }
  }
  export function getRoleUserString(role: string): number {
    // Utiliza una estructura de selección para asignar el número correspondiente al rol
    switch (role) {
      case RoleUser.Director:
        return 0;
      case RoleUser.Administrator:
        return 1;
      case RoleUser.InventoryTechnician:
        return 2;
      case RoleUser.User:
        return 3;
      case RoleUser.Carrier:
        return 4;
      default:
        return -1; // Opcional: devuelve un valor predeterminado para roles desconocidos
    }
  }
  