package com.example.myfirstapp.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.myfirstapp.R
import com.example.myfirstapp.adapter.QuestionAdapter
import com.example.myfirstapp.model.QuestionBean
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class QuestionFragment : Fragment() {

  private lateinit var recyclerView: RecyclerView
  private lateinit var questionAdapter: QuestionAdapter
  private lateinit var swipeRefreshLayout: SwipeRefreshLayout
  private var currentPage = 0
  private val pageSize = 3
  private var progressDialog: ProgressDialog? = null

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.fragment_question, container, false)
    initView(view)
    return view
  }

  private fun initView(view: View) {
    recyclerView = view.findViewById(R.id.recyclerView2)
    recyclerView.layoutManager = LinearLayoutManager(requireContext())
    questionAdapter = QuestionAdapter()
    recyclerView.adapter = questionAdapter
    swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout2)
    swipeRefreshLayout.setOnRefreshListener {
      currentPage = 0
      if (isAdded) {
        requireActivity().runOnUiThread {
          questionAdapter.setDataClear()
        }
      }
      fetchQuestionData()
    }
    questionAdapter.setOnLikeButtonClickListener(object :
      QuestionAdapter.OnLikeButtonClickListener {
      override fun onLikeButtonClick(questionBean: QuestionBean) {
        questionBean.isLiked = !(questionBean.isLiked)
        questionAdapter.updateLikeOfArticleItem(questionBean)
      }
    })
    questionAdapter.setOnItemClickListener(object : QuestionAdapter.OnItemClickListener {
      override fun onItemClick(questionBean: QuestionBean) {
        val intent = Intent(requireContext(), WebViewActivity::class.java)
        intent.putExtra("url", questionBean.link)
        startActivity(intent)
      }
    })
    setupRecyclerViewScrollListener()
    fetchQuestionData()
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
      .url("https://wanandroid.com/wenda/list/$currentPage/json")
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
            val questionList = mutableListOf<QuestionBean>()

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
                QuestionBean(
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
            if (isAdded) {
              requireActivity().runOnUiThread {
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
            val questionList = mutableListOf<QuestionBean>()
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
                QuestionBean(
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
            if (isAdded) {
              requireActivity().runOnUiThread {
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