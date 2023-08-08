package com.example.myfirstapp

import QuestionItem
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

class QuestionAnswer : AppCompatActivity() {

  private lateinit var recyclerView: RecyclerView
  private lateinit var questionAdapter: QuestionAdapter
  private lateinit var swipeRefreshLayout: SwipeRefreshLayout
  private lateinit var bottomNavigationView: BottomNavigationView

  private var currentPage = 0
  private val pageSize = 3

  private var progressDialog: ProgressDialog? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_question_answer)

    recyclerView = findViewById(R.id.recyclerView2)
    recyclerView.layoutManager = LinearLayoutManager(this)
    questionAdapter = QuestionAdapter()
    recyclerView.adapter = questionAdapter

    bottomNavigationView = findViewById(R.id.bottomNavigationView)
    setupBottomNavigationView()
    bottomNavigationView.selectedItemId = R.id.action_question
    bottomNavigationView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED

    swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout2)
    swipeRefreshLayout.setOnRefreshListener {
      currentPage = 0
      runOnUiThread {
        questionAdapter.setDataClear()
      }
      fetchQuestionData()
    }

    questionAdapter.setOnLikeButtonClickListener(object :
      QuestionAdapter.OnLikeButtonClickListener {
      override fun onLikeButtonClick(questionItem: QuestionItem) {
        questionItem.isLiked = !(questionItem.isLiked)
        questionAdapter.updateLikeOfArticleItem(questionItem)
      }
    })

    questionAdapter.setOnItemClickListener(object : QuestionAdapter.OnItemClickListener {
      override fun onItemClick(questionItem: QuestionItem) {
        val intent = Intent(this@QuestionAnswer, WebViewActivity::class.java)
        intent.putExtra("url", questionItem.link)
        startActivity(intent)
      }
    })
    setupRecyclerViewScrollListener()
    fetchQuestionData()
  }

  private fun setupBottomNavigationView() {
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
      .url("https://wanandroid.com/wenda/list/$currentPage/json")
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
            val questionList = mutableListOf<QuestionItem>()

            for (i in 0 until dataArray.length()) {
              val questionObject = dataArray.getJSONObject(i)
              val titleOrigin = questionObject.optString("title")
              val author = questionObject.optString("author")
              val niceDate = questionObject.optString("niceDate")
              val descOrigin = questionObject.optString("desc")
              val chapterName = questionObject.optString("chapterName")
              val superChapterName = questionObject.optString("superChapterName")
              val link = questionObject.optString("link")
              val title = Html.fromHtml(titleOrigin, Html.FROM_HTML_MODE_LEGACY).toString()
              val desc = Html.fromHtml(descOrigin, Html.FROM_HTML_MODE_LEGACY).toString()
              questionList.add(
                QuestionItem(
                  title,
                  author,
                  niceDate,
                  desc,
                  chapterName,
                  superChapterName,
                  isLiked = false,
                  link
                )
              )
            }

            hideProgressDialog()

            runOnUiThread {
              if (currentPage == 0) {
                questionAdapter.setData(questionList)
              } else {
                questionAdapter.addData(questionList)
              }
              swipeRefreshLayout.isRefreshing = false
            }
          }
        }
      }
    })
  }

  private fun fetchQuestionData() {

    val client = OkHttpClient()
    val request = Request.Builder()
      .url("https://wanandroid.com/wenda/list/$currentPage/json")
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
            val questionList = mutableListOf<QuestionItem>()

            for (i in 0 until dataArray.length()) {
              val questionObject = dataArray.getJSONObject(i)
              val titleOrigin = questionObject.optString("title")
              val author = questionObject.optString("author")
              val niceDate = questionObject.optString("niceDate")
              val descOrigin = questionObject.optString("desc")
              val chapterName = questionObject.optString("chapterName")
              val superChapterName = questionObject.optString("superChapterName")
              val link = questionObject.optString("link")
              val title = Html.fromHtml(titleOrigin, Html.FROM_HTML_MODE_LEGACY).toString()
              val desc = Html.fromHtml(descOrigin, Html.FROM_HTML_MODE_LEGACY).toString()
              questionList.add(
                QuestionItem(
                  title,
                  author,
                  niceDate,
                  desc,
                  chapterName,
                  superChapterName,
                  isLiked = false,
                  link
                )
              )
            }

            runOnUiThread {
              if (currentPage == 0) {
                questionAdapter.setData(questionList)
              } else {
                questionAdapter.addData(questionList)
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