import { Dashboard } from "./dashboard";

export interface Data {
    id: number;
    idUser:number;
    nombre: string;
    apellido: string;
    data: {
      dashboards: Dashboard[];
    };
  }
