
package org.example.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CampusGoSidebar(
    selectedScreen: String,
    onScreenSelected: (String) -> Unit
) {

    val navigationItems = listOf(
        "Dashboard" to "🏠",
        "Timetable" to "📅",
        "Attendance" to "✔",
        "Students" to "👥",
        "Comments" to "💬",
        "Settings" to "⚙"
    )

    Column(
        modifier = Modifier
            .width(260.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE5EAFF),
                        Color(0xFFD6DEFF),
                        Color(0xFFCFD9FF)
                    )
                )
            )
            .padding(18.dp)
    ) {

        // Logo and title
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "🎓",
                style = MaterialTheme.typography.displayMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "CampusGo",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF151B50)
            )

            Text(
                text = "Smart Campus, Smart You",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF535B91)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Navigation buttons
        navigationItems.forEach { (screen, emoji) ->

            val isSelected = selectedScreen == screen

            Button(
                onClick = {
                    onScreenSelected(screen)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected)
                        Color(0xFF2855F5)
                    else
                        Color.White.copy(alpha = 0.65f),

                    contentColor = if (isSelected)
                        Color.White
                    else
                        Color(0xFF171B50)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = if (isSelected) 4.dp else 0.dp
                )
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = emoji,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = screen,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        // Footer
        Text(
            text = "CampusGo Staff",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            color = Color(0xFF535B91),
            style = MaterialTheme.typography.bodySmall
        )
    }
}
