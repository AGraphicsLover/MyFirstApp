package com.example.myfirstapp.adapter

import android.content.Intent
import com.example.myfirstapp.model.ArticleBean
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.WebViewActivity

class ArticleAdapter : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {
  private val data = mutableListOf<ArticleBean>()

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ArticleViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
    return ArticleViewHolder(view)
  }

  override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
    val article = data[position]
    holder.bind(article)
  }

  override fun getItemCount(): Int {
    return data.size
  }

  fun setData(articleList: List<ArticleBean>) {
    data.clear()
    data.addAll(articleList)
    notifyDataSetChanged()
  }

  fun addData(articleList: List<ArticleBean>) {
    data.addAll(articleList)
    notifyDataSetChanged()
  }

  fun updateLikeOfArticleItem(articleBean: ArticleBean) {
    val position = data.indexOf(articleBean)
    if (position != -1) {
      data[position] = articleBean
      notifyItemChanged(position)
    }
  }

  inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
    private val authorTextView: TextView = itemView.findViewById(R.id.authorTextView)
    private val updateTimeTextView: TextView = itemView.findViewById(R.id.updateTimeTextView)
    private val sourceTextView: TextView = itemView.findViewById(R.id.sourceTextView)
    private val likeFrameLayout: FrameLayout = itemView.findViewById(R.id.item_article_like_layout)
    private val likeImageView: ImageView = itemView.findViewById(R.id.item_article_like_iv)
    private val chapterNameTextView: TextView = itemView.findViewById(R.id.chapterNameTextView)

    fun bind(articleBean: ArticleBean) {
      titleTextView.text = articleBean.title
      val author: String =
        if (articleBean.author == "") articleBean.shareUser else articleBean.author
      authorTextView.text = author
      updateTimeTextView.text = articleBean.niceDate
      sourceTextView.text = articleBean.superChapterName
      chapterNameTextView.text = " · ${articleBean.chapterName}"
      itemView.setOnClickListener {
        val intent = Intent(itemView.context, WebViewActivity::class.java)
        intent.putExtra("url", articleBean.link)
        itemView.context.startActivity(intent)
      }
      likeFrameLayout.setOnClickListener {
        articleBean.isLiked = !(articleBean.isLiked)
        updateLikeOfArticleItem(articleBean)
      }

      val imageRes = if (articleBean.isLiked) R.drawable.like else R.drawable.unlike
      likeImageView.setImageResource(imageRes)
    }
  }
}