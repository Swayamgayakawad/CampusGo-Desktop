import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.ui.screens.*
import org.example.project.ui.components.CampusGoSidebar

@Composable
fun App() {

    var selectedScreen by remember {
        mutableStateOf("Dashboard")
    }

    Row(
        modifier = Modifier.fillMaxSize()
    ) {

        CampusGoSidebar(
            selectedScreen = selectedScreen,
            onScreenSelected = { selectedScreen = it }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(30.dp)
        ) {
            when (selectedScreen) {
                "Dashboard" -> DashboardScreen()
                "Timetable" -> TimetableScreen()
                "Attendance" -> AttendanceScreen()
                "Students" -> StudentsScreen()
                "Comments" -> CommentsScreen()
                "Settings" -> SettingsScreen()
            }
        }
    }
}
