import { View } from "./view";

export interface Dashboard {
    nombre: string;
    widget: Widget[];
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

export interface Widget {
    name: string;
    objectT: Task[];
    posicion: string;
}

export interface Task {
    name: string;
    completed: boolean;
}