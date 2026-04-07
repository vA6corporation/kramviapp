package com.example.kramviapp.models

data class LoginResultModel(
    var accessToken: String,
    var business: BusinessModel,
    val user: UserModel,
    var setting: SettingModel?,
    var office: OfficeModel?,
    var activeModule: ActiveModuleModel?,
)
