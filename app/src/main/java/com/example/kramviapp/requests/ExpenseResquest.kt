package com.example.kramviapp.requests

import com.example.kramviapp.models.CreateExpenseModel
import com.google.gson.annotations.SerializedName

data class ExpenseResquest(
    @SerializedName("expense") val expense: CreateExpenseModel
)