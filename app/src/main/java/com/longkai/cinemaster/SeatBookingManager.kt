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
import java.util.concurrent.locks.ReentrantLock

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

    // 1. Define explicit ReentrantLock and sold seats map
    private val lock = ReentrantLock()
    private val soldSeatsMap = mutableMapOf<String, MutableSet<String>>()

    // store booked tickets
    val tickets = mutableStateListOf<BookingTicket>()

    // default booked seats
    private val defaultSoldSeats = setOf(
        "1-3", "2-3", "2-4", "2-5", "2-8", "3-2",
        "3-6", "6-6", "6-7", "7-5", "7-7", "8-7"
    )

    // Thread-safe mechanism to read sold seats using ReentrantLock
    fun getSoldSeats(showtimeKey: String): Set<String> {
        lock.lock()
        try {
            return soldSeatsMap.getOrPut(showtimeKey) {
                defaultSoldSeats.toMutableSet()
            }.toSet()
        } finally {
            lock.unlock()
        }
    }

    // Check and update seat status atomically under ReentrantLock
    fun bookSeats(
        showtimeKey: String,
        movieTitle: String,
        showDate: String,
        showTime: String,
        hallName: String,
        selectedSeats: List<Seat>,
        unitPrice: Double
    ): BookingTicket? {
        // 1. Acquire the lock
        lock.lock()
        try {
            val currentSold = soldSeatsMap.getOrPut(showtimeKey) {
                defaultSoldSeats.toMutableSet()
            }
            val seatKeys = selectedSeats.map { "${it.row}-${it.col}" }

            // 2. Check seat availability: if any seat is already sold, return null
            if (seatKeys.any { currentSold.contains(it) }) {
                return null
            }

            // Booking successful: update sold seats set
            currentSold.addAll(seatKeys)

            // Create booking ticket
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
        } finally {
            // 3. Always release the lock in finally block to prevent deadlocks
            lock.unlock()
        }
    }
}

