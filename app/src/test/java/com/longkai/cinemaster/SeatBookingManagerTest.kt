package com.longkai.cinemaster

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger

class SeatBookingManagerTest {

    // clear history orders, before start a new test
    @Before
    fun setUp() {
        SeatBookingManager.tickets.clear()
    }

    @Test
    fun `two threads booking the same seat concurrently - only one succeeds`() {
        // prepare test data
        val showtimeKey = "Interstellar|Today, Nov 23|10:00 - 12:49|Hall 1"
        val targetSeat = listOf(Seat(row = 4, col = 4, status = SeatStatus.SELECTED))

        val successCount = AtomicInteger(0)
        val failCount = AtomicInteger(0)

        // Starting gun: Make two threads run simultaneously to create the maximum concurrent conflict
        val startGate = CountDownLatch(1)
        // End gate: Wait until both threads have completed their execution before making an assertion
        val endGate = CountDownLatch(2)

        val bookingTask = Runnable {
            try {
                startGate.await() // thread waiting here until startGate.countDown()

                val ticket = SeatBookingManager.bookSeats(
                    showtimeKey = showtimeKey,
                    movieTitle = "Interstellar",
                    showDate = "Today, Nov 23",
                    showTime = "10:00 - 12:49",
                    hallName = "Hall 1",
                    selectedSeats = targetSeat,
                    unitPrice = 15.0
                )

                if (ticket != null) {
                    successCount.incrementAndGet()
                    println("${Thread.currentThread().name}: booked successfully! Ticket ID = ${ticket.ticketId}")
                } else {
                    failCount.incrementAndGet()
                    println("${Thread.currentThread().name}: booked failed(seat sold)")
                }
            } finally {
                endGate.countDown()
            }
        }

        // create two thread to click Confirm
        val userAThread = Thread(bookingTask, "Thread-UserA")
        val userBThread = Thread(bookingTask, "Thread-UserB")

        userAThread.start()
        userBThread.start()

        // start entering bookSeats()
        startGate.countDown()

        // waiting for to finish
        endGate.await()

        // verify result
        assertEquals("Only one thread should have been successfully booked", 1, successCount.get())
        assertEquals("Only one thread should have been booked failed", 1, failCount.get())
        assertEquals("Only one ticket should be generated in the order list", 1, SeatBookingManager.tickets.size)
        assertTrue(
            "This seat must be marked as sold in the warehouse (4-4)",
            SeatBookingManager.getSoldSeats(showtimeKey).contains("4-4")
        )
    }

    @Test
    fun `10 threads booking the same seat concurrently - only one succeeds`() {
        // Ten users are competing for the same seat simultaneously
        val showtimeKey = "Interstellar|Tomorrow, Nov 24|20:30 - 23:19|IMAX Hall"
        val targetSeat = listOf(Seat(row = 5, col = 5, status = SeatStatus.SELECTED))

        val threadCount = 10
        val successCount = AtomicInteger(0)
        val startGate = CountDownLatch(1)
        val endGate = CountDownLatch(threadCount)

        repeat(threadCount) { i ->
            Thread({
                try {
                    startGate.await()
                    val ticket = SeatBookingManager.bookSeats(
                        showtimeKey = showtimeKey,
                        movieTitle = "Interstellar",
                        showDate = "Tomorrow, Nov 24",
                        showTime = "20:30 - 23:19",
                        hallName = "IMAX Hall",
                        selectedSeats = targetSeat,
                        unitPrice = 22.0
                    )
                    if (ticket != null) successCount.incrementAndGet()
                } finally {
                    endGate.countDown()
                }
            }, "User-$i").start()
        }

        startGate.countDown()
        endGate.await()

        assertEquals("When ten threads are competing for a seat simultaneously, only one must succeed", 1, successCount.get())
        assertEquals("Only one ticket should be generated in the order list", 1, SeatBookingManager.tickets.size)
    }
}