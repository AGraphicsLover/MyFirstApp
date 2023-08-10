package com.example.myfirstapp.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.example.myfirstapp.R

class SystemSettingsActivity : AppCompatActivity() {

  private lateinit var systemSettingsBackImageView: ImageView
  private lateinit var systemSettingsFollowDarkModeSwitch: Switch
  private lateinit var systemSettingsDarkModeSwitch: Switch
  private lateinit var systemSettingsHideAuthorSwitch: Switch
  private lateinit var systemSettingsHideGithubSwitch: Switch
  private lateinit var systemSettingsDarkModeTextView: TextView
  private lateinit var sharedPreferences: SharedPreferences

  private var isFollowDarkMode: Boolean = false
  private var isDarkMode: Boolean = false
  private var isHideAuthor: Boolean = false
  private var isHideGithub: Boolean = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_system_settings)

    systemSettingsBackImageView = findViewById(R.id.system_settings_back_iv)
    systemSettingsFollowDarkModeSwitch = findViewById(R.id.system_settings_follow_dark_mode_swt)
    systemSettingsDarkModeSwitch = findViewById(R.id.system_settings_dark_mode_swt)
    systemSettingsHideAuthorSwitch = findViewById(R.id.system_settings_hide_author_swt)
    systemSettingsDarkModeTextView = findViewById(R.id.system_settings_dark_mode_tv)
    systemSettingsHideGithubSwitch = findViewById(R.id.system_settings_hide_github_swt)

    sharedPreferences = getSharedPreferences("system_settings", MODE_PRIVATE)
    isFollowDarkMode = sharedPreferences.getBoolean("follow_dark_mode", false)
    isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
    isHideAuthor = sharedPreferences.getBoolean("hide_author", false)
    isHideGithub = sharedPreferences.getBoolean("hide_github", false)
    systemSettingsHideGithubSwitch.isChecked = isHideGithub
    systemSettingsHideAuthorSwitch.isChecked = isHideAuthor
    systemSettingsFollowDarkModeSwitch.isChecked = isFollowDarkMode
    updateDarkModeSwitchState(isDarkMode)
    updateAppTheme(isFollowDarkMode, isDarkMode)

    systemSettingsHideAuthorSwitch.setOnCheckedChangeListener { _, isChecked ->
      saveHideAuthorState(isChecked)
    }

    systemSettingsHideGithubSwitch.setOnCheckedChangeListener { _, isChecked ->
      saveHideGithubState(isChecked)
    }

    systemSettingsFollowDarkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
      saveFollowDarkModeState(isChecked)
      updateDarkModeSwitchState(isDarkMode)
      updateAppTheme(isChecked, sharedPreferences.getBoolean("dark_mode", false))
      if (isChecked) {
        systemSettingsDarkModeTextView.setTextColor(ContextCompat.getColor(this, R.color.grey))
      } else {
        systemSettingsDarkModeTextView.setTextColor(ContextCompat.getColor(this, R.color.black))
      }
    }

    systemSettingsDarkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
      saveDarkModeState(isChecked)
      updateAppTheme(false, isChecked)
    }

    systemSettingsBackImageView.setOnClickListener {
      finish()
    }

  }

  private fun updateDarkModeSwitchState(isChecked: Boolean) {
    systemSettingsDarkModeSwitch.isChecked = isChecked
    systemSettingsDarkModeSwitch.isEnabled =
      !sharedPreferences.getBoolean("follow_dark_mode", false)
  }

  private fun saveHideGithubState(isChecked: Boolean) {
    val editor = sharedPreferences.edit()
    editor.putBoolean("hide_github", isChecked)
    editor.apply()
  }

  private fun saveHideAuthorState(isChecked: Boolean) {
    val editor = sharedPreferences.edit()
    editor.putBoolean("hide_author", isChecked)
    editor.apply()
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