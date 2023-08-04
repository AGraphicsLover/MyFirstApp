package com.example.myfirstapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class LogIn : AppCompatActivity() {

  private lateinit var loginButton: Button
  private lateinit var usernameEditText: EditText
  private lateinit var passwordEditText: EditText

  private var progressDialog: ProgressDialog? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    val closeButton: ImageButton = findViewById(R.id.closeButton)
    closeButton.setOnClickListener {
      // 处理关闭按钮点击事件，例如返回上一个界面
      onBackPressed()
    }

    loginButton = findViewById(R.id.activity_login_btn)
    usernameEditText = findViewById(R.id.activity_login_user_et)
    passwordEditText = findViewById(R.id.activity_login_pwd_et)

    loginButton.setOnClickListener {
      showProgressDialog()
      val username = usernameEditText.text.toString()
      val password = passwordEditText.text.toString()

      val client = OkHttpClient()

      val url = "https://www.wanandroid.com/user/login"
      val requestBody = FormBody.Builder()
        .add("username", username)
        .add("password", password)
        .build()

      val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

      client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
          hideProgressDialog()
          Toast.makeText(
            this@LogIn,
            "网络请求失败，请检查网络连接",
            Toast.LENGTH_SHORT
          ).show()
        }
//        {"data":{"admin":false,"chapterTops":[],"coinCount":10,"collectIds":[],"email":"","icon":"",
//          "id":151641,"nickname":"AGraphicsLover","password":"","publicName":"AGraphicsLover",
//          "token":"","type":0,"username":"AGraphicsLover"},"errorCode":0,"errorMsg":""}

        //        {"data":null,"errorCode":-1,"errorMsg":"账号密码不匹配！"}
        override fun onResponse(call: Call, response: Response) {
          hideProgressDialog()
          try {
            val responseData = response.body?.string()
            if (responseData != null) {
              val jsonObject = JSONObject(responseData)
              val errorCode = jsonObject.optInt("errorCode")

              if (errorCode == 0) {
                val dataObject = jsonObject.optJSONObject("data")
                val userName = dataObject?.optString("username")
                val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("isLogin", true)
                editor.putString("username", userName)
                editor.apply()

                val handler = Handler(mainLooper)
                handler.post {
                  Toast.makeText(this@LogIn, "欢迎，${userName}！", Toast.LENGTH_SHORT).show()
                  val intent = Intent(this@LogIn, Person::class.java)
                  intent.putExtra("username", userName)
                  intent.putExtra("isLogin", true)
                  startActivity(intent)
                  finish()
                }
              } else {
                val errorMsg = jsonObject.optString("errorMsg")
                runOnUiThread {
                  Toast.makeText(this@LogIn, errorMsg, Toast.LENGTH_SHORT).show()
                }
              }
            }
          } catch (e: JSONException) {
            e.printStackTrace()
          }
        }
      })
    }
  }

  private fun showProgressDialog() {
    if (progressDialog == null) {
      progressDialog = ProgressDialog(this)
      progressDialog?.setMessage("登录中...")
      progressDialog?.setCancelable(false)
    }
    progressDialog?.show()
  }

  private fun hideProgressDialog() {
    progressDialog?.dismiss()
  }
}