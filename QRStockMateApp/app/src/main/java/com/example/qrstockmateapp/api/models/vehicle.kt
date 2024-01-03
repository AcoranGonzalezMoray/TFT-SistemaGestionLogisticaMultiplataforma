package com.example.qrstockmateapp.api.models

data class Vehicle(
    val id: Int,
    val code: String,
    val make: String,  // Fabricante (por ejemplo, Toyota, Ford, etc.)
    val model: String,  // Modelo del vehículo
    val year: Int,  // Año de fabricación
    val color: String,  // Color del vehículo
    val licensePlate: String,  // Matrícula del vehículo
    val maxLoad: Double,  // Carga máxima del vehículo
    val location: String  // Ubicacion cada cierto tiempo
)
