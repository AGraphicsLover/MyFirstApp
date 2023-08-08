package com.example.myfirstapp

import ArticleItem
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
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
  private lateinit var viewPagerAdapter: ViewPagerAdapter
  private lateinit var bottomNavigationView: BottomNavigationView
  private lateinit var sharedPreferencesSystemSettings: SharedPreferences
  private val pageSize = 3
  private var currentPage = 0
  private var progressDialog: ProgressDialog? = null
  private var isDarkMode: Boolean = false
  private var isFollowDarkMode: Boolean = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    sharedPreferencesSystemSettings = getSharedPreferences("system_settings", MODE_PRIVATE)
    isFollowDarkMode = sharedPreferencesSystemSettings.getBoolean("follow_dark_mode", false)
    isDarkMode = sharedPreferencesSystemSettings.getBoolean("dark_mode", false)
    updateAppTheme(isFollowDarkMode, isDarkMode)
    bottomNavigationView = findViewById(R.id.bottomNavigationView)
    setupbottomNavigationView()
    bottomNavigationView.selectedItemId = R.id.action_home
    bottomNavigationView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
    recyclerView = findViewById(R.id.recyclerView)
    recyclerView.layoutManager = LinearLayoutManager(this)
    articleAdapter = ArticleAdapter()
    viewPagerAdapter = ViewPagerAdapter(this)
    recyclerView.adapter = ConcatAdapter(viewPagerAdapter, articleAdapter)
    swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
    swipeRefreshLayout.setOnRefreshListener {
      currentPage = 0
      fetchArticleData()
    }

    articleAdapter.setOnLikeButtonClickListener(object : ArticleAdapter.OnLikeButtonClickListener {
      override fun onLikeButtonClick(articleItem: ArticleItem) {
        articleItem.isLiked = !(articleItem.isLiked)
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
    setupRecyclerViewScrollListener()
    fetchArticleData()
  }

  private fun setupbottomNavigationView() {
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
  }

  private fun updateAppTheme(isFollowDarkMode: Boolean, isDarkMode: Boolean) {
    if (isFollowDarkMode) {
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    } else {
      val nightMode =
        if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
      AppCompatDelegate.setDefaultNightMode(nightMode)
    }
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
        hideProgressDialog()
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
        hideProgressDialog()
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
}