package com.example.myfirstapp.activity

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.myfirstapp.R

class PersonFragment : Fragment() {
  private lateinit var logInTv: TextView
  private lateinit var logOutBtn: Button
  private lateinit var personAvatarLayout: LinearLayout
  private lateinit var personSettingLayout: LinearLayout
  private lateinit var personAboutLayout: LinearLayout
  private lateinit var personGithubLayout: LinearLayout
  private lateinit var personNightImageView: ImageView
  private lateinit var sharedPreferencesUserData: SharedPreferences
  private lateinit var sharedPreferencesSystemSettings: SharedPreferences

  private var username: String = ""
  private var isLogin: Boolean = false
  private var isDarkMode: Boolean = false
  private var isHideAuthor: Boolean = false
  private var isHideGithub: Boolean = false

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.fragment_person, container, false)
    initView(view)
    return view
  }

  private fun initView(view: View) {
    logInTv = view.findViewById(R.id.person_login_tv)
    logOutBtn = view.findViewById(R.id.person_logout_btn)
    personAvatarLayout = view.findViewById(R.id.person_avatar_layout)
    personSettingLayout = view.findViewById(R.id.person_my_setting_layout)
    personNightImageView = view.findViewById(R.id.person_night_iv)
    personAboutLayout = view.findViewById(R.id.person_my_about_layout)
    personGithubLayout = view.findViewById(R.id.person_my_github_layout)
    sharedPreferencesUserData = requireActivity().getSharedPreferences(
      "user_data", AppCompatActivity
        .MODE_PRIVATE
    )
    isLogin = sharedPreferencesUserData.getBoolean("isLogin", false)
    username = sharedPreferencesUserData.getString("username", "") ?: ""

    sharedPreferencesSystemSettings = requireActivity().getSharedPreferences(
      "system_settings",
      AppCompatActivity.MODE_PRIVATE
    )
    isDarkMode = sharedPreferencesSystemSettings.getBoolean("dark_mode", false)
    isHideAuthor = sharedPreferencesSystemSettings.getBoolean("hide_author", false)
    isHideGithub = sharedPreferencesSystemSettings.getBoolean("hide_github", false)
    updateUI(isLogin, username)
    updateAboutVisibility(isHideAuthor)
    updateGithubVisibility(isHideGithub)
    setLogOut()
    setPersonAvatarLayout()
    setPersonSettingLayout()
  }

  private fun setPersonSettingLayout() {
    personSettingLayout.setOnClickListener {
      val intent = Intent(requireContext(), SystemSettingsActivity::class.java)
      startActivity(intent)
    }
  }

  private fun setPersonAvatarLayout() {
    if (isLogin) {
      personAvatarLayout.setOnClickListener {
        requireActivity().runOnUiThread {
          Toast.makeText(requireContext(), "您已经登录啦！", Toast.LENGTH_SHORT).show()
        }
      }
    } else {
      personAvatarLayout.setOnClickListener {
        val intent = Intent(requireContext(), LogInActivity::class.java)
        startActivity(intent)
      }
    }
  }

  private fun setLogOut() {
    logOutBtn.setOnClickListener {
      val sharedPreferences =
        requireActivity().getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
      val editor = sharedPreferences.edit()
      editor.putBoolean("isLogin", false)
      editor.putString("username", "")
      editor.apply()
      updateUI(false, "")
      requireActivity().runOnUiThread {
        Toast.makeText(requireContext(), "您已退出登录！", Toast.LENGTH_SHORT).show()
      }
      personAvatarLayout.setOnClickListener {
        val intent = Intent(requireContext(), LogInActivity::class.java)
        startActivity(intent)
      }
    }
  }

  private fun updateAboutVisibility(isHideAuthor: Boolean) {
    personAboutLayout.visibility = if (isHideAuthor) View.GONE else View.VISIBLE
  }

  private fun updateGithubVisibility(isHideGithub: Boolean) {
    personGithubLayout.visibility = if (isHideGithub) View.GONE else View.VISIBLE
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

  override fun onResume() {
    super.onResume()
    setPersonNightImageView()
    updateAboutVisibility(isHideAuthor)
    updateGithubVisibility(isHideGithub)
  }

  private fun setPersonNightImageView() {
    personNightImageView.setOnClickListener {
      if (sharedPreferencesSystemSettings.getBoolean("follow_dark_mode", false)) {
        Toast.makeText(
          requireContext(),
          "当前为跟随系统暗色模式，无法修改主题哦！",
          Toast.LENGTH_SHORT
        )
          .show()
      } else {
        val editor = sharedPreferencesSystemSettings.edit()
        editor.putBoolean("return_person_fragment", true)
        editor.apply()
        isDarkMode = !isDarkMode
        saveDarkModeState(isDarkMode)
        updateAppTheme(isDarkMode)
      }
    }
    val imgRes =
      if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) R.drawable.moonsec
      else R.drawable.night
    personNightImageView.setImageResource(imgRes)
    isHideAuthor = sharedPreferencesSystemSettings.getBoolean("hide_author", false)
    isHideGithub = sharedPreferencesSystemSettings.getBoolean("hide_github", false)
  }
}