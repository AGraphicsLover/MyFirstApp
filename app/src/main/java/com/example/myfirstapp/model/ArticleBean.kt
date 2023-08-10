package com.example.myfirstapp.model

data class ArticleBean(
  val title: String,
  val author: String,
  val shareUser: String,
  val niceDate: String,
  val chapterName: String,
  val superChapterName: String,
  val link: String,
  var isLiked: Boolean
)
