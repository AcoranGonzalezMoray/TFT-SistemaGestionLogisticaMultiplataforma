export interface TransportRoute {
    id: number;
    code: string;
    startLocation: string;  // Ubicación de inicio de la ruta
    endLocation: string;    // Ubicación de fin de la ruta
    departureTime: Date; // Hora de salida
    arrivalTime: Date;   // Hora de llegada
    palets: string; // Empaquetado de gran número de productos limit : [1;2;4;4;5,2;4;3;] (, palet) (; producto)
    assignedVehicleId: number; // Vehículo asignado a la ruta
    carrierId: number; // Conductor
    date: Date;
    status: RoleStatus;
    route: string;
}

export enum RoleStatus {
    Pending="Pending",
    OnRoute="On Route",
    Finalized="Finalized"
}

export function getRoleStatus(number: number): string {
    // Usa la sintaxis EnumName.EnumMember para acceder al valor del enumerado
    switch (number) {
      case 0:
        return RoleStatus.Pending;
      case 1:
        return RoleStatus.OnRoute;
      case 2:
        return RoleStatus.Finalized;
      default:
        return 'Unknown'; // Opcional: devuelve un valor predeterminado para números desconocidos
    }
  }