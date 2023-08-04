package com.example.myfirstapp

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat

class SystemSettings : AppCompatActivity() {

  private lateinit var systemSettingsBackImageView: ImageView
  private lateinit var systemSettingsFollowDarkModeSwitch: Switch
  private lateinit var systemSettingsDarkModeSwitch: Switch
  private lateinit var systemSettingsDarkModeTextView: TextView
  private lateinit var sharedPreferences: SharedPreferences

  private var isFollowDarkMode: Boolean = false
  private var isDarkMode: Boolean = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_system_settings)

    systemSettingsBackImageView = findViewById(R.id.system_settings_back_iv)
    systemSettingsFollowDarkModeSwitch = findViewById(R.id.system_settings_follow_dark_mode_swt)
    systemSettingsDarkModeSwitch = findViewById(R.id.system_settings_dark_mode_swt)
    systemSettingsDarkModeTextView = findViewById(R.id.system_settings_dark_mode_tv)

    sharedPreferences = getSharedPreferences("system_settings", MODE_PRIVATE)
    isFollowDarkMode = getCheckedFollowDarkModeSwitch()
    isDarkMode = getCheckedDarkModeSwitch()
    updateFollowDarkModeSwitchState(isFollowDarkMode)
    updateDarkModeSwitchState(isDarkMode)
    updateAppTheme(isFollowDarkMode, isDarkMode)

    systemSettingsFollowDarkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
      saveFollowDarkModeState(isChecked)
      getCheckedDarkModeSwitch()
      updateDarkModeSwitchState(getCheckedDarkModeSwitch())
      updateAppTheme(isChecked, isDarkMode)
      if (isChecked) {
        systemSettingsDarkModeTextView.setTextColor(ContextCompat.getColor(this, R.color.grey))
      } else {
        updateAppTheme(false, getCheckedDarkModeSwitch())
        systemSettingsDarkModeTextView.setTextColor(ContextCompat.getColor(this, R.color.black))
      }
    }

    systemSettingsDarkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
      saveDarkModeState(isChecked)
      updateAppTheme(false, isChecked)
    }

    systemSettingsBackImageView.setOnClickListener {
      onBackPressed()
    }

  }

  private fun updateDarkModeSwitchState(isChecked: Boolean) {
    systemSettingsDarkModeSwitch.isChecked = isChecked
    systemSettingsDarkModeSwitch.isEnabled = !getCheckedFollowDarkModeSwitch()
  }

  private fun updateFollowDarkModeSwitchState(isChecked: Boolean) {
    systemSettingsFollowDarkModeSwitch.isChecked = isChecked
  }

  private fun saveDarkModeState(isChecked: Boolean) {
    val editor = sharedPreferences.edit()
    editor.putBoolean("dark_mode", isChecked)
    editor.apply()
  }

  private fun saveFollowDarkModeState(isChecked: Boolean) {
    val editor = sharedPreferences.edit()
    editor.putBoolean("follow_dark_mode", isChecked)
    editor.apply()
  }

  private fun getCheckedFollowDarkModeSwitch(): Boolean {
    return sharedPreferences.getBoolean("follow_dark_mode", false)
  }

  private fun getCheckedDarkModeSwitch(): Boolean {
    return sharedPreferences.getBoolean("dark_mode", false)
  }

  private fun updateAppTheme(isFollowDarkMode: Boolean, isDarkMode: Boolean) {
    if (isFollowDarkMode) {
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    } else {
      val nightMode =
        if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
      AppCompatDelegate.setDefaultNightMode(nightMode)
    }
  }
}