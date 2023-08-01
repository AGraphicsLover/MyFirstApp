package com.example.myfirstapp

import ArticleItem
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class QuestionAnswer : AppCompatActivity() {

  private lateinit var recyclerView: RecyclerView
  private lateinit var articleAdapter: ArticleAdapter
  private lateinit var swipeRefreshLayout: SwipeRefreshLayout

  private var currentPage = 0
  private val pageSize = 3

  private var progressDialog: ProgressDialog? = null

  private lateinit var toolbar: Toolbar

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_question_answer)

    toolbar = findViewById(R.id.toolbar2)
    toolbar.setBackgroundColor(getStatusBarColor())

    recyclerView = findViewById(R.id.recyclerView2)
    recyclerView.layoutManager = LinearLayoutManager(this)
    articleAdapter = ArticleAdapter()
    recyclerView.adapter = articleAdapter

    val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
    bottomNavigationView.setOnItemSelectedListener { item ->
      when (item.itemId) {
        R.id.action_home -> {
          val intent = Intent(this, MainActivity::class.java)
          intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
          startActivity(intent)
          true
        }

        R.id.action_question -> {
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

    bottomNavigationView.selectedItemId = R.id.action_question

    swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout2)
    swipeRefreshLayout.setOnRefreshListener {
      currentPage = 0
      runOnUiThread {
        articleAdapter.setDataClear()
      }
      fetchArticleData()
    }

    articleAdapter.setOnLikeButtonClickListener(object : ArticleAdapter.OnLikeButtonClickListener {
      override fun onLikeButtonClick(articleItem: ArticleItem) {
        articleItem?.isLiked = !(articleItem.isLiked)
        articleAdapter.updateLikeOfArticleItem(articleItem)
      }
    })

    articleAdapter.setOnItemClickListener(object : ArticleAdapter.OnItemClickListener {
      override fun onItemClick(articleItem: ArticleItem) {
        if (articleItem != null) {
          val intent = Intent(this@QuestionAnswer, WebViewActivity::class.java)
          intent.putExtra("url", articleItem.link)
          startActivity(intent)
        }
      }
    })

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

  private fun getStatusBarColor(): Int {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      // 在 Android 5.0 及以上版本可以使用 window.statusBarColor 获取状态栏颜色
      val window = window
      return window.statusBarColor
    }
    // 如果是 Android 5.0 以下版本，则默认返回一个颜色值
    return ContextCompat.getColor(this, R.color.blue)
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
            this@QuestionAnswer,
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
              val superChapterName = articleObject.optString("superChapterName")
              val link = articleObject.optString("link")
              val title = Html.fromHtml(titleOrigin, Html.FROM_HTML_MODE_LEGACY).toString()
              articleList.add(
                ArticleItem(
                  title,
                  author,
                  shareUser,
                  niceDate,
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
            this@QuestionAnswer,
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
              val superChapterName = articleObject.optString("superChapterName")
              val link = articleObject.optString("link")
              val title = Html.fromHtml(titleOrigin, Html.FROM_HTML_MODE_LEGACY).toString()
              articleList.add(
                ArticleItem(
                  title,
                  author,
                  shareUser,
                  niceDate,
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