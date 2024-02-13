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
    Pending,
    OnRoute,
    Finalized
}
