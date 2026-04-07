package com.example.kramviapp.models

import com.example.kramviapp.enums.DocumentType

data class CustomerModel(
    val id: Int,
    val documentType: DocumentType,
    val document: String,
    val name: String,
    val address: String,
    val mobileNumber: String,
    val email: String,

)
