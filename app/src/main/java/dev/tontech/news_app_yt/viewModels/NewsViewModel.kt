package dev.tontech.news_app_yt.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import dev.tontech.news_app_yt.model.News
import dev.tontech.news_app_yt.network.NewsApi
import dev.tontech.news_app_yt.repositories.NewsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

sealed class NewsUIState {
    object Loading: NewsUIState()
    data class Success(val news: News): NewsUIState()
    data class Error(val message: String): NewsUIState()
}

class NewsViewModel(private val repository: NewsRepository): ViewModel() {
    private val _newsUiState = MutableLiveData<NewsUIState>()
    val newsUIState: LiveData<NewsUIState>
        get() = _newsUiState


    fun fetchNews() {
        viewModelScope.launch {
            _newsUiState.value = NewsUIState.Loading
            try {
                val results = viewModelScope.async { repository.fetchAllNews() }.await()
                _newsUiState.value = NewsUIState.Success(results)
            } catch (e: Exception) {
                _newsUiState.value = NewsUIState.Error(e.message ?: "Unknown Error occurred")
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return NewsViewModel(NewsRepository(NewsApi())) as T
            }
        }
    }
}