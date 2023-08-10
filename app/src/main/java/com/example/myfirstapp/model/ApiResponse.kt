package com.example.myfirstapp.model

data class ApiResponse<T>(
  val errorCode: Int,
  val errorMsg: String,
  val data: T?
)