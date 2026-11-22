
package org.example.project.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CommentsScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {

        Text(
            "Comments & Feedback",
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFF15152D)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Read feedback and communicate with students.",
            color = Color(0xFF777799)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Comments list
        Box(
            modifier = Modifier.weight(1f)
        ) {
            mid()
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Message field
        ChatFiled()
    }
}


@Composable
fun mid() {

    val comments = listOf(
        "The classroom projector is not working.",
        "Please update the timetable.",
        "The canteen food quality is good.",
        "We need more library study space.",
        "Thank you for organizing the event."
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        items(comments) { comment ->

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        "Student Feedback",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF2855F5)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        comment,
                        color = Color(0xFF292945),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}


@Composable
fun ChatFiled() {

    var searchText by remember {
        mutableStateOf("")
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Write a comment...") },
            placeholder = { Text("Enter your message") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(18.dp),
            maxLines = 3
        )

        Spacer(modifier = Modifier.width(12.dp))

        Button(
            onClick = {
                if (searchText.isNotBlank()) {
                    println("Message: $searchText")
                    searchText = ""
                }
            },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.height(55.dp)
        ) {
            Text("Send")
        }
    }
}
