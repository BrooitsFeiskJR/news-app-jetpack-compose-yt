package dev.tontech.news_app_yt.repositories

import dev.tontech.news_app_yt.model.News
import dev.tontech.news_app_yt.network.NewsApi

class NewsRepository(private val api: NewsApi): NewsRepositoryInt {

    override suspend fun fetchAllNews(): News {
        try {
            return api.getApiNews().getMostPopularNews().body()!!
        } catch (e: Exception) {
            throw e
        }
    }
}