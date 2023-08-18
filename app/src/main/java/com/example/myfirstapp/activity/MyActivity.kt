package com.example.myfirstapp.activity

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.myfirstapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class MyActivity : AppCompatActivity() {
  private var fragmentManager: FragmentManager? = null
  private var currentFragment: Fragment? = null
  private lateinit var bottomNavigationView: BottomNavigationView
  private lateinit var sharedPreferencesUserData: SharedPreferences
  private lateinit var sharedPreferencesSystemSettings: SharedPreferences

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_my)
    initView()
  }

  override fun onStart() {
    super.onStart()
    if (sharedPreferencesSystemSettings.getBoolean("return_person_fragment", false)) {
      val editor = sharedPreferencesSystemSettings.edit()
      editor.putBoolean("return_person_fragment", false)
      editor.apply()
      switchFragment(PersonFragment())
    }
  }

  private fun initView() {
    sharedPreferencesUserData = getSharedPreferences("user_data", MODE_PRIVATE)
    sharedPreferencesSystemSettings = getSharedPreferences("system_settings", MODE_PRIVATE)
    fragmentManager = supportFragmentManager
    currentFragment = HomePageFragment()
    bottomNavigationView = findViewById(R.id.activity_my_bnv)
    setupBottomNavigationView()
    fragmentManager!!.beginTransaction()
      .add(R.id.fragmentContainer, currentFragment!!).commit()
  }

  private fun switchFragment(fragment: Fragment) {
    val transaction = fragmentManager!!.beginTransaction()
    transaction.replace(R.id.fragmentContainer, fragment)
    transaction.commit()
    currentFragment = fragment
  }

  private fun setupBottomNavigationView() {
    bottomNavigationView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
    bottomNavigationView.setOnItemSelectedListener { item ->
      when (item.itemId) {
        R.id.action_home -> {
          switchFragment(HomePageFragment())
          true
        }

        R.id.action_question -> {
          switchFragment(QuestionFragment())
          true
        }

        R.id.action_system -> {
          switchFragment(SystemFragment())
          true
        }

        R.id.action_profile -> {
          switchFragment(PersonFragment())
          true
        }

        else -> false
      }
    }
  }
}