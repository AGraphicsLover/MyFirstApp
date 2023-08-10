package com.example.myfirstapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myfirstapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class SystemActivity : AppCompatActivity() {
  private lateinit var bottomNavigationView: BottomNavigationView
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_system)

    bottomNavigationView = findViewById(R.id.bottomNavigationView)
    setupBottomNavigationView()
    bottomNavigationView.selectedItemId = R.id.action_system
    bottomNavigationView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
  }

  private fun setupBottomNavigationView() {
    bottomNavigationView.setOnItemSelectedListener { item ->
      when (item.itemId) {
        R.id.action_home -> {
          val intent = Intent(this, MainActivity::class.java)
          intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
          startActivity(intent)
          true
        }

        R.id.action_question -> {
          val intent = Intent(this, QuestionAnswerActvity::class.java)
          intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
          startActivity(intent)
          true
        }

        R.id.action_system -> {
          true
        }

        R.id.action_profile -> {
          val intent = Intent(this, PersonActivity::class.java)
          intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
          startActivity(intent)
          true
        }

        else -> false
      }
    }
  }
}