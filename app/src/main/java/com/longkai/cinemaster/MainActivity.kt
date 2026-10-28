package com.longkai.cinemaster
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.longkai.cinemaster.ui.theme.CineMasterTheme
import kotlin.jvm.java
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CineMasterTheme {
                // call and display the home page
                Home()
            }
        }
    }
}

// Home page implementation and combination
@Composable
fun Home(){
    // Using Scaffold structure to organize the home page
    Scaffold(bottomBar = {Bottom()}) {// fix the bottom buttons at the end.
        paddingValues ->
        Column(// column to stack the components
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(15.dp)// change the space between two components
        ) {
            // Break down each component in home screen for easier maintain
            Spacer(modifier = Modifier.height(5.dp))
            Header()
            Welcome()
            Movies()
            Location()
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

// Home page - Header function
@Composable
fun Header(){
    Row(
        // modifier to set the padding to the content
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            // header contents
            Row {
                Text(text = "Cine", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                Text(text = "Master", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE50914))
            }
            Text(text = "MOVIES BRING US TOGETHER",fontSize = 15.sp, color = Color.Gray, letterSpacing = 1.sp)
        }
    }
}
// Home page - Welcome function
@Composable
fun Welcome(){
    Surface(// surface to add background color to the content
        color = Color(0xFFFFF0F3),
        modifier = Modifier.fillMaxWidth()// modifier to set the padding to the content
    ) {
            //  Welcome contents
        Column(modifier = Modifier.padding(horizontal = 15.dp, vertical = 15.dp)) {
            Text(text = "Welcome to", fontSize = 30.sp, color = Color(0xFF4B5563))
            Text(text = "CineMaster", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Great movies. A better experience.", fontSize = 14.sp, color = Color(0xFF4B5563))
        }
    }
}

// Home page - Movies function
@Composable
fun Movies(){
    // get current page context
    val context = LocalContext.current

    Column(modifier = Modifier.padding(5.dp)) {
        // title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(text = "Now Showing", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            // "See all"  button
            Button(
                // will move to Movie list activity
                onClick = {
                    val intent = Intent(context, MovieListActivity::class.java)
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.Red),
                elevation = null
            ){
                Text(text = "See All >")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        //Movie List
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)// distance between each movie
        ){
            // Movies description
            MovieCard(modifier = Modifier.weight(1f), title = "Interstellar", type = "Sci-Fi", rate = "4.8")
            MovieCard(modifier = Modifier.weight(1f), title = "Dune", type = "Sci-Fi", rate = "4.5")
            MovieCard(modifier = Modifier.weight(1f), title = "The Batman", type = "Action", rate = "4.9")
        }
    }
}

@Composable
// movie card function to display movies in Home page
fun MovieCard(modifier: Modifier = Modifier, title: String, type: String, rate:String){
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(6.dp)) {
            // poster
            Box(
              modifier = Modifier
                  .fillMaxWidth()
                  .height(110.dp)
                  .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Movie name, if the length is longer than one row, then using ellipsis.
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)

            // type
            Text(text = type, color = Color.Gray, fontSize = 10.sp, maxLines = 1)

            // rate
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)){
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Star",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(text = rate, fontSize = 10.sp)
            }

            // Detail Button
            Button(
                onClick = {},
                modifier  = Modifier
                    .fillMaxWidth()
                    .height(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(text = "Details", fontSize = 10.sp)
            }

        }
    }
}
// Home page - Location function
@Composable
fun Location(){
    // using Card() to organise  the location components
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)// background color: white
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            // components 1 location icon and content. Row()
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ){
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFE5E5)),
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Location",
                        tint = Color.Red
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // text content
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "CineMaster Cork", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "20 Grand Parade, Cork", color = Color.Gray, fontSize = 12.sp)
                    Text(text = "1.8 km away", color = Color.Gray, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                //Location icon
                Box(
                    modifier = Modifier
                        .size(60.dp, 40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEEEEEE)),
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Map Pin",
                        tint = Color.Red,
                        modifier = Modifier.size(16.dp)
                    )

                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            val context = LocalContext.current
            // get direction button
            Button(
                onClick = {
                    // get direction: to open google map
                    // cinema location by passing latitude and longitude
                    val location = "51.9146,-8.1675"
                    val uri = Uri.parse("google.navigation:q=$location")

                    // create implicit intent
                    val mapIntent = Intent(Intent.ACTION_VIEW,uri)
                    mapIntent.setPackage("com.google.android.apps.maps")

                    // execute and handle the phone without Google map
                    try{
                        context.startActivity(mapIntent)
                    }catch (e: ActivityNotFoundException){
                        // if there is no Google map on this phone
                        Toast.makeText(context,"Sorry, Google Map was not found", Toast.LENGTH_SHORT).show()
                    }


                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Icon(
                    imageVector = Icons.Filled.Place,
                    contentDescription = "Directions",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Get Directions")
            }
        }
    }
}
// Home page - Bottom buttons function
@Composable
fun Bottom(){
    Surface(
        // add a background color to the bottom buttons
        color = Color.LightGray,
        modifier = Modifier.fillMaxWidth()
    ){
        // variable to remember the current selected button
        var selectedTab by remember { mutableStateOf("Home") }

        Row(// add padding to the buttons
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Home button
            Button(
                onClick = { selectedTab = "Home" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,// background color white
                    contentColor = Color.Black// text color black
                )) {
                // content to display Home
                Text(text = if (selectedTab == "Home") "Home   " else "Home")
            }
            // My Ticket button
            Button(
                onClick = { selectedTab = "My Tickets" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,// background color white
                    contentColor = Color.Black// text color black
                )) {
                // content to display My Ticket
                Text(text = if (selectedTab == "My Tickets") "My Tickets" else "My Tickets")
            }
        }


    }
}

