package com.example.kramviapp.models

data class OfficeModel(
    val _id: String = "",
    val name: String = "Casa matriz",
    val tradeName: String = "Nombre comercial",
    val address: String = "Direccion de empresa",
    val serialPrefix: String = "001",
    val mobileNumber: String = "999 999 999",
    val activityId: String = "",
    val setting: SettingModel = SettingModel(),
    val activeModule: ActiveModuleModel = ActiveModuleModel()
)