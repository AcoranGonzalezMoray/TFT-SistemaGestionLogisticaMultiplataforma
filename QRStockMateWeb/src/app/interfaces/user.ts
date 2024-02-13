import { Dashboard } from "./dashboard";

export interface User {
    nombre: string;
    apellido: string;
    data: {
      dashboards: Dashboard[];
    };
  }
