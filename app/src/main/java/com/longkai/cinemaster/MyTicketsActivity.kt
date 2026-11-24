package com.longkai.cinemaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.longkai.cinemaster.ui.theme.CineMasterTheme
import kotlin.random.Random

class MyTicketsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CineMasterTheme {
                MyTicketsScreen()
            }
        }
    }
}

// my ticket main page layout function
@Composable
fun MyTicketsScreen() {
    val activity = LocalActivity.current
    // obtain booked ticket
    val tickets = SeatBookingManager.tickets

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // top navigation bar(return)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // return icon
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.clickable { activity?.finish() }
            )
            Text(
                text = "My Tickets",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(24.dp))
        }

        // check if the ticket page is empty
        // if it is then display: no tickets booked yet.
        if (tickets.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No tickets booked yet.", color = Color.Gray, fontSize = 15.sp)
            }
        }
        // if not empty
        // display all booking order
        else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tickets) { ticket ->
                    TicketCard(ticket = ticket)
                }
            }
        }
    }
}

// order layout and components function
@Composable
fun TicketCard(ticket: BookingTicket) {
    val pinkColor = Color(0xFFE91E63)// main color: pink
    // control QR code window
    var showQrDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // row 1: movie name, QR code view button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ticket.movieTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                // view button to display QR code
                Button(
                    onClick = { showQrDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFCE4EC),
                        contentColor = pinkColor
                    ),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text(
                        text = "View",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // row 2: date, time, hall
            Text(
                text = "${ticket.showDate} | ${ticket.showTime} | ${ticket.hallName}",
                fontSize = 13.sp,
                color = Color.Gray
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = Color(0xFFEEEEEE)
            )

            // row 3: seat number, price
            Text(
                text = "Seats: ${ticket.seats.joinToString(", ")}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray
            )
            Text(
                text = String.format("€%.2f", ticket.totalPrice),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = pinkColor
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    // click 'view' button display QR code
    if (showQrDialog) {
        TicketQrDialog(
            ticket = ticket,
            onDismiss = { showQrDialog = false }
        )
    }
}

// QR code component
@Composable
fun TicketQrDialog(ticket: BookingTicket, onDismiss: () -> Unit) {
    val pinkColor = Color(0xFFE91E63)

    // layout
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top title
                Text(
                    text = "CINEMA TICKET",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = pinkColor,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // movie name
                Text(
                    text = ticket.movieTitle,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                // showtime
                Text(
                    text = "Showtime: ${ticket.showDate} | ${ticket.showTime}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                //
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                ) {
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // seats and total price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "SEATS", fontSize = 10.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = ticket.seats.joinToString(", "),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "TOTAL", fontSize = 10.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = String.format("€%.2f", ticket.totalPrice),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = pinkColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // generate QR Code based on ticket ID
                QrCodeGraphic(
                    seedText = ticket.ticketId,
                    modifier = Modifier.size(160.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Scan at the entrance",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

// Draw a QR code component with three positioning angles (each ticketId generates a unique pattern)
@Composable
fun QrCodeGraphic(seedText: String, modifier: Modifier = Modifier) {
    val gridCount = 21 // QR code standard layout 21x21
    // according to Hash value to generate random matrix
    val matrix = remember(seedText) {
        val random = Random(seedText.hashCode())
        Array(gridCount) { r ->
            BooleanArray(gridCount) { c ->
                val isTopLeft = r < 8 && c < 8
                val isTopRight = r < 8 && c >= gridCount - 8
                val isBottomLeft = r >= gridCount - 8 && c < 8
                if (isTopLeft || isTopRight || isBottomLeft) false else random.nextBoolean()
            }
        }
    }

    Canvas(modifier = modifier) {
        val cellSize = size.width / gridCount

        //
        for (r in 0 until gridCount) {
            for (c in 0 until gridCount) {
                if (matrix[r][c]) {
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(c * cellSize, r * cellSize),
                        size = Size(cellSize, cellSize)
                    )
                }
            }
        }

        //
        fun drawFinderPattern(startRow: Int, startCol: Int) {
            //
            drawRect(
                color = Color.Black,
                topLeft = Offset(startCol * cellSize, startRow * cellSize),
                size = Size(7 * cellSize, 7 * cellSize)
            )
            //
            drawRect(
                color = Color.White,
                topLeft = Offset((startCol + 1) * cellSize, (startRow + 1) * cellSize),
                size = Size(5 * cellSize, 5 * cellSize)
            )
            //
            drawRect(
                color = Color.Black,
                topLeft = Offset((startCol + 2) * cellSize, (startRow + 2) * cellSize),
                size = Size(3 * cellSize, 3 * cellSize)
            )
        }

        drawFinderPattern(0, 0)
        drawFinderPattern(0, gridCount - 7)
        drawFinderPattern(gridCount - 7, 0)
    }
}