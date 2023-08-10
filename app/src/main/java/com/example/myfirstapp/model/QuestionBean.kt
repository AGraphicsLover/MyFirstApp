package com.example.myfirstapp.model

data class QuestionBean(
  val title: String,
  val author: String,
  val niceDate: String,
  val desc: String,
  val chapterName: String,
  val superChapterName: String,
  var isLiked: Boolean,
  val link: String
)