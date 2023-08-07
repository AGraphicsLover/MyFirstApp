package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class System : AppCompatActivity() {
  private lateinit var bottomNavigationView: BottomNavigationView
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_system)

    bottomNavigationView = findViewById(R.id.bottomNavigationView)
    setupBottomNavigationView()
    bottomNavigationView.selectedItemId = R.id.action_system
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
          val intent = Intent(this, QuestionAnswer::class.java)
          intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
          startActivity(intent)
          true
        }
        R.id.action_system -> {
          true
        }
        R.id.action_profile -> {
          val intent = Intent(this, Person::class.java)
          intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
          startActivity(intent)
          true
        }
        else -> false
      }
    }
  }
}