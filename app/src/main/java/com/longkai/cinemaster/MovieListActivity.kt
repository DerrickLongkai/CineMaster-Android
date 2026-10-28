package com.longkai.cinemaster
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.longkai.cinemaster.ui.theme.CineMasterTheme

class MovieListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CineMasterTheme {
                MovieList()
            }
        }
    }
}

// Object-Oriented Programming structure for movies
data class Movie(
    //var id: Int,
    val title: String,
    //val posterImageResId: Int, // store poster
    val director: String,
    val starring: String,
    val type: String,
    val rating: String,
    val duration: String
)

@Composable
fun MovieList(){
    // top buttons state: Now Showing, Coming Soon
    var selectedTabIndex by remember { mutableStateOf(0) }
    var tabs = listOf("Now Showing", "Coming Soon")

    // now showing movie list
    var nowShowingMovies = listOf(
        Movie("Interstellar","Christopher Nolan","Matthew McConaughey","Sci-Fi","4.8","2h 49m"),
        Movie("Interstellar","Christopher Nolan","Matthew McConaughey","Sci-Fi","4.8","2h 49m")
    )
    // coming soon movie list
    var comingSoonMovies = listOf(
        Movie("Spider Man","Christopher Nolan","Matthew McConaughey","Sci-Fi","4.8","2h 49m"),
        Movie("Spider Man","Christopher Nolan","Matthew McConaughey","Sci-Fi","4.8","2h 49m")
    )

    // current display condition
    val currentDisplayMovies = if (selectedTabIndex == 0) nowShowingMovies else comingSoonMovies
    // check which page is selected: now showing = 0, coming soon = 1
    val isComingSoon = selectedTabIndex == 1

    // content
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {

        // top buttons
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.White,
            contentColor = Color(0xFFE91E63)
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            color = if (selectedTabIndex == index) Color.Black else Color.Gray,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Movie content change, pass the selected page boolean state
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(currentDisplayMovies) { movie ->
                MovieListItemView(movie = movie, isComingSoon = isComingSoon)
            }
        }
    }
}

// movie display content
@Composable
fun MovieListItemView(movie: Movie, isComingSoon: Boolean) {

    val context = LocalContext.current

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // movie poster
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(120.dp)
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // text content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Director: ${movie.director}",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Starring: ${movie.starring}",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

            // check which page is selected and then distinguish content.
            if (isComingSoon){

                // display the coming soon movies, with a detail button
                Button(
                    onClick = {  },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(text = "Detail", fontSize = 12.sp, color = Color.White)
                }
            }else{
                //  display the now showing movies, with a purchase button
                Button(
                    // purchase action
                    // move to showtime activity
                    onClick = {
                        val intent = Intent(context, ShowTimeActivity::class.java)
                        // add movie title here before going to next activity
                        intent.putExtra("MOVIE_TITLE", movie.title)
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(text = "Purchase", fontSize = 12.sp, color = Color.White)
                }
            }

        }

        // bottom gray spacer
        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
    }
}



