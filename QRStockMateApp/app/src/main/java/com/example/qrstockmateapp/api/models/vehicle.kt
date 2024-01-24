package com.example.qrstockmateapp.api.models

data class Vehicle(
    val id: Int,
    val code: String,
    var make: String,  // Fabricante (por ejemplo, Toyota, Ford, etc.)
    var model: String,  // Modelo del vehículo
    var year: Int,  // Año de fabricación
    var color: String,  // Color del vehículo
    var licensePlate: String,  // Matrícula del vehículo
    var maxLoad: Double,  // Carga máxima del vehículo
    val location: String  // Ubicacion cada cierto tiempo
)
