
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

data class Student(
    val name: String,
    val usn: String,
    val branch: String,
    val year: String
)

@Composable
fun StudentsScreen() {

    var searchText by remember { mutableStateOf("") }

    val students = listOf(
        Student("Swayam Gayakawad", "1Mj25iS406", "Information Science", "3rd Year"),
        Student("Rahul Sharma", "1MV23CS001", "Computer Science", "3rd Year"),
        Student("Priya Kumar", "1MV23IS002", "Information Science", "3rd Year"),
        Student("Amit Patel", "1MV24CS003", "Computer Science", "2nd Year"),
        Student("Sneha Rao", "1MV24EC004", "Electronics", "2nd Year"),
        Student("Arjun Reddy", "1MV22IS005", "Information Science", "4th Year"),
        Student("Rahul Sharma", "1MV23CS001", "Computer Science", "3rd Year"),
        Student("Priya Kumar", "1MV23IS002", "Information Science", "3rd Year"),
        Student("Amit Patel", "1MV24CS003", "Computer Science", "2nd Year"),
        Student("Sneha Rao", "1MV24EC004", "Electronics", "2nd Year"),
        Student("Arjun Reddy", "1MV22IS005", "Information Science", "4th Year"),
        Student("Rahul Sharma", "1MV23CS001", "Computer Science", "3rd Year"),
        Student("Priya Kumar", "1MV23IS002", "Information Science", "3rd Year"),
        Student("Amit Patel", "1MV24CS003", "Computer Science", "2nd Year"),
        Student("Sneha Rao", "1MV24EC004", "Electronics", "2nd Year"),
        Student("Arjun Reddy", "1MV22IS005", "Information Science", "4th Year")
    )

    val filteredStudents = students.filter {
        it.name.contains(searchText, ignoreCase = true) ||
                it.usn.contains(searchText, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {

        Text(
            "Student Management",
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFF15152D)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "View and manage all registered students.",
            color = Color(0xFF777799)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Search field
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search by name or USN...") },
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Student count
        Text(
            "Students (${filteredStudents.size})",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF25264A)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Student list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filteredStudents.isEmpty()) {
                item {
                    Text(
                        "No students found.",
                        modifier = Modifier.padding(20.dp),
                        color = Color.Gray
                    )
                }
            } else {
                items(filteredStudents) { student ->
                    StudentCard(student)
                }
            }
        }
    }
}

@Composable
fun StudentCard(student: Student) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Student initials
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFE5EAFF),
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        student.name.first().toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF2855F5)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    student.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF15152D)
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    student.usn,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF777799)
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    "${student.branch} • ${student.year}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF555577)
                )
            }
        }
    }
}
