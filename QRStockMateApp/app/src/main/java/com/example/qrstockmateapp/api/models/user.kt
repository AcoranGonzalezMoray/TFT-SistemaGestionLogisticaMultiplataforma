package com.example.qrstockmateapp.api.models

data class User(
    val id: Int,
    var name: String,
    var email: String,
    val password: String,
    var phone: String,
    val code: String,
    val url: String,
    var role: Int,

    )

fun userRoleToString(roleId: Int): String {
    return when (roleId) {
        0 -> "Director"
        1 -> "Administrator"
        2 -> "Inventory Technician"
        3 -> "User"
        else -> "Unknown Role"
    }
}
