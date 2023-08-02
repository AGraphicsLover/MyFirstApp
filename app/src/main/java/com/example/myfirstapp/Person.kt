package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class Person : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_person)

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

}