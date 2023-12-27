package com.example.qrstockmateapp.api.models

data class Warehouse(

    val id:Int,
    var name:String,
    var location:String,
    var organization:String,
    var idAdministrator:Int,
    val idItems:String,
    var url:String
)