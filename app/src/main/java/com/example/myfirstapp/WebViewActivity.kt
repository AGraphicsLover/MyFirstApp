package com.example.myfirstapp

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class WebViewActivity : AppCompatActivity() {

  private lateinit var backButton: ImageButton
  private var dx: Float = 0f
  private var dy: Float = 0f
  private var isMoving = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_web_view)

    val webView: WebView = findViewById(R.id.webView)
    backButton = findViewById(R.id.backButton)

    val url = intent.getStringExtra("url")
    webView.settings.javaScriptEnabled = true
    if (url != null) {
      webView.loadUrl(url)
    } else {
      Toast.makeText(this@WebViewActivity, "空网页！", Toast.LENGTH_SHORT).show()
    }

    backButton.setOnClickListener {
      finish()
    }

    backButton.setOnTouchListener { v, event ->
      when (event.action) {
        MotionEvent.ACTION_DOWN -> {
          dx = v.x - event.rawX
          dy = v.y - event.rawY
          isMoving = false
        }

        MotionEvent.ACTION_MOVE -> {
          isMoving = true
          val newX = event.rawX + dx
          val newY = event.rawY + dy

          // 边界检测，避免按钮移动出屏幕
          val parentWidth = (v.parent as View).width
          val parentHeight = (v.parent as View).height
          val buttonWidth = v.width
          val buttonHeight = v.height

          val maxX = parentWidth - buttonWidth
          val maxY = parentHeight - buttonHeight

          val finalX = when {
            newX < 0 -> 0f
            newX > maxX -> maxX.toFloat()
            else -> newX
          }

          val finalY = when {
            newY < 0 -> 0f
            newY > maxY -> maxY.toFloat()
            else -> newY
          }

          v.animate()
            .x(finalX)
            .y(finalY)
            .setDuration(0)
            .start()
        }

        MotionEvent.ACTION_UP -> {
          if (!isMoving) {
            finish()
          }
        }
      }
      true
    }

  }
}