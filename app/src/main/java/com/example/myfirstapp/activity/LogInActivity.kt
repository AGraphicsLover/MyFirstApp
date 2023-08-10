package com.example.myfirstapp.activity

import com.example.myfirstapp.model.ApiResponse
import com.example.myfirstapp.api.ApiServiceFactory
import com.example.myfirstapp.model.UserBean
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myfirstapp.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogInActivity : AppCompatActivity() {

  private lateinit var loginButton: Button
  private lateinit var usernameEditText: EditText
  private lateinit var passwordEditText: EditText
  private lateinit var closeButton: ImageButton

  private var progressDialog: ProgressDialog? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    closeButton = findViewById(R.id.closeButton)
    closeButton.setOnClickListener {
      finish()
    }

    loginButton = findViewById(R.id.activity_login_btn)
    usernameEditText = findViewById(R.id.activity_login_user_et)
    passwordEditText = findViewById(R.id.activity_login_pwd_et)

    loginButton.setOnClickListener {
      showProgressDialog()
      val username = usernameEditText.text.toString()
      val password = passwordEditText.text.toString()

      ApiServiceFactory.apiService.login(username, password).enqueue(object :
        Callback<ApiResponse<UserBean>> {
        override fun onFailure(call: Call<ApiResponse<UserBean>>, t: Throwable) {
          hideProgressDialog()
          Toast.makeText(this@LogInActivity, "网络请求失败，请检查网络连接", Toast.LENGTH_SHORT).show()
        }

        override fun onResponse(
          call: Call<ApiResponse<UserBean>>,
          response: Response<ApiResponse<UserBean>>
        ) {
          hideProgressDialog()
          val responseData = response.body()
          if (responseData != null) {
            if (responseData.errorCode == 0) {
              val userName = responseData.data?.username
              val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
              val editor = sharedPreferences.edit()
              editor.putBoolean("isLogin", true)
              editor.putString("username", userName)
              editor.apply()

              val handler = Handler(mainLooper)
              handler.post {
                Toast.makeText(this@LogInActivity, "欢迎，${userName}！", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LogInActivity, PersonActivity::class.java)
                intent.putExtra("username", userName)
                intent.putExtra("isLogin", true)
                startActivity(intent)
                finish()
              }
            } else {
              val errorMsg = responseData.errorMsg
              runOnUiThread {
                Toast.makeText(this@LogInActivity, errorMsg, Toast.LENGTH_SHORT).show()
              }
            }
          }
        }
      })

//      val client = OkHttpClient()
//      val url = "https://www.wanandroid.com/user/login"
//      val requestBody = FormBody.Builder()
//        .add("username", username)
//        .add("password", password)
//        .build()
//      val request = Request.Builder()
//        .url(url)
//        .post(requestBody)
//        .build()
//      client.newCall(request).enqueue(object : Callback {
//        override fun onFailure(call: Call, e: IOException) {
//          hideProgressDialog()
//          Toast.makeText(
//            this@LogIn,
//            "网络请求失败，请检查网络连接",
//            Toast.LENGTH_SHORT
//          ).show()
//        }

      //        {
//          "data":{
//          "admin":false, "chapterTops":[], "coinCount":10, "collectIds":[], "email":"", "icon":"",
//          "id":151641, "nickname":"AGraphicsLover", "password":"", "publicName":"AGraphicsLover",
//          "token":"", "type":0, "username":"AGraphicsLover"
//        }, "errorCode":0, "errorMsg":""
//        }
//
//        { "data":null, "errorCode":-1, "errorMsg":"账号密码不匹配！" }   以上为API接口返回的数据
//        override fun onResponse(call: Call, response: Response) {
//          hideProgressDialog()
//          try {
//            val responseData = response.body?.string()
//            if (responseData != null) {
//              val jsonObject = JSONObject(responseData)
//              val errorCode = jsonObject.optInt("errorCode")
//
//              if (errorCode == 0) {
//                val dataObject = jsonObject.optJSONObject("data")
//                val userName = dataObject?.optString("username")
//                val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
//                val editor = sharedPreferences.edit()
//                editor.putBoolean("isLogin", true)
//                editor.putString("username", userName)
//                editor.apply()
//
//                val handler = Handler(mainLooper)
//                handler.post {
//                  Toast.makeText(this@LogIn, "欢迎，${userName}！", Toast.LENGTH_SHORT).show()
//                  val intent = Intent(this@LogIn, Person::class.java)
//                  intent.putExtra("username", userName)
//                  intent.putExtra("isLogin", true)
//                  startActivity(intent)
//                  finish()
//                }
//              } else {
//                val errorMsg = jsonObject.optString("errorMsg")
//                runOnUiThread {
//                  Toast.makeText(this@LogIn, errorMsg, Toast.LENGTH_SHORT).show()
//                }
//              }
//            }
//          } catch (e: JSONException) {
//            e.printStackTrace()
//          }
//        }
//      })
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