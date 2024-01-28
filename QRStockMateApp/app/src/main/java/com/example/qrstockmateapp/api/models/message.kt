package com.example.qrstockmateapp.api.models

data class Message(
    val id: Int,
    val code: String,
    val senderContactId: Int,
    val receiverContactId: Int,
    val content: String,
    val sentDate: String, // O puedes usar DateTime si estás utilizando alguna biblioteca de fechas en Kotlin
    val type: Int
)
