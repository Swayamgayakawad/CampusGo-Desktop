
package org.example.project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen() {

    val blue = Color(0xFF2563EB)
    val purple = Color(0xFF7657E8)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FF))
            .padding(28.dp)
    ) {

        // Header
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFF15152D)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Welcome to CampusGo Staff!",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF777799)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // CampusGo Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(blue, purple)
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(28.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Column {
                Text(
                    text = "🎓 CampusGo",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Manage  •  Monitor  •  Grow",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Better Campus, Better Tomorrow",
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Statistics Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            StatCard(
                title = "Total Students",
                value = "1200",
                emoji = "👥",
                modifier = Modifier.weight(1f),
                iconColor = Color(0xFFE1ECFF)
            )

            StatCard(
                title = "Today's Classes",
                value = "42",
                emoji = "📅",
                modifier = Modifier.weight(1f),
                iconColor = Color(0xFFDDF5E9)
            )

            StatCard(
                title = "Attendance",
                value = "87%",
                emoji = "📊",
                modifier = Modifier.weight(1f),
                iconColor = Color(0xFFF0E2FF)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Recent Updates
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(22.dp)
            ) {

                Text(
                    text = "📢  Recent Updates",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF20203A)
                )

                Spacer(modifier = Modifier.height(18.dp))

                UpdateItem(
                    message = "Timetable for next week is updated.",
                    time = "2 hours ago",
                    dotColor = blue
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color(0xFFEEEEF5)
                )

                UpdateItem(
                    message = "Campus fest registration is open!",
                    time = "1 day ago",
                    dotColor = Color(0xFF16A34A)
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color(0xFFEEEEF5)
                )

                UpdateItem(
                    message = "Maintenance scheduled on 5th Nov, 10 AM.",
                    time = "2 days ago",
                    dotColor = purple
                )
            }
        }
    }
}


// Statistics Card
@Composable
fun StatCard(
    title: String,
    value: String,
    emoji: String,
    modifier: Modifier = Modifier,
    iconColor: Color
) {
    Card(
        modifier = modifier.height(145.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(55.dp)
                    .background(
                        iconColor,
                        RoundedCornerShape(18.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF17172D)
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF777799)
                )
            }
        }
    }
}


// Recent Update Item
@Composable
fun UpdateItem(
    message: String,
    time: String,
    dotColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(10.dp)
                .background(
                    dotColor,
                    RoundedCornerShape(50)
                )
        )

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = message,
            modifier = Modifier.weight(1f),
            color = Color(0xFF292945),
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = time,
            color = Color(0xFF8585A0),
            style = MaterialTheme.typography.bodySmall
        )
    }
}
