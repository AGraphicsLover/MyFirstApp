package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class StartPage : AppCompatActivity() {
  private val delayTime: Long = 1000

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_start_page)

    Handler().postDelayed({
      val intent = Intent(this@StartPage, MainActivity::class.java)
      startActivity(intent)
      finish()
    }, delayTime)
  }
}