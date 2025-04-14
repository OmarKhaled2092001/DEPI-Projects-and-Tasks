package com.example.whatnow

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.whatnow.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth

        val category = intent.getStringExtra("category") ?: "general"

        loadNews(category)

        binding.swipeRefresh.setOnRefreshListener {
            loadNews(category)
        }

    }

    private fun loadNews(category: String) {
        val country = getSharedPreferences("sharedPrefs", MODE_PRIVATE).getString("country", "us")

        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC)

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val retrofit = Retrofit
            .Builder()
            .baseUrl("https://newsapi.org")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val newsCallable = retrofit.create(NewsCallable::class.java)
        newsCallable.getNews(category = category, country = country ?: "us")
            .enqueue(object : Callback<News> {
                override fun onResponse(p0: Call<News>, p1: Response<News>) {
                    val news = p1.body()
                    val articles = news?.articles!!
                    //Log.d("trace", "articles: $articles")
                    articles.removeAll {
                        it.title == "[Removed]"
                    }
                    showNews(articles)
                    binding.progress.isVisible = false
                    binding.swipeRefresh.isRefreshing = false
                }

                override fun onFailure(p0: Call<News>, p1: Throwable) {
                    Log.d("trace", "error: ${p1.message}")
                    binding.progress.isVisible = false
                    binding.swipeRefresh.isRefreshing = false
                }
            })
    }

    private fun showNews(articles: ArrayList<Article>) {
        val adapter = NewsAdapter(this, articles)
        binding.newsList.adapter = adapter
    }

}