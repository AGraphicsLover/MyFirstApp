package com.example.myfirstapp

import QuestionItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QuestionAdapter : RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>() {
  private val data = mutableListOf<QuestionItem>()
  private var onItemClickListener: OnItemClickListener? = null
  private var likeButtonClickListener: OnLikeButtonClickListener? = null

  interface OnItemClickListener {
    fun onItemClick(questionItem: QuestionItem)
  }

  fun setOnItemClickListener(listener: OnItemClickListener) {
    onItemClickListener = listener
  }

  interface OnLikeButtonClickListener {
    fun onLikeButtonClick(questionItem: QuestionItem)
  }

  fun setOnLikeButtonClickListener(listener: OnLikeButtonClickListener) {
    likeButtonClickListener = listener
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): QuestionAdapter.QuestionViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_question, parent, false)
    return QuestionViewHolder(view)
  }

  override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
    val article = data[position]
    holder.bind(article)
  }

  override fun getItemCount(): Int {
    return data.size
  }

  fun setDataClear() {
    data.clear()
    notifyDataSetChanged()
  }

  fun setData(questionList: List<QuestionItem>) {
    data.clear()
    data.addAll(questionList)
    notifyDataSetChanged()
  }

  fun addData(questionList: List<QuestionItem>) {
    data.addAll(questionList)
    notifyDataSetChanged()
  }

  fun updateLikeOfArticleItem(questionItem: QuestionItem) {
    val position = data.indexOf(questionItem)
    if (position != -1) {
      data[position] = questionItem
      notifyItemChanged(position)
    }
  }

  inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val titleTextView: TextView = itemView.findViewById(R.id.item_question_title_tv)
    private val authorTextView: TextView = itemView.findViewById(R.id.item_question_author_tv)
    private val dateTextView: TextView = itemView.findViewById(R.id.item_question_date_tv)
    private val descTextView: TextView = itemView.findViewById(R.id.item_question_desc_tv)
    private val chapterTextView: TextView = itemView.findViewById(R.id.item_question_chapter_tv)
    private val superChapterTextView: TextView = itemView.findViewById(
      R.id
        .item_question_super_chapter_tv
    )
    private val likeButton: ImageButton = itemView.findViewById(R.id.like_btn)

    fun bind(questionItem: QuestionItem) {
      titleTextView.text = questionItem.title
      authorTextView.text = questionItem.author
      dateTextView.text = questionItem.niceDate
      descTextView.text = questionItem.desc
      chapterTextView.text = " · ${questionItem.chapterName}"
      superChapterTextView.text = questionItem.superChapterName
      itemView.setOnClickListener {
        onItemClickListener?.onItemClick(questionItem)
      }
      likeButton.setOnClickListener {
        likeButtonClickListener?.onLikeButtonClick(questionItem)
      }

      val imageRes = if (questionItem.isLiked) R.drawable.like else R.drawable.unlike
      likeButton.setImageResource(imageRes)

    }
  }
}