import { View } from "./view";

export interface Dashboard {
    nombre: string;
    map: MapDash[];
    count: Count[];
    vista: View[];
}

export interface Count {
    title: string;
    posicion:string
}

export interface MapDash {
    map: string;
    posicion:string
}