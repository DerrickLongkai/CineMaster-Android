package com.longkai.cinemaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.longkai.cinemaster.ui.theme.CineMasterTheme
import androidx.compose.runtime.mutableStateListOf
import java.util.UUID

// booking data model
data class BookingTicket(
    val ticketId: String = "TKT-${UUID.randomUUID().toString().take(6).uppercase()}",
    val movieTitle: String,
    val showDate: String,
    val showTime: String,
    val hallName: String,
    val seats: List<String>,
    val totalPrice: Double
)

object SeatBookingManager {

    // define shared Lock and sold seats set
    private val lock = Any()
    private val soldSeatsMap = mutableMapOf<String, MutableSet<String>>()

    // store booked ticket
    val tickets = mutableStateListOf<BookingTicket>()

    // default booked seats
    private val defaultSoldSeats = setOf(
        "1-3", "2-3", "2-4", "2-5", "2-8", "3-2",
        "3-6", "6-6", "6-7", "7-5", "7-7", "8-7"
    )

    // thread-safe machinism  to read sold seats
    fun getSoldSeats(showtimeKey: String): Set<String> {
        synchronized(lock) {
            return soldSeatsMap.getOrPut(showtimeKey) {
                defaultSoldSeats.toMutableSet()
            }.toSet()
        }
    }

    // in 1 synchronized situation to finalize checking and updating
    fun bookSeats(
        showtimeKey: String,
        movieTitle: String,
        showDate: String,
        showTime: String,
        hallName: String,
        selectedSeats: List<Seat>,
        unitPrice: Double
    ): BookingTicket? {
        synchronized(lock) {
            val currentSold = soldSeatsMap.getOrPut(showtimeKey) {
                defaultSoldSeats.toMutableSet()
            }
            val seatKeys = selectedSeats.map { "${it.row}-${it.col}" }

            // check: if any seat is in currentSold, return null
            if (seatKeys.any { currentSold.contains(it) }) {
                return null
            }

            // update: add selected seat to Sold set
            currentSold.addAll(seatKeys)

            // create order document
            val ticket = BookingTicket(
                movieTitle = movieTitle,
                showDate = showDate,
                showTime = showTime,
                hallName = hallName,
                seats = selectedSeats.map { it.label },
                totalPrice = selectedSeats.size * unitPrice
            )
            tickets.add(0, ticket)
            return ticket
        }
    }
}

