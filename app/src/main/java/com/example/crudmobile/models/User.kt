package com.example.crudmobile.models

data class User(
    val id: Long = Long.MIN_VALUE,
    val name: String = "",
    val email: String = "",
    val password: String = ""
)
