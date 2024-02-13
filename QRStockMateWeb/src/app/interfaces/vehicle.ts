export interface Vehicle {
    id: number;
    code: string;
    make: string;  // Fabricante (por ejemplo, Toyota, Ford, etc.)
    model: string;  // Modelo del vehículo
    year: number;  // Año de fabricación
    color: string;  // Color del vehículo
    licensePlate: string;  // Matrícula del vehículo
    maxLoad: number;  // Carga máxima del vehículo	
    location: string;  // Ubicacion cada cierto tiempo
}
