package com.example.kramviapp.models

import android.os.Bundle

data class UserModel(
    val name: String = "",
    val email: String = "",
    val isAdmin: Boolean = false,
    val privileges: Map<String, Boolean> = mapOf(),
    val assignedOfficeId: String? = ""
)
