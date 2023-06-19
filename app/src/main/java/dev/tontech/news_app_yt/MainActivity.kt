package dev.tontech.news_app_yt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import dev.tontech.news_app_yt.model.News
import dev.tontech.news_app_yt.model.NewsResults
import dev.tontech.news_app_yt.ui.theme.BlackTextColor
import dev.tontech.news_app_yt.ui.theme.Blue
import dev.tontech.news_app_yt.ui.theme.Gray
import dev.tontech.news_app_yt.ui.theme.GrayTextColor
import dev.tontech.news_app_yt.ui.theme.NewsappytTheme
import dev.tontech.news_app_yt.ui.theme.White
import dev.tontech.news_app_yt.viewModels.NewsUIState
import dev.tontech.news_app_yt.viewModels.NewsViewModel

data class Suggestion(val title: String = "", var isFocused: Boolean = false)

object SuggestionsList {
    val list: List<Suggestion> = listOf(
        Suggestion("All", true),
        Suggestion("Politic"),
        Suggestion("Sport"),
        Suggestion("Education"),
        Suggestion("Economy"),
        Suggestion("Technology")
    )
}

class MainActivity : ComponentActivity() {
    private val vm: NewsViewModel by viewModels { NewsViewModel.Factory }
    private val state = mutableStateOf<NewsUIState>(NewsUIState.Loading)

    override fun onCreate(savedInstanceState: Bundle?) {
        vm.fetchNews()

        vm.newsUIState.observe(this) {
            state.value = it
        }

        super.onCreate(savedInstanceState)
        setContent {
            NewsappytTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = White
                ) {
                    when(val currentState = state.value) {
                        is NewsUIState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        is NewsUIState.Success -> {
                            NewsScreen(news = currentState.news, viewModel = vm)
                        }
                        is NewsUIState.Error -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = currentState.message)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewsScreen(
    news: News?,
    viewModel: NewsViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 40.dp
            )
    ) {
        Header()
        Spacer(modifier = modifier.height(30.dp))
        SearchBar()
        Spacer(modifier = modifier.height(20.dp))
        SuggestionsRow(suggestion = SuggestionsList.list, viewModel = viewModel)
        Spacer(modifier = modifier.height(12.dp))
        if (news != null) {
            NewsSession(news = news.results)
        }
    }
}

@Composable
fun Header(modifier: Modifier = Modifier) {
    Text(
        text = "Discover",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = BlackTextColor
    )
    Spacer(modifier = modifier.height(4.dp))
    Text(
        text = "News from all around the world",
        color = GrayTextColor,
        fontSize = 14.sp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(modifier: Modifier = Modifier) {
    var text by remember {
        mutableStateOf("")
    }
    Row(
        modifier
            .fillMaxWidth()
            .background(color = Gray)
            .clip(shape = RoundedCornerShape(10.dp))
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = {text = it},
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = White,
                unfocusedBorderColor = White,
            ),
            modifier = modifier.weight(1f),
            placeholder = {
                Row {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    Spacer(modifier = modifier.width(10.dp))
                    Text(text = "Search")
                }
            }
        )
    }
}

@Composable
fun NewsSession(news: List<NewsResults>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(news) {currentNews ->
            NewsSessionItem(currentNews)
        }
    }
}

@Composable
fun NewsSessionItem(newsItem: NewsResults, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Image(
            painter = rememberAsyncImagePainter(newsItem.media[0].metadata[1].url),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(110.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        Spacer(modifier = modifier.width(10.dp))
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                newsItem.section,
                fontSize = 12.sp,
                color = GrayTextColor
            )
            Spacer(modifier = modifier.height(5.dp))
            Text(
                text = newsItem.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = BlackTextColor
            )
            Spacer(modifier = modifier.height(5.dp))
            Text(
                text = newsItem.publishedDate,
                fontSize = 12.sp,
                color = GrayTextColor
            )
        }
    }

}
var selectedSuggestionIndex by mutableStateOf(0)
@Composable
fun SuggestionsRow(
    suggestion: List<Suggestion>,
    viewModel: NewsViewModel
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(suggestion) {index, sug ->
            SuggestionItem(
                suggestion = sug,
                isFocused = index == selectedSuggestionIndex,
                onClick = {
                    selectedSuggestionIndex = index
                    viewModel.fetchNews()
                }
            )
        }
    }
}

@Composable
fun SuggestionItem(
    suggestion: Suggestion,
    isFocused: Boolean,
    onClick: () -> Unit,
) {
   Button(
       onClick = {onClick()},
       shape = RoundedCornerShape(40.dp),
       colors = ButtonDefaults.buttonColors(
           containerColor = if(isFocused) Blue else Gray
       )
   ) {
       Text(
           text = suggestion.title,
           color = if(isFocused) White else GrayTextColor
       )
   }
}

