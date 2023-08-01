package com.example.myfirstapp

import BannerItem
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class BannerAdapter : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {
  private val data = mutableListOf<BannerItem>()
  private var defaultImageDrawable: Drawable? = null
  private var onBannerItemClickListener: OnBannerItemClickListener? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_banner, parent, false)
    return BannerViewHolder(view)
  }

  override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
    if (data[position].imagePath == "") {
      // 如果没有数据或是空数据项，则使用默认图片
      holder.bindDefault(defaultImageDrawable)
    } else {
      val bannerItem = data[position]
      holder.bind(bannerItem)
    }
  }

  override fun getItemCount(): Int {
    return data.size
  }

  fun getItemAt(position: Int): BannerItem? {
    if (position in 0 until data.size) {
      return data[position]
    }
    return null
  }

  interface OnBannerItemClickListener {
    fun onBannerItemClick(position: Int)
  }

  fun setOnBannerItemClickListener(listener: OnBannerItemClickListener) {
    onBannerItemClickListener = listener
  }

  fun setDefaultImage(drawable: Drawable?) {
    defaultImageDrawable = drawable
  }

  fun setData(bannerList: List<BannerItem>) {
    data.clear()
    data.addAll(bannerList)
    notifyDataSetChanged()
  }

  inner class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val bannerImageView: ImageView = itemView.findViewById(R.id.bannerImageView)

    fun bind(bannerItem: BannerItem) {
      Glide.with(itemView.context)
        .load(bannerItem.imagePath)
        .into(bannerImageView)
      bannerImageView.setOnClickListener {
        onBannerItemClickListener?.onBannerItemClick((adapterPosition))
      }
    }

    // 添加绑定默认图片的方法
    fun bindDefault(defaultImageDrawable: Drawable?) {
      Glide.with(itemView.context)
        .load(defaultImageDrawable)
        .into(bannerImageView)
    }
  }
}