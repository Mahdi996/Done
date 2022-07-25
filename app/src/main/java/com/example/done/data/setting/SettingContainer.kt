package com.example.done.data.setting

import com.example.done.R

object SettingContainer {

    var hide: Boolean = false
        private set

    var sort: Int = R.id.showByName
        private set

    fun updateSettings(hide: Boolean, sort: Int) {
        SettingContainer.hide = hide
        SettingContainer.sort = sort
    }
}