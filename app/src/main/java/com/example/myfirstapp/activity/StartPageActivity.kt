package com.example.myfirstapp.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.myfirstapp.R

class StartPageActivity : AppCompatActivity() {
  private val delayTime: Long = 1000

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_start_page)

    Handler().postDelayed({
      val intent = Intent(this@StartPageActivity, MainActivity::class.java)
      startActivity(intent)
      finish()
    }, delayTime)
  }
}