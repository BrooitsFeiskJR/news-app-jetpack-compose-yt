package dev.tontech.news_app_yt.model

import com.google.gson.annotations.SerializedName

data class ImageNews(
    val type: String,
    @SerializedName("media-metadata")
    val metadata: List<ImageUrl>
)
