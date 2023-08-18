package com.example.myfirstapp.activity

import android.app.ProgressDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.myfirstapp.R
import com.example.myfirstapp.adapter.ArticleAdapter
import com.example.myfirstapp.adapter.ViewPagerAdapter
import com.example.myfirstapp.model.ArticleBean
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class HomePageFragment : Fragment() {

  private lateinit var mRecyclerView: RecyclerView
  private lateinit var articleAdapter: ArticleAdapter
  private lateinit var swipeRefreshLayout: SwipeRefreshLayout
  private lateinit var viewPagerAdapter: ViewPagerAdapter
  private lateinit var sharedPreferencesSystemSettings: SharedPreferences
  private val pageSize = 3
  private var currentPage = 0
  private var progressDialog: ProgressDialog? = null
  private var isDarkMode: Boolean = false
  private var isFollowDarkMode: Boolean = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    initConfig()
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.fragment_home_page, container, false)
    initView(view)
    setupRecyclerViewScrollListener()
    fetchArticleData()
    return view
  }

  private fun initView(view: View) {
    mRecyclerView = view.findViewById(R.id.recyclerView)
    swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
    swipeRefreshLayout.setOnRefreshListener {
      currentPage = 0
      fetchArticleData()
    }
    updateAppTheme(isFollowDarkMode, isDarkMode)
    mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    articleAdapter = ArticleAdapter()
    viewPagerAdapter = ViewPagerAdapter(requireContext())
    mRecyclerView.adapter = ConcatAdapter(viewPagerAdapter, articleAdapter)

  }

  private fun initConfig() {
    sharedPreferencesSystemSettings = requireActivity().getSharedPreferences(
      "system_settings",
      AppCompatActivity.MODE_PRIVATE
    )
    isFollowDarkMode = sharedPreferencesSystemSettings.getBoolean("follow_dark_mode", false)
    isDarkMode = sharedPreferencesSystemSettings.getBoolean("dark_mode", false)
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
      progressDialog = ProgressDialog(requireContext())
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
        if (isAdded) {
          requireActivity().runOnUiThread {
            Toast.makeText(
              requireContext(),
              "网络请求失败，请检查网络连接",
              Toast.LENGTH_SHORT
            ).show()
            swipeRefreshLayout.isRefreshing = false
          }
        }
      }

      override fun onResponse(call: Call, response: Response) {
        hideProgressDialog()
        val responseData = response.body?.string()
        if (responseData != null) {
          val jsonObject = JSONObject(responseData)
          val dataArray = jsonObject.optJSONObject("data")?.optJSONArray("datas")
          if (dataArray != null) {
            val articleList = mutableListOf<ArticleBean>()
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
                ArticleBean(
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
            if (isAdded) {
              requireActivity().runOnUiThread {
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
        if (isAdded) {
          requireActivity().runOnUiThread {
            Toast.makeText(
              requireContext(),
              "网络请求失败，请检查网络连接",
              Toast.LENGTH_SHORT
            ).show()
            swipeRefreshLayout.isRefreshing = false
          }
        }
      }

      override fun onResponse(call: Call, response: Response) {
        val responseData = response.body?.string()
        if (responseData != null) {
          val jsonObject = JSONObject(responseData)
          val dataArray = jsonObject.optJSONObject("data")?.optJSONArray("datas")
          if (dataArray != null) {
            val articleList = mutableListOf<ArticleBean>()
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
                ArticleBean(
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
            if (isAdded) {
              requireActivity().runOnUiThread {
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
      }
    })
  }

  private fun setupRecyclerViewScrollListener() {
    mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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