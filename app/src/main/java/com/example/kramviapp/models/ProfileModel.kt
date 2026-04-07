package com.example.kramviapp.models

data class ProfileModel(
    var business: BusinessModel = BusinessModel(),
    var office: OfficeModel = OfficeModel(),
    var setting: SettingModel = SettingModel(),
    var activeModule: ActiveModuleModel = ActiveModuleModel(),
    var user: UserModel = UserModel(),
)
