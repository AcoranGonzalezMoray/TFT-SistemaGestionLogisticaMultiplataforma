package com.example.qrstockmateapp.api.models

data class TransportRoute(
    val id: Int,
    val code: String,
    var startLocation: String,  // Ubicación de inicio de la ruta
    var endLocation: String,    // Ubicación de fin de la ruta
    val departureTime: String, // Hora de salida
    val arrivalTime: String,   // Hora de llegada
    var palets: String, // Empaquetado de gran número de productos limit : [1;2;4;4;5,2;4;3;] (, palet) (; producto)
    var assignedVehicleId: Int, // Vehiculo asignados a la ruta
    var carrierId: Int, // Conductor
    var date: String,
    val status: Int,
    var route: String
)

fun statusRoleToString(roleId: Int): String {
    return when (roleId) {
        0 -> "Pending"
        1 -> "On Route"
        2 -> "Finalized"
        else -> "Unknown Status"
    }
}
