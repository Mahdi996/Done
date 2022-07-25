package com.example.done.data.setting

import android.content.SharedPreferences
import com.example.done.R

class SettingRepositoryImpl(private val sharedPreferences: SharedPreferences) : SettingRepository {

    override fun loadSetting() {
        SettingContainer.updateSettings(
            sharedPreferences.getBoolean("hide", false),
            sharedPreferences.getInt("sort", R.id.showByName)
        )
    }

    override fun saveSetting(hide: Boolean, sort: Int) {
        sharedPreferences.edit().apply {
            putBoolean("hide", hide)
            putInt("sort", sort)
        }.apply()
        loadSetting()
    }
}