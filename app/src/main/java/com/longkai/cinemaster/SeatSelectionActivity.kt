package com.longkai.cinemaster

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.longkai.cinemaster.ui.theme.CineMasterTheme

class SeatSelectionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // accept data from showtime activity
        val movieTitle = intent.getStringExtra("MOVIE_TITLE") ?: "Movie Details"
        val showDate = intent.getStringExtra("SHOW_DATE") ?: ""
        val showTime = intent.getStringExtra("SHOW_TIME") ?: ""
        val hallName = intent.getStringExtra("HALL_NAME") ?: ""
        val ticketPrice = intent.getDoubleExtra("TICKET_PRICE", 15.0)

        setContent {
            CineMasterTheme {
                SeatSelectionScreen(
                    movieTitle = movieTitle,
                    showtimeInfo = "Showtime: $showDate | $showTime | $hallName",
                    ticketPrice = ticketPrice
                )
            }
        }
    }
}

// Seat data model and seat status enum
enum class SeatStatus { AVAILABLE, SOLD, SELECTED }

data class Seat(
    val row: Int,
    val col: Int,
    val status: SeatStatus = SeatStatus.AVAILABLE,
    val isAisle: Boolean = false // for aisle
) {
    val label: String get() = "Row $row Seat $col"
}

@Composable
fun SeatSelectionScreen(
    movieTitle: String,
    showtimeInfo: String,
    ticketPrice: Double
) {
    val context = LocalContext.current
    val pinkColor = Color(0xFFE91E63)
    val bgColor = Color(0xFFF8F9FA)

    // simulate sold seats
    val soldSeatKeys = setOf("1-3", "2-3", "2-4", "2-5", "2-8", "3-2", "3-6", "6-6", "6-7", "7-5", "7-7", "8-7")

    //  mutableStateListOf manage seats status
    val seats = remember {
        mutableStateListOf<Seat>().apply {
            for (row in 1..8) {
                for (gridCol in 1..9) {
                    if (gridCol == 5) {
                        // fifth column for aisle
                        add(Seat(row = row, col = 0, isAisle = true))
                    } else {
                        // right number -1, to count from 1 to 8
                        val actualCol = if (gridCol > 5) gridCol - 1 else gridCol
                        val status = if (soldSeatKeys.contains("$row-$actualCol")) {
                            SeatStatus.SOLD
                        } else {
                            SeatStatus.AVAILABLE
                        }
                        add(Seat(row = row, col = actualCol, status = status))
                    }
                }
            }
        }
    }

    // calculate price based on selected seats
    val selectedSeats = seats.filter { !it.isAisle && it.status == SeatStatus.SELECTED }
    val totalPrice = selectedSeats.size * ticketPrice

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // top movie information
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
                text = showtimeInfo,
                fontSize = 13.sp,
                color = Color.Gray
            )
        }

        // status instruction
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

        // screen bar
        Box(
            modifier = Modifier
                .padding(top = 12.dp, bottom = 16.dp)
                .width(200.dp)
                .height(24.dp)
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFE8E8E8)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "SCREEN", fontSize = 11.sp, color = Color.Gray)
        }

        // 2. use LazyVerticalGrid display seat
        LazyVerticalGrid(
            columns = GridCells.Fixed(9),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(seats) { index, seat ->
                if (seat.isAisle) {

                    Spacer(modifier = Modifier.aspectRatio(1f))
                } else {
                    SeatItem(
                        seat = seat,
                        onClick = {
                            // status exchange: available <-> selected
                            val newStatus = if (seat.status == SeatStatus.AVAILABLE) {
                                SeatStatus.SELECTED
                            } else {
                                SeatStatus.AVAILABLE
                            }
                            seats[index] = seat.copy(status = newStatus)
                        }
                    )
                }
            }
        }

        // bottom information
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (selectedSeats.isEmpty()) {
                        "No seats selected"
                    } else {
                        selectedSeats.joinToString(", ") { it.label }
                    },
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
                        text = String.format("€%.2f", totalPrice),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = pinkColor
                    )
                    Button(
                        onClick = {
                            if (selectedSeats.isEmpty()) {
                                Toast.makeText(context, "Please select at least one seat", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Confirmed: ${selectedSeats.joinToString(", ") { it.label }}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        },
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
fun SeatItem(seat: Seat, onClick: () -> Unit) {
    val shape = RoundedCornerShape(6.dp)
    val baseModifier = Modifier
        .aspectRatio(1f) // keep square to fit the screen
        .clip(shape)

    val finalModifier = when (seat.status) {
        SeatStatus.AVAILABLE -> baseModifier
            .background(Color.White)
            .border(1.5.dp, Color(0xFFB0BEC5), shape)
            .clickable { onClick() } // clickable

        SeatStatus.SELECTED -> baseModifier
            .background(Color(0xFFE91E63))
            .clickable { onClick() } // cancel

        SeatStatus.SOLD -> baseModifier
            .background(Color(0xFFE8E8E8)) // not clickable for sold seats
    }

    Box(modifier = finalModifier)
}

@Composable
fun LegendItem(status: SeatStatus, label: String) {
    val shape = RoundedCornerShape(4.dp)
    val boxModifier = when (status) {
        SeatStatus.AVAILABLE -> Modifier
            .size(18.dp)
            .clip(shape)
            .background(Color.White)
            .border(1.5.dp, Color(0xFFB0BEC5), shape)
        SeatStatus.SOLD -> Modifier
            .size(18.dp)
            .clip(shape)
            .background(Color(0xFFE8E8E8))
        SeatStatus.SELECTED -> Modifier
            .size(18.dp)
            .clip(shape)
            .background(Color(0xFFE91E63))
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = boxModifier)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, fontSize = 12.sp, color = Color.DarkGray)
    }
}