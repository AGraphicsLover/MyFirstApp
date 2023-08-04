package com.example.myfirstapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.bottomnavigation.BottomNavigationView

class Person : AppCompatActivity() {

  private lateinit var logInTv: TextView
  private lateinit var logOutBtn: Button
  private lateinit var personAvatarLayout: LinearLayout
  private lateinit var personSettingLayout: LinearLayout
  private lateinit var personNightImageView: ImageView
  private lateinit var sharedPreferencesUserData: SharedPreferences
  private lateinit var sharedPreferencesSystemSettings: SharedPreferences

  private var username: String = ""
  private var isLogin: Boolean = false
  private var isDarkMode: Boolean = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_person)

    logInTv = findViewById(R.id.person_login_tv)
    logOutBtn = findViewById(R.id.person_logout_btn)
    personAvatarLayout = findViewById(R.id.person_avatar_layout)
    personSettingLayout = findViewById(R.id.person_my_setting_layout)
    personNightImageView = findViewById(R.id.person_night_iv)

    sharedPreferencesUserData = getSharedPreferences("user_data", MODE_PRIVATE)
    isLogin = sharedPreferencesUserData.getBoolean("isLogin", false)
    username = sharedPreferencesUserData.getString("username", "") ?: ""

    sharedPreferencesSystemSettings = getSharedPreferences("system_settings", MODE_PRIVATE)
    isDarkMode = sharedPreferencesSystemSettings.getBoolean("dark_mode", false)

    personNightImageView.setOnClickListener {
      isDarkMode = !isDarkMode
      saveDarkModeState(isDarkMode)
      updateAppTheme(isDarkMode)
    }

    logOutBtn.setOnClickListener {
      val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
      val editor = sharedPreferences.edit()
      editor.putBoolean("isLogin", false)
      editor.putString("username", "")
      editor.apply()

      updateUI(false, "")
      personAvatarLayout.setOnClickListener {
        val intent = Intent(this@Person, LogIn::class.java)
        startActivity(intent)
      }
    }

    updateUI(isLogin, username)

    if (isLogin) {
      personAvatarLayout.setOnClickListener {
        runOnUiThread {
          Toast.makeText(this@Person, "您已经登录啦！", Toast.LENGTH_SHORT).show()
        }
      }
    } else {
      personAvatarLayout.setOnClickListener {
        val intent = Intent(this@Person, LogIn::class.java)
        startActivity(intent)
      }
    }

    personSettingLayout.setOnClickListener {
      val intent = Intent(this@Person, SystemSettings::class.java)
      startActivity(intent)
    }

    val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
    bottomNavigationView.setOnItemSelectedListener { item ->
      when (item.itemId) {
        R.id.action_home -> {
          val intent = Intent(this, MainActivity::class.java)
          intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
          startActivity(intent)
          true
        }

        R.id.action_question -> {
          val intent = Intent(this, QuestionAnswer::class.java)
          intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
          startActivity(intent)
          true
        }

        R.id.action_system -> {
          val intent = Intent(this, System::class.java)
          intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
          startActivity(intent)
          true
        }

        R.id.action_profile -> {
          true
        }

        else -> false
      }

    }

    bottomNavigationView.selectedItemId = R.id.action_profile

  }

  private fun updateUI(isLogin: Boolean, username: String) {
    logInTv.text = if (isLogin) username else "去登录"
    logOutBtn.visibility = if (isLogin) View.VISIBLE else View.GONE
  }

  private fun saveDarkModeState(isChecked: Boolean) {
    val editor = sharedPreferencesSystemSettings.edit()
    editor.putBoolean("dark_mode", isChecked)
    editor.apply()
  }

  private fun updateAppTheme(isDarkMode: Boolean) {
    val nightMode =
      if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
    AppCompatDelegate.setDefaultNightMode(nightMode)
  }

}