package com.longkai.cinemaster

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.longkai.cinemaster.ui.theme.CineMasterTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ShowTimeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Receive a movie name selected from movie list activity
        val movieTitle = intent.getStringExtra("MOVIE_TITLE") ?: "Movie Details"
        setContent {
            CineMasterTheme {
                ShowtimeScreen(movieTitle = movieTitle)
            }
        }
    }
}

// object-oriented structure for showtime
data class Showtime(
    val time: String,
    val endTime: String,
    val format: String,
    val hall: String,
    val price: String
)

// show time screen
@Composable
fun ShowtimeScreen(movieTitle: String) {
    // put the date list and the currently selected item here for easy passing to ShowtimeList
    var selectedDateIndex by remember { mutableStateOf(0) }
    val dates = remember {
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("MMM d", Locale.ENGLISH)
        List(5) { i ->
            if (i > 0) calendar.add(Calendar.DAY_OF_YEAR, 1)
            val formattedDate = formatter.format(calendar.time)
            when (i) {
                0 -> "Today, $formattedDate"
                1 -> "Tomorrow, $formattedDate"
                else -> formattedDate
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopBar(movieTitle)
        DateTabs(
            dates = dates,
            selectedDateIndex = selectedDateIndex,
            onDateSelected = { selectedDateIndex = it }
        )
        ShowtimeList(
            movieTitle = movieTitle,
            selectedDate = dates[selectedDateIndex]
        )
    }
}

// top bar: return button, a movie name from last activity
@Composable
fun TopBar(movieTitle: String) {
    val activity = LocalActivity.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier.clickable { activity?.finish() }
        )

        Text(
            text = movieTitle,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.width(24.dp))
    }
}

// Date tabs
@Composable
fun DateTabs(
    dates: List<String>,
    selectedDateIndex: Int,
    onDateSelected: (Int) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedDateIndex,
        containerColor = Color.White,
        contentColor = Color(0xFFE91E63),
        edgePadding = 8.dp
    ) {
        dates.forEachIndexed { index, dateText ->
            Tab(
                selected = selectedDateIndex == index,
                onClick = { onDateSelected(index) },
                text = {
                    Text(
                        text = dateText,
                        color = if (selectedDateIndex == index) Color(0xFFE91E63) else Color.Gray,
                        fontWeight = if (selectedDateIndex == index) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }
    }
}

// ShowtimeList
@Composable
fun ShowtimeList(movieTitle: String, selectedDate: String) {
    val showtime = listOf(
        Showtime("10:00", "Ends 12:49", "English 2D", "Hall 1", "€15.00"),
        Showtime("13:30", "Ends 16:19", "English IMAX", "IMAX Hall", "€22.00"),
        Showtime("17:00", "Ends 19:49", "English 2D", "VIP Hall 3", "€18.00"),
        Showtime("20:30", "Ends 23:19", "English IMAX", "IMAX Hall", "€22.00"),
        Showtime("23:40", "Ends 02:29", "English 2D", "Hall 2", "€12.00")
    )

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(showtime) { showtime ->
            ShowtimeItem(
                showtime = showtime,
                movieTitle = movieTitle,
                selectedDate = selectedDate
            )
        }
    }
}

// ShowtimeItem: showtime content
@Composable
fun ShowtimeItem(
    showtime: Showtime,
    movieTitle: String,
    selectedDate: String
) {
    val context = LocalContext.current

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // row1 start time and end time information
            Column(modifier = Modifier.width(80.dp)) {
                Text(text = showtime.time, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(text = showtime.endTime, color = Color.Gray, fontSize = 12.sp)
            }

            // row2 format and hall
            Column(modifier = Modifier.weight(1f)) {
                Text(text = showtime.format, fontSize = 14.sp, color = Color.DarkGray)
                Text(text = showtime.hall, color = Color.Gray, fontSize = 12.sp)
            }

            // price and seat selection button
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = showtime.price,
                    color = Color(0xFFE91E63),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        // remove "Ends " become "10:00 - 12:49"
                        val cleanEndTime = showtime.endTime.removePrefix("Ends ")
                        // convert "€" String to a double value
                        val numericPrice = showtime.price.removePrefix("€").toDoubleOrNull() ?: 0.0

                        val intent = Intent(context, SeatSelectionActivity::class.java).apply {
                            putExtra("MOVIE_TITLE", movieTitle)
                            putExtra("SHOW_DATE", selectedDate)
                            putExtra("SHOW_TIME", "${showtime.time} - $cleanEndTime")
                            putExtra("HALL_NAME", showtime.hall)
                            putExtra("TICKET_PRICE", numericPrice)
                        }
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFCE4EC),
                        contentColor = Color(0xFFE91E63)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(text = "Select Seat", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
    }
}