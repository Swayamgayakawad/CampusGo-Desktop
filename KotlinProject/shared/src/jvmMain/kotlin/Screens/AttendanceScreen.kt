
package org.example.project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.generateQRCode

private val PrimaryBlue = Color(0xFF2563EB)
private val BackgroundColor = Color(0xFFF5F7FB)
private val TextDark = Color(0xFF172033)
private val Green = Color(0xFF16A34A)
private val Red = Color(0xFFDC2626)

data class AttendanceStudent(
    val name: String,
    val usn: String,
    val status: String
)

@Composable
fun AttendanceScreen() {

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Column(
            modifier = Modifier.weight(1.5f),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            top()

            AttenedStudentList()
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            QrViwe()
        }
    }
}

// TOP HEADER

@Composable
fun top() {

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {

        Text(
            "Attendance Management",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )

        Text(
            "Manage student attendance and generate QR codes",
            color = Color.Gray,
            fontSize = 14.sp
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            SummaryCard("Total Students", "60", PrimaryBlue)
            SummaryCard("Present", "48", Green)
            SummaryCard("Absent", "12", Red)
        }
    }
}


@Composable
fun SummaryCard(
    title: String,
    count: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(title, color = Color.Gray, fontSize = 13.sp)

            Spacer(Modifier.height(8.dp))

            Text(
                count,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

// STUDENT LIST

@Composable
fun AttenedStudentList() {

    val students = listOf(
        AttendanceStudent("Rahul Sharma", "1MV23CS001", "Present"),
        AttendanceStudent("Amit Kumar", "1MV23CS002", "Present"),
        AttendanceStudent("Sneha Patil", "1MV23CS003", "Absent"),
        AttendanceStudent("Priya Singh", "1MV23CS004", "Present"),
        AttendanceStudent("Rohit Verma", "1MV23CS005", "Absent"),
        AttendanceStudent("Akash Rao", "1MV23CS006", "Present"),
        AttendanceStudent("Kiran M", "1MV23CS007", "Present")
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(Modifier.padding(18.dp)) {

            Text(
                "Student Attendance",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundColor)
                    .padding(12.dp)
            ) {
                Text("Student", Modifier.weight(1.5f), fontWeight = FontWeight.Bold)
                Text("USN", Modifier.weight(1.3f), fontWeight = FontWeight.Bold)
                Text("Status", Modifier.weight(1f), fontWeight = FontWeight.Bold)
            }

            LazyColumn(
                modifier = Modifier.heightIn(max = 450.dp)
            ) {

                items(students) { student ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            student.name,
                            Modifier.weight(1.5f),
                            fontSize = 13.sp
                        )

                        Text(
                            student.usn,
                            Modifier.weight(1.3f),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )

                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {

                            Text(
                                student.status,
                                color = if (student.status == "Present") Green else Red,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    HorizontalDivider(color = BackgroundColor)
                }
            }
        }
    }
}

// QR CODE PANEL



@Composable
fun QrViwe() {

    var selectedSubject by remember {
        mutableStateOf("Data Structures")
    }

    var qrImage by remember {
        mutableStateOf<ImageBitmap?>(null)
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    var showFullScreen by remember {
        mutableStateOf(false)
    }

    val subjects = listOf(
        "Data Structures",
        "Operating Systems",
        "Computer Networks",
        "Artificial Intelligence"
    )

    // FULL SCREEN QR VIEW
    if (showFullScreen && qrImage != null) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                "CampusGo Attendance",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            Text(
                selectedSubject,
                fontSize = 20.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(30.dp))

            Image(
                bitmap = qrImage!!,
                contentDescription = "Full Screen Attendance QR",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Spacer(Modifier.height(25.dp))

            Button(
                onClick = {
                    showFullScreen = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Close Full Screen")
            }
        }

        return
    }

    // NORMAL QR GENERATOR VIEW
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "Generate Attendance QR",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(20.dp))

            Text(
                "Select Subject",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Box {

                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(selectedSubject)
                    Spacer(Modifier.weight(1f))
                    Text("▼")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {

                    subjects.forEach { subject ->

                        DropdownMenuItem(
                            text = { Text(subject) },
                            onClick = {
                                selectedSubject = subject
                                expanded = false
                                qrImage = null
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(25.dp))

            Box(
                modifier = Modifier
                    .size(240.dp)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {

                if (qrImage != null) {

                    Image(
                        bitmap = qrImage!!,
                        contentDescription = "Attendance QR Code",
                        modifier = Modifier.size(220.dp)
                    )

                } else {

                    Text(
                        "QR Code Preview",
                        color = Color.Gray
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    val qrData =
                        "CAMPUSGO|ATTENDANCE|$selectedSubject"

                    qrImage = generateQRCode(qrData)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Generate QR Code")
            }

            Spacer(Modifier.height(10.dp))

            // FULL SCREEN BUTTON
            OutlinedButton(
                onClick = {
                    if (qrImage != null) {
                        showFullScreen = true
                    }
                },
                enabled = qrImage != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Full Screen")
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    qrImage = null
                    showFullScreen = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reset")
            }
        }
    }
}
