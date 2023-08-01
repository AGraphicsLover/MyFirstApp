package com.example.myfirstapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import okhttp3.internal.toHexString

class StartPage : AppCompatActivity() {
  private val delayTime: Long = 1000

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_start_page)

    val layout = findViewById<RelativeLayout>(R.id.startPage)
    layout.setBackgroundColor(getStatusBarColor())

    Handler().postDelayed({
      val intent = Intent(this@StartPage, MainActivity::class.java)
      startActivity(intent)
      finish()
    }, delayTime)
  }

  private fun getStatusBarColor(): Int {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      // 在 Android 5.0 及以上版本可以使用 window.statusBarColor 获取状态栏颜色
      val window = window
      Log.d("颜色：", window.statusBarColor.toHexString())
      return window.statusBarColor
    }
    // 如果是 Android 5.0 以下版本，则默认返回一个颜色值
    return ContextCompat.getColor(this, R.color.blue)
  }
}