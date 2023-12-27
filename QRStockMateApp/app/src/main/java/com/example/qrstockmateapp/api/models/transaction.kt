package com.example.qrstockmateapp.api.models

import java.time.LocalDateTime

data class Transaction(
    val id: Int,
    val name: String,
    val code: String,
    val description: String,
    val created: String,
    val operation: Int
)

enum class OperationHistory {
    Add,
    Post,
    Put,
    Delete
}

fun operationToString(roleId: Int): String {
    return when (roleId) {
        0 -> "ADD"
        1 -> "POST"
        2 -> "PUT"
        3 -> "DELETE"
        else -> "Unknown Role"
    }
}