import { View } from "./view";

export interface Dashboard {
    nombre: string;
    count: Count[];
    vista: View[];
}

export interface Count {
    title: string;
    posicion:string
}