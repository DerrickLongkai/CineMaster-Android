package com.longkai.cinemaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.longkai.cinemaster.ui.theme.CineMasterTheme

class SeatSelectionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get movie data from showtimeActivity
        val movieTitle = intent.getStringExtra("MOVIE_TITLE") ?: ""
        val showDate = intent.getStringExtra("SHOW_DATE") ?: ""
        val showtime = intent.getStringExtra("SHOW_TIME") ?: ""
        val ticketPrice = intent.getDoubleExtra("TICKET_PRICE", 0.0)
        val hallName = intent.getStringExtra("HALL_NAME") ?: ""
        setContent {
            CineMasterTheme {

                SeatSelection(
                    movieTitle = movieTitle,
                    showtime =  "Showtime: $showDate | ${showtime} | ${hallName}",
                    ticketPrice = ticketPrice
                )
            }
        }
    }
}

// seat status enum: available, sold, selected
enum class SeatStatus{AVAILABLE, SOLD, SELECTED}

// seat selection display function
@Composable
fun SeatSelection(
    movieTitle: String,
    showtime: String,
    ticketPrice: Double
){

    val pinkColor = Color(0xFFE91E63)
    val bgColor = Color(0xFFF8F9FA)
    // temporary seat place holder
    val soldSeats = setOf("1-3", "2-3", "2-4", "2-5", "2-8", "3-2", "3-6", "6-6", "6-7", "7-5", "7-7", "8-7")
    val selectedSeats = setOf("7-3", "7-4")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {

        // 1: Top movie information
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(
                text = movieTitle,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = showtime,
                fontSize = 13.sp,
                color = Color.Gray
            )
        }

        // 2. Status instruction(Available / Sold / Selected)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(status = SeatStatus.AVAILABLE, label = "Available")
            Spacer(modifier = Modifier.width(16.dp))
            LegendItem(status = SeatStatus.SOLD, label = "Sold")
            Spacer(modifier = Modifier.width(16.dp))
            LegendItem(status = SeatStatus.SELECTED, label = "Selected")
        }


        // 3. screen bar
        Box(
            modifier = Modifier
                .padding(top = 12.dp, bottom = 20.dp)
                .width(200.dp)
                .height(24.dp)
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFE8E8E8)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "SCREEN", fontSize = 11.sp, color = Color.Gray)
        }

        // 4. 8x8 seat area
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (row in 1..8) {
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (col in 1..8) {
                        // 第 4 列和第 5 列之间加过道
                        if (col == 5) {
                            Spacer(modifier = Modifier.width(24.dp))
                        }

                        val key = "$row-$col"
                        val status = when {
                            soldSeats.contains(key) -> SeatStatus.SOLD
                            selectedSeats.contains(key) -> SeatStatus.SELECTED
                            else -> SeatStatus.AVAILABLE
                        }

                        SeatBox(status = status)
                        if (col != 4 && col != 8) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            }
        }

        // 5. bottom information
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Row 7 Seat 3, Row 7 Seat 4",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "30.00",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = pinkColor
                    )
                    Button(
                        onClick = { /*  */ },
                        colors = ButtonDefaults.buttonColors(containerColor = pinkColor),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .width(140.dp)
                            .height(44.dp)
                    ) {
                        Text(text = "Confirm", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(status: SeatStatus, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        SeatBox(status = status, sizeDp = 18)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, fontSize = 12.sp, color = Color.DarkGray)
    }
}
@Composable
fun SeatBox(status: SeatStatus, sizeDp: Int = 34) {
    val shape = RoundedCornerShape(6.dp)
    val modifier = when (status) {
        SeatStatus.AVAILABLE -> Modifier
            .size(sizeDp.dp)
            .clip(shape)
            .background(Color.White)
            .border(1.5.dp, Color(0xFFB0BEC5), shape)
        SeatStatus.SOLD -> Modifier
            .size(sizeDp.dp)
            .clip(shape)
            .background(Color(0xFFE8E8E8))
        SeatStatus.SELECTED -> Modifier
            .size(sizeDp.dp)
            .clip(shape)
            .background(Color(0xFFE91E63))
    }
    Box(modifier = modifier)
}
