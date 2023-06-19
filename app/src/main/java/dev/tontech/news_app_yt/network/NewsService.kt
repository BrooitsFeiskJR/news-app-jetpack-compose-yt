package dev.tontech.news_app_yt.network

import dev.tontech.news_app_yt.model.News
import retrofit2.Response
import retrofit2.http.GET

interface NewsService {
    @GET("/svc/mostpopular/v2/emailed/1.json?api-key=${Constants.KEY}")
    suspend fun getMostPopularNews() : Response<News>
}