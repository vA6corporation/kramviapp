package com.example.kramviapp.models

import android.os.Bundle

data class UserModel(
    val name: String = "",
    val email: String = "",
    val isAdmin: Boolean = false,
    val activeModule: Map<String, Boolean> = mapOf(),
    val officeId: Int? = 0
)
