package com.example.myfirstapp.adapter

import com.example.myfirstapp.model.QuestionBean
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.R

class QuestionAdapter : RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>() {

  private val data = mutableListOf<QuestionBean>()
  private var onItemClickListener: OnItemClickListener? = null
  private var likeButtonClickListener: OnLikeButtonClickListener? = null

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): QuestionViewHolder {
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

  fun setData(questionList: List<QuestionBean>) {
    data.clear()
    data.addAll(questionList)
    notifyDataSetChanged()
  }

  fun addData(questionList: List<QuestionBean>) {
    data.addAll(questionList)
    notifyDataSetChanged()
  }

  fun updateLikeOfArticleItem(questionBean: QuestionBean) {
    val position = data.indexOf(questionBean)
    if (position != -1) {
      data[position] = questionBean
      notifyItemChanged(position)
    }
  }


  fun setOnItemClickListener(listener: OnItemClickListener) {
    onItemClickListener = listener
  }

  fun setOnLikeButtonClickListener(listener: OnLikeButtonClickListener) {
    likeButtonClickListener = listener
  }


  inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val titleTextView: TextView = itemView.findViewById(R.id.item_question_title_tv)
    private val authorTextView: TextView = itemView.findViewById(R.id.item_question_author_tv)
    private val dateTextView: TextView = itemView.findViewById(R.id.item_question_date_tv)
    private val descTextView: TextView = itemView.findViewById(R.id.item_question_desc_tv)
    private val chapterTextView: TextView = itemView.findViewById(R.id.item_question_chapter_tv)
    private val superChapterTextView: TextView = itemView.findViewById(
      R.id.item_question_super_chapter_tv
    )
    private val likeButton: ImageButton = itemView.findViewById(R.id.like_btn)

    fun bind(questionBean: QuestionBean) {
      titleTextView.text = questionBean.title
      authorTextView.text = questionBean.author
      dateTextView.text = questionBean.niceDate
      descTextView.text = questionBean.desc
      chapterTextView.text = " · ${questionBean.chapterName}"
      superChapterTextView.text = questionBean.superChapterName
      itemView.setOnClickListener {
        onItemClickListener?.onItemClick(questionBean)
      }
      likeButton.setOnClickListener {
        likeButtonClickListener?.onLikeButtonClick(questionBean)
      }

      val imageRes = if (questionBean.isLiked) R.drawable.like else R.drawable.unlike
      likeButton.setImageResource(imageRes)

    }
  }

  interface OnItemClickListener {
    fun onItemClick(questionBean: QuestionBean)
  }

  interface OnLikeButtonClickListener {
    fun onLikeButtonClick(questionBean: QuestionBean)
  }

}