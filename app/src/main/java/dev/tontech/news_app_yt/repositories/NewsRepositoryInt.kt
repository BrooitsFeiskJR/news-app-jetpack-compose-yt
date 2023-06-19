package dev.tontech.news_app_yt.repositories

import dev.tontech.news_app_yt.model.News

interface NewsRepositoryInt {
    suspend fun fetchAllNews() : News
}