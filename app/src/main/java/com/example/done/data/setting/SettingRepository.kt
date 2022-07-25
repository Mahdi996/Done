package com.example.done.data.setting

interface SettingRepository {

    fun loadSetting()

    fun saveSetting(hide: Boolean,sort:Int)
}