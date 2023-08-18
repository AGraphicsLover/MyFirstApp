package com.example.myfirstapp.activity

import android.app.ProgressDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myfirstapp.R
import com.example.myfirstapp.api.ApiServiceFactory
import com.example.myfirstapp.model.ApiResponse
import com.example.myfirstapp.model.UserBean
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogInActivity : AppCompatActivity() {

  private lateinit var loginButton: Button
  private lateinit var usernameEditText: EditText
  private lateinit var passwordEditText: EditText
  private lateinit var closeButton: ImageButton
  private var progressDialog: ProgressDialog? = null
  private lateinit var sharedPreferencesSystemSettings: SharedPreferences

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)
    initView()
  }

  private fun initView() {
    closeButton = findViewById(R.id.closeButton)
    closeButton.setOnClickListener {
      finish()
    }

    loginButton = findViewById(R.id.activity_login_btn)
    usernameEditText = findViewById(R.id.activity_login_user_et)
    passwordEditText = findViewById(R.id.activity_login_pwd_et)
    sharedPreferencesSystemSettings = getSharedPreferences("system_settings", MODE_PRIVATE)
    loginButton.setOnClickListener {
      showProgressDialog()
      logInRequest()
    }
  }

  private fun logInRequest() {
    val username = usernameEditText.text.toString()
    val password = passwordEditText.text.toString()
    ApiServiceFactory.apiService.login(username, password).enqueue(object :
      Callback<ApiResponse<UserBean>> {
      override fun onFailure(call: Call<ApiResponse<UserBean>>, t: Throwable) {
        hideProgressDialog()
        Toast.makeText(this@LogInActivity, "网络请求失败，请检查网络连接", Toast.LENGTH_SHORT)
          .show()
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
              val editor = sharedPreferencesSystemSettings.edit()
              editor.putBoolean("return_person_fragment", true)
              editor.apply()
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