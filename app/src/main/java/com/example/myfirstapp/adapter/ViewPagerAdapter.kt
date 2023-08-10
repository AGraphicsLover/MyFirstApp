package com.example.myfirstapp.adapter

import com.example.myfirstapp.model.BannerBean
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.WebViewActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class ViewPagerAdapter(private val context: Context) :
  RecyclerView.Adapter<ViewPagerAdapter.HeaderViewHolder>() {

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): HeaderViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_viewpager2, parent, false)
    return HeaderViewHolder(view)
  }

  override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
    holder.bind()
    holder.fetchBannerData()
  }

  override fun getItemCount(): Int {
    return 1
  }

  inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var bannerRunnable: Runnable
    private val bannerHandler = Handler(Looper.getMainLooper())
    private val bannerDelayTime: Long = 3000
    private var isBannerAutoScrollEnabled = true
    private val viewPager: ViewPager2 = itemView.findViewById(R.id.header_viewPager)

    fun bind() {
      bannerAdapter = BannerAdapter()
      viewPager.adapter = bannerAdapter
      bannerRunnable = Runnable {
        var nextItem = viewPager.currentItem + 1
        if (nextItem >= (viewPager.adapter?.itemCount ?: 0)) {
          nextItem = 0
        }
        viewPager.currentItem = nextItem
        bannerHandler.postDelayed(bannerRunnable, bannerDelayTime)
      }
      viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
          when (state) {
            ViewPager2.SCROLL_STATE_IDLE -> {
              if (!isBannerAutoScrollEnabled) {
                isBannerAutoScrollEnabled = true
                startAutoScroll()
              }
            }

            ViewPager2.SCROLL_STATE_DRAGGING -> {
              isBannerAutoScrollEnabled = false
              stopAutoScroll()
            }

            ViewPager2.SCROLL_STATE_SETTLING -> {}
          }
        }
      })
      bannerAdapter.setOnBannerItemClickListener(object : BannerAdapter.OnBannerItemClickListener {
        override fun onBannerItemClick(position: Int) {
          val bannerItem = bannerAdapter.getItemAt(position)
          if (bannerItem != null) {
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra("url", bannerItem.url)
            context.startActivity(intent)
          }
        }
      })
    }

    fun fetchBannerData() {

      val client = OkHttpClient()
      val request = Request.Builder()
        .url("https://www.wanandroid.com/banner/json")
        .build()
      client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
          val defaultImageDrawable =
            ContextCompat.getDrawable(context, R.drawable.defaultimage)
          bannerAdapter.setDefaultImage(defaultImageDrawable)
          val emptyBannerList = mutableListOf<BannerBean>()
          for (i in 0..2) {
            emptyBannerList.add(BannerBean("", "", "", ""))
          }
          (context as Activity).runOnUiThread {
            Toast.makeText(
              context,
              "网络请求失败，请检查网络连接",
              Toast.LENGTH_SHORT
            ).show()
            bannerAdapter.setData(emptyBannerList)
          }
          startAutoScroll()
        }

        override fun onResponse(call: Call, response: Response) {
          val responseData = response.body?.string()
          if (responseData != null) {
            val jsonObject = JSONObject(responseData)
            val dataArray = jsonObject.optJSONArray("data")

            if (dataArray != null) {
              val bannerList = mutableListOf<BannerBean>()

              for (i in 0 until dataArray.length()) {
                val bannerObject = dataArray.getJSONObject(i)
                val title = bannerObject.optString("title")
                val desc = bannerObject.optString("desc")
                val url = bannerObject.optString("url")
                val imagePath = bannerObject.optString("imagePath")
                bannerList.add(BannerBean(title, desc, imagePath, url))
              }
              (context as Activity).runOnUiThread {
                bannerAdapter.setData(bannerList)
                startAutoScroll()
              }
            }
          }
        }
      })

    }

    private fun startAutoScroll() {
      bannerHandler.removeCallbacksAndMessages(null)
      bannerHandler.postDelayed(bannerRunnable, bannerDelayTime)
    }

    private fun stopAutoScroll() {
      bannerHandler.removeCallbacks(bannerRunnable)
    }
  }
}