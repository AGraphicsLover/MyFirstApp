package com.example.myfirstapp

import ArticleItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ArticleAdapter : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {
  private val data = mutableListOf<ArticleItem>()
  private var onItemClickListener: OnItemClickListener? = null
  private var likeButtonClickListener: OnLikeButtonClickListener? = null

  interface OnItemClickListener {
    fun onItemClick(articleItem: ArticleItem)
  }

  fun setOnItemClickListener(listener: OnItemClickListener) {
    onItemClickListener = listener
  }

  interface OnLikeButtonClickListener {
    fun onLikeButtonClick(articleItem: ArticleItem)
  }

  fun setOnLikeButtonClickListener(listener: OnLikeButtonClickListener) {
    likeButtonClickListener = listener
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ArticleAdapter.ArticleViewHolder {
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

  fun setData(articleList: List<ArticleItem>) {
    data.clear()
    data.addAll(articleList)
    notifyDataSetChanged()
  }

  fun addData(articleList: List<ArticleItem>) {
    data.addAll(articleList)
    notifyDataSetChanged()
  }

  fun updateLikeOfArticleItem(articleItem: ArticleItem) {
    val position = data.indexOf(articleItem)
    if (position != -1) {
      data[position] = articleItem
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
    fun bind(articleItem: ArticleItem) {
      titleTextView.text = articleItem.title
      val author: String =
        if (articleItem.author == "") articleItem.shareUser else articleItem.author
      authorTextView.text = author
      updateTimeTextView.text = articleItem.niceDate
      sourceTextView.text = articleItem.superChapterName
      chapterNameTextView.text = " · ${articleItem.chapterName}"
      itemView.setOnClickListener {
        onItemClickListener?.onItemClick(articleItem)
      }
      likeFrameLayout.setOnClickListener {
        likeButtonClickListener?.onLikeButtonClick(articleItem)
      }

      val imageRes = if (articleItem.isLiked) R.drawable.like else R.drawable.unlike
      likeImageView.setImageResource(imageRes)
    }
  }
}