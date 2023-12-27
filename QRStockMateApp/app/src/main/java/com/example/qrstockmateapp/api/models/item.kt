package com.example.qrstockmateapp.api.models

data class Item (

    val id:Int,
    var name:String,
    var warehouseId:Int,
    var location:String,
    var stock:Int,
    val url:String

)