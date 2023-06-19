package dev.tontech.news_app_yt.model

import com.google.gson.annotations.SerializedName

data class News(
    val status: String,
    @SerializedName("num_results")
    val numResults: Int,
    val results: List<NewsResults>
)
