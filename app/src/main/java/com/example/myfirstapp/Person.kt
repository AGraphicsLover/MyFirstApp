package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class Person : AppCompatActivity() {

  private lateinit var logInTv: TextView
  private lateinit var logOutBtn: Button
  private lateinit var personAvatarLayout: LinearLayout

  private var username: String = ""
  private var isLogin: Boolean = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_person)

    logInTv = findViewById(R.id.person_login_tv)
    logOutBtn = findViewById(R.id.person_logout_btn)
    personAvatarLayout = findViewById(R.id.person_avatar_layout)

    username = intent.getStringExtra("username").toString()
    isLogin = intent.getBooleanExtra("isLogin", false)

    logOutBtn.setOnClickListener {
      isLogin = false
      logInTv.text = "去登录"
      logOutBtn.visibility = View.GONE
      personAvatarLayout.setOnClickListener {
        val intent = Intent(this@Person, LogIn::class.java)
        startActivity(intent)
      }
    }

    if (isLogin) {
      logInTv.text = username
      logOutBtn.visibility = View.VISIBLE
      personAvatarLayout.setOnClickListener {
        runOnUiThread {
          Toast.makeText(this@Person, "您已经登录啦！", Toast.LENGTH_SHORT).show()
        }
      }
    } else {
      logInTv.text = "去登录"
      logOutBtn.visibility = View.GONE
      personAvatarLayout.setOnClickListener {
        val intent = Intent(this@Person, LogIn::class.java)
        startActivity(intent)
      }
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

}