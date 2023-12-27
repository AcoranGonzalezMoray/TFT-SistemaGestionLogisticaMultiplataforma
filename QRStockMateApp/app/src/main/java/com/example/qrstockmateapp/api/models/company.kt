package com.example.qrstockmateapp.api.models

data class Company (
    val id:Int,
    val name:String,
    val director: String,
    val location: String,
    val code: String,
    val warehouseId: String,
    val employeeId: String,
)