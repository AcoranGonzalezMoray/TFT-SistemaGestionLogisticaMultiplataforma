export interface Warehouse {
    id: number;
    name: string;
    location: string;
    organization: string;
    idAdministrator: number;
    idItems: string;
    url: string;
    latitude: number;  // Propiedad para almacenar la latitud
    longitude: number; // Propiedad para almacenar la longitud
}

