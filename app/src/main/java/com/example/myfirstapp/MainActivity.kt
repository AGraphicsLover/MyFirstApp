package com.example.myfirstapp

import ArticleItem
import BannerItem
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

  private lateinit var recyclerView: RecyclerView
  private lateinit var articleAdapter: ArticleAdapter
  private lateinit var swipeRefreshLayout: SwipeRefreshLayout

  private var currentPage = 0
  private val pageSize = 3

  private lateinit var viewPager: ViewPager2
  private lateinit var bannerAdapter: BannerAdapter
  private lateinit var bannerRunnable: Runnable
  private val bannerHandler = Handler(Looper.getMainLooper())
  private val bannerDelayTime: Long = 3000
  private var isBannerAutoScrollEnabled = true

  private var progressDialog: ProgressDialog? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    recyclerView = findViewById(R.id.recyclerView)
    recyclerView.layoutManager = LinearLayoutManager(this)
    articleAdapter = ArticleAdapter()
    recyclerView.adapter = articleAdapter

    val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
    bottomNavigationView.setOnItemSelectedListener { item ->
      when (item.itemId) {
        R.id.action_home -> {
          true
        }

        R.id.action_question -> {
          val intent = Intent(this, QuestionAnswer::class.java)
          intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
          startActivity(intent)
          true
        }

        R.id.action_system -> {
          val intent = Intent(this, System::class.java)
          intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
          startActivity(intent)
          true
        }

        R.id.action_profile -> {
          val intent = Intent(this, Person::class.java)
          intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
          startActivity(intent)
          true
        }

        else -> false
      }

    }

    bottomNavigationView.selectedItemId = R.id.action_home

    swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
    swipeRefreshLayout.setOnRefreshListener {
      currentPage = 0
      runOnUiThread {
        articleAdapter.setDataClear()
      }
      fetchArticleData()
    }

    viewPager = findViewById(R.id.viewPager)
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

    articleAdapter.setOnLikeButtonClickListener(object : ArticleAdapter.OnLikeButtonClickListener {
      override fun onLikeButtonClick(articleItem: ArticleItem) {
        articleItem?.isLiked = !(articleItem.isLiked)
        articleAdapter.updateLikeOfArticleItem(articleItem)
      }
    })

    articleAdapter.setOnItemClickListener(object : ArticleAdapter.OnItemClickListener {
      override fun onItemClick(articleItem: ArticleItem) {
        val intent = Intent(this@MainActivity, WebViewActivity::class.java)
        intent.putExtra("url", articleItem.link)
        startActivity(intent)
      }
    })

    bannerAdapter.setOnBannerItemClickListener(object : BannerAdapter.OnBannerItemClickListener {
      override fun onBannerItemClick(position: Int) {
        val bannerItem = bannerAdapter.getItemAt(position)
        if (bannerItem != null) {
          val intent = Intent(this@MainActivity, WebViewActivity::class.java)
          intent.putExtra("url", bannerItem.url)
          startActivity(intent)
        }
      }
    })

    fetchBannerData()

    // 添加上滑加载的逻辑
    setupRecyclerViewScrollListener()

    // 第一次加载数据
    fetchArticleData()

  }

  private fun showProgressDialog() {
    if (progressDialog == null) {
      progressDialog = ProgressDialog(this)
      progressDialog?.setMessage("正在加载中...")
      progressDialog?.setCancelable(false)
    }
    progressDialog?.show()
  }

  private fun hideProgressDialog() {
    progressDialog?.dismiss()
  }

  private fun fetchNextPage() {
    showProgressDialog()
    val client = OkHttpClient()
    val request = Request.Builder()
      .url("https://www.wanandroid.com/article/list/$currentPage/json")
      .build()

    client.newCall(request).enqueue(object : Callback {
      override fun onFailure(call: Call, e: IOException) {
        runOnUiThread {
          Toast.makeText(
            this@MainActivity,
            "网络请求失败，请检查网络连接",
            Toast.LENGTH_SHORT
          ).show()
          swipeRefreshLayout.isRefreshing = false
        }
      }

      override fun onResponse(call: Call, response: Response) {
        val responseData = response.body?.string()
        if (responseData != null) {
          val jsonObject = JSONObject(responseData)
          val dataArray = jsonObject.optJSONObject("data")?.optJSONArray("datas")

          if (dataArray != null) {
            val articleList = mutableListOf<ArticleItem>()

            for (i in 0 until dataArray.length()) {
              val articleObject = dataArray.getJSONObject(i)
              val titleOrigin = articleObject.optString("title")
              val author = articleObject.optString("author")
              val shareUser = articleObject.optString("shareUser")
              val niceDate = articleObject.optString("niceDate")
              val chapterName = articleObject.optString("chapter")
              val superChapterName = articleObject.optString("superChapterName")
              val link = articleObject.optString("link")
              val title = Html.fromHtml(titleOrigin, Html.FROM_HTML_MODE_LEGACY).toString()
              articleList.add(
                ArticleItem(
                  title,
                  author,
                  shareUser,
                  niceDate,
                  chapterName,
                  superChapterName,
                  link,
                  isLiked = false
                )
              )
            }

            hideProgressDialog()

            runOnUiThread {
              if (currentPage == 0) {
                articleAdapter.setData(articleList)
              } else {
                articleAdapter.addData(articleList)
              }
              swipeRefreshLayout.isRefreshing = false
            }
          }
        }
      }
    })
  }

  private fun fetchArticleData() {

    val client = OkHttpClient()
    val request = Request.Builder()
      .url("https://www.wanandroid.com/article/list/$currentPage/json")
      .build()

    client.newCall(request).enqueue(object : Callback {
      override fun onFailure(call: Call, e: IOException) {
        runOnUiThread {
          Toast.makeText(
            this@MainActivity,
            "网络请求失败，请检查网络连接",
            Toast.LENGTH_SHORT
          ).show()
          swipeRefreshLayout.isRefreshing = false
        }
      }

      override fun onResponse(call: Call, response: Response) {
        val responseData = response.body?.string()
        if (responseData != null) {
          val jsonObject = JSONObject(responseData)
          val dataArray = jsonObject.optJSONObject("data")?.optJSONArray("datas")

          if (dataArray != null) {
            val articleList = mutableListOf<ArticleItem>()

            for (i in 0 until dataArray.length()) {
              val articleObject = dataArray.getJSONObject(i)
              val titleOrigin = articleObject.optString("title")
              val author = articleObject.optString("author")
              val shareUser = articleObject.optString("shareUser")
              val niceDate = articleObject.optString("niceDate")
              val chapterName = articleObject.optString("chapterName")
              val superChapterName = articleObject.optString("superChapterName")
              val link = articleObject.optString("link")
              val title = Html.fromHtml(titleOrigin, Html.FROM_HTML_MODE_LEGACY).toString()
              articleList.add(
                ArticleItem(
                  title,
                  author,
                  shareUser,
                  niceDate,
                  chapterName,
                  superChapterName,
                  link,
                  isLiked = false
                )
              )
            }

            runOnUiThread {
              if (currentPage == 0) {
                articleAdapter.setData(articleList)
              } else {
                articleAdapter.addData(articleList)
              }
              swipeRefreshLayout.isRefreshing = false
            }
          }
        }
      }
    })
  }

  private fun setupRecyclerViewScrollListener() {
    recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val totalItemCount = layoutManager.itemCount
        val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

        if (!swipeRefreshLayout.isRefreshing && totalItemCount < (lastVisibleItem + pageSize)) {
          currentPage++
          fetchNextPage()
        }
      }
    })
  }

  private fun fetchBannerData() {
    val client = OkHttpClient()
    val request = Request.Builder()
      .url("https://www.wanandroid.com/banner/json")
      .build()

    client.newCall(request).enqueue(object : Callback {
      override fun onFailure(call: Call, e: IOException) {
        runOnUiThread {
          // 显示错误信息，通过 Toast提示用户
          Toast.makeText(
            this@MainActivity,
            "网络请求失败，请检查网络连接",
            Toast.LENGTH_SHORT
          ).show()

          val defaultImageDrawable =
            ContextCompat.getDrawable(this@MainActivity, R.drawable.fail)
          bannerAdapter.setDefaultImage(defaultImageDrawable)

          val emptyBannerList = mutableListOf<BannerItem>()
          for (i in 0..2) {
            emptyBannerList.add(BannerItem("", "", "", ""))
          }
          bannerAdapter.setData(emptyBannerList)
          startAutoScroll()
        }
      }

      override fun onResponse(call: Call, response: Response) {
        val responseData = response.body?.string()
        if (responseData != null) {
          val jsonObject = JSONObject(responseData)
          val dataArray = jsonObject.optJSONArray("data")

          if (dataArray != null) {
            val bannerList = mutableListOf<BannerItem>()

            for (i in 0 until dataArray.length()) {
              val bannerObject = dataArray.getJSONObject(i)
              val title = bannerObject.optString("title")
              val desc = bannerObject.optString("desc")
              val url = bannerObject.optString("url")
              val imagePath = bannerObject.optString("imagePath")
              bannerList.add(BannerItem(title, desc, imagePath, url))
            }

            runOnUiThread {
              bannerAdapter.setData(bannerList)
              startAutoScroll()
            }
          }
        }
      }
    })
  }

  private fun startAutoScroll() {
    bannerHandler.postDelayed(bannerRunnable, bannerDelayTime)
  }

  private fun stopAutoScroll() {
    bannerHandler.removeCallbacks(bannerRunnable)
  }

}