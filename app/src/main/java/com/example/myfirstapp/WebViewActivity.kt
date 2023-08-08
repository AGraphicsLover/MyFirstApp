package com.example.myfirstapp

import android.os.Bundle
import android.webkit.WebView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class WebViewActivity : AppCompatActivity() {

  private lateinit var backImageView: ImageView
  private lateinit var webView: WebView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_web_view)

    webView = findViewById(R.id.webView)
    backImageView = findViewById(R.id.web_view_back_iv)
    loadUrl()
    backImageView.setOnClickListener {
      finish()
    }
  }

  private fun loadUrl() {
    val url = intent.getStringExtra("url")
    webView.settings.javaScriptEnabled = true
    if (url != null) {
      webView.loadUrl(url)
    } else {
      Toast.makeText(this@WebViewActivity, "空网页！", Toast.LENGTH_SHORT).show()
    }
  }
}