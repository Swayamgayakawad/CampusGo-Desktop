package org.example.project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.viwemodel.TimeTableViewModel

// -----------------------------------------------------------------------------
// MODELS
// -----------------------------------------------------------------------------

data class CellKey(
    val day: String,
    val time: String
)

data class ScheduleSlot(
    val id: String,
    val label: String,
    val isBreak: Boolean = false
)

// -----------------------------------------------------------------------------
// TIME HELPERS
// Input is 24-hour HH:mm. Display is 12-hour with AM/PM.
// -----------------------------------------------------------------------------

fun parseTime(value: String): Int? {
    val parts = value.trim().split(":")
    if (parts.size != 2) return null

    val hour = parts[0].toIntOrNull() ?: return null
    val minute = parts[1].toIntOrNull() ?: return null

    if (hour !in 0..23 || minute !in 0..59) return null
    return hour * 60 + minute
}

fun formatTime(totalMinutes: Int): String {
    val normalized = ((totalMinutes % 1440) + 1440) % 1440
    val hour24 = normalized / 60
    val minute = normalized % 60
    val period = if (hour24 < 12) "AM" else "PM"
    val hour12 = when (hour24 % 12) {
        0 -> 12
        else -> hour24 % 12
    }
    return "%d:%02d %s".format(hour12, minute, period)
}

fun createSchedule(
    startTime: Int,
    classDuration: Int,
    breakAfter1: Int,
    breakAfter2: Int,
    breakDuration1: Int,
    breakDuration2: Int,
    classesPerDay: Int
): List<ScheduleSlot> {
    val slots = mutableListOf<ScheduleSlot>()
    var currentTime = startTime

    for (classNumber in 1..classesPerDay) {
        val classEnd = currentTime + classDuration

        slots.add(
            ScheduleSlot(
                id = "class_$classNumber",
                label = "${formatTime(currentTime)} – ${formatTime(classEnd)}"
            )
        )
        currentTime = classEnd

        val breakDuration = when (classNumber) {
            breakAfter1 -> breakDuration1
            breakAfter2 -> breakDuration2
            else -> null
        }

        if (breakDuration != null) {
            val breakEnd = currentTime + breakDuration
            slots.add(
                ScheduleSlot(
                    id = "break_$classNumber",
                    label = "${formatTime(currentTime)} – ${formatTime(breakEnd)}",
                    isBreak = true
                )
            )
            currentTime = breakEnd
        }
    }

    return slots
}

// -----------------------------------------------------------------------------
// MAIN SCREEN
// -----------------------------------------------------------------------------

@Composable
fun TimetableScreen() {
    val timeTable = remember { TimeTableViewModel() }

    val days = remember {
        listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    }

    val assignments = remember { mutableStateMapOf<CellKey, String>() }
    val cellBounds = remember { mutableStateMapOf<CellKey, Rect>() }

    var draggedSubject by remember { mutableStateOf<String?>(null) }
    var sourceCell by remember { mutableStateOf<CellKey?>(null) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }

    // Schedule configuration
    var showScheduleSettings by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf("09:00") }
    var classDuration by remember { mutableStateOf("50") }
    var breakAfter1 by remember { mutableStateOf("2") }
    var breakAfter2 by remember { mutableStateOf("4") }
    var breakDuration1 by remember { mutableStateOf("15") }
    var breakDuration2 by remember { mutableStateOf("30") }
    var classesPerDay by remember { mutableStateOf("6") }
    var settingsError by remember { mutableStateOf("") }

    var scheduleSlots by remember {
        mutableStateOf(
            createSchedule(
                startTime = 9 * 60,
                classDuration = 50,
                breakAfter1 = 2,
                breakAfter2 = 4,
                breakDuration1 = 15,
                breakDuration2 = 30,
                classesPerDay = 6
            )
        )
    }

    val onDragStart: (String, CellKey?) -> Unit = { subject, source ->
        draggedSubject = subject
        sourceCell = source
    }

    val onDragPosition: (Offset) -> Unit = { position ->
        dragPosition = position
    }

    val onDragEnd: () -> Unit = {
        val subject = draggedSubject
        if (subject != null) {
            val destination = cellBounds.entries
                .firstOrNull { (_, bounds) -> bounds.contains(dragPosition) }
                ?.key

            if (destination != null && !assignments.containsKey(destination)) {
                sourceCell?.let(assignments::remove)
                assignments[destination] = subject
            }
        }

        draggedSubject = null
        sourceCell = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(18.dp)
    ) {
        ScreenHeader(
            subjectCount = timeTable.subjects.size,
            classCount = scheduleSlots.count { !it.isBreak }
        )

        Spacer(Modifier.height(14.dp))

        ScheduleSettingsCard(
            expanded = showScheduleSettings,
            onExpandedChange = { showScheduleSettings = !showScheduleSettings },
            startTime = startTime,
            onStartTimeChange = { startTime = it },
            classDuration = classDuration,
            onClassDurationChange = { classDuration = it },
            breakAfter1 = breakAfter1,
            onBreakAfter1Change = { breakAfter1 = it },
            breakAfter2 = breakAfter2,
            onBreakAfter2Change = { breakAfter2 = it },
            breakDuration1 = breakDuration1,
            onBreakDuration1Change = { breakDuration1 = it },
            breakDuration2 = breakDuration2,
            onBreakDuration2Change = { breakDuration2 = it },
            classesPerDay = classesPerDay,
            onClassesPerDayChange = { classesPerDay = it },
            error = settingsError,
            onApply = {
                val start = parseTime(startTime)
                val duration = classDuration.toIntOrNull()
                val after1 = breakAfter1.toIntOrNull()
                val after2 = breakAfter2.toIntOrNull()
                val length1 = breakDuration1.toIntOrNull()
                val length2 = breakDuration2.toIntOrNull()
                val count = classesPerDay.toIntOrNull()

                val valid = start != null &&
                    duration != null && duration in 1..240 &&
                    after1 != null && after2 != null &&
                    count != null && count in 2..12 &&
                    after1 in 1 until count &&
                    after2 in 1 until count &&
                    after1 != after2 &&
                    length1 != null && length1 in 0..120 &&
                    length2 != null && length2 in 0..120 &&
                    start + (count * duration) + length1 + length2 <= 1440

                if (!valid) {
                    settingsError = "Check the start time, class count, break positions and durations."
                } else {
                    scheduleSlots = createSchedule(
                        startTime = start!!,
                        classDuration = duration!!,
                        breakAfter1 = after1!!,
                        breakAfter2 = after2!!,
                        breakDuration1 = length1!!,
                        breakDuration2 = length2!!,
                        classesPerDay = count!!
                    )

                    // Keep assignments for class numbers that still exist.
                    val validSlotIds = scheduleSlots
                        .filterNot { it.isBreak }
                        .map { it.id }
                        .toSet()
                    assignments.keys.toList().forEach { key ->
                        if (key.time !in validSlotIds) assignments.remove(key)
                    }
                    cellBounds.clear()
                    settingsError = ""
                    showScheduleSettings = false
                }
            }
        )

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1.05f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                SubjectPanel(
                    timeTable = timeTable,
                    assignments = assignments,
                    onDragStart = onDragStart,
                    onDragPosition = onDragPosition,
                    onDragEnd = onDragEnd
                )
            }

            Card(
                modifier = Modifier
                    .weight(3.4f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                TimetablePanel(
                    days = days,
                    scheduleSlots = scheduleSlots,
                    assignments = assignments,
                    cellBounds = cellBounds,
                    onDragStart = onDragStart,
                    onDragPosition = onDragPosition,
                    onDragEnd = onDragEnd
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// HEADER
// -----------------------------------------------------------------------------

@Composable
private fun ScreenHeader(
    subjectCount: Int,
    classCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = "Timetable Planner",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Build and organise your weekly college schedule",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoPill(label = "$subjectCount subjects")
            InfoPill(label = "$classCount periods/day")
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "CAMPUSGO STAFF",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun InfoPill(label: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// -----------------------------------------------------------------------------
// COLLAPSIBLE SCHEDULE SETTINGS
// -----------------------------------------------------------------------------

@Composable
private fun ScheduleSettingsCard(
    expanded: Boolean,
    onExpandedChange: () -> Unit,
    startTime: String,
    onStartTimeChange: (String) -> Unit,
    classDuration: String,
    onClassDurationChange: (String) -> Unit,
    breakAfter1: String,
    onBreakAfter1Change: (String) -> Unit,
    breakAfter2: String,
    onBreakAfter2Change: (String) -> Unit,
    breakDuration1: String,
    onBreakDuration1Change: (String) -> Unit,
    breakDuration2: String,
    onBreakDuration2Change: (String) -> Unit,
    classesPerDay: String,
    onClassesPerDayChange: (String) -> Unit,
    error: String,
    onApply: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onExpandedChange)
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Schedule configuration",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Set college hours, periods and two breaks",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess
                    else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Hide settings" else "Show settings",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            if (expanded) {
                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SettingField(
                        title = "College starts (24h)",
                        value = startTime,
                        onValueChange = onStartTimeChange,
                        modifier = Modifier.weight(1f)
                    )
                    SettingField(
                        title = "Class duration (min)",
                        value = classDuration,
                        onValueChange = onClassDurationChange,
                        modifier = Modifier.weight(1f)
                    )
                    SettingField(
                        title = "Classes per day",
                        value = classesPerDay,
                        onValueChange = onClassesPerDayChange,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(9.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SettingField(
                        title = "Break 1 after class",
                        value = breakAfter1,
                        onValueChange = onBreakAfter1Change,
                        modifier = Modifier.weight(1f)
                    )
                    SettingField(
                        title = "Break 1 duration (min)",
                        value = breakDuration1,
                        onValueChange = onBreakDuration1Change,
                        modifier = Modifier.weight(1f)
                    )
                    SettingField(
                        title = "Break 2 after class",
                        value = breakAfter2,
                        onValueChange = onBreakAfter2Change,
                        modifier = Modifier.weight(1f)
                    )
                    SettingField(
                        title = "Break 2 duration (min)",
                        value = breakDuration2,
                        onValueChange = onBreakDuration2Change,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (error.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Existing assignments are kept for periods that still exist.",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(onClick = onApply) {
                        Text("Apply schedule")
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingField(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(5.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(11.dp),
            textStyle = MaterialTheme.typography.bodyMedium
        )
    }
}

// -----------------------------------------------------------------------------
// SUBJECT PANEL
// -----------------------------------------------------------------------------

@Composable
private fun SubjectPanel(
    timeTable: TimeTableViewModel,
    assignments: MutableMap<CellKey, String>,
    onDragStart: (String, CellKey?) -> Unit,
    onDragPosition: (Offset) -> Unit,
    onDragEnd: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Subjects",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Add subjects, then drag them into a free period.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(14.dp))

        SubjectEditor(tableViewModel = timeTable)

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Available subjects",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            InfoPill(label = "${timeTable.subjects.size}")
        }

        Spacer(Modifier.height(8.dp))

        if (timeTable.subjects.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "No subjects yet",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Add your first subject above.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                items(
                    items = timeTable.subjects,
                    key = { it }
                ) { subject ->
                    DraggableSubject(
                        name = subject,
                        onDragStart = { onDragStart(subject, null) },
                        onDragPosition = onDragPosition,
                        onDragEnd = onDragEnd,
                        onDelete = {
                            timeTable.removeSub(subject)
                            assignments.entries
                                .filter { it.value == subject }
                                .map { it.key }
                                .forEach(assignments::remove)
                        }
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// TIMETABLE PANEL
// -----------------------------------------------------------------------------

@Composable
private fun TimetablePanel(
    days: List<String>,
    scheduleSlots: List<ScheduleSlot>,
    assignments: MutableMap<CellKey, String>,
    cellBounds: MutableMap<CellKey, Rect>,
    onDragStart: (String, CellKey?) -> Unit,
    onDragPosition: (Offset) -> Unit,
    onDragEnd: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Weekly timetable",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Monday to Saturday  •  Drag subjects to assign periods",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            InfoPill(label = "${scheduleSlots.count { !it.isBreak }} periods")
        }

        Spacer(Modifier.height(14.dp))

        // One shared horizontal scroll keeps the day headings and all rows aligned.
        val horizontalState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(horizontalState)
        ) {
            DayHeader(days = days)

            Spacer(Modifier.height(7.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .width(115.dp + (days.size * 158).dp),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                items(
                    items = scheduleSlots,
                    key = { it.id }
                ) { slot ->
                    if (slot.isBreak) {
                        BreakRow(slot = slot, daysCount = days.size)
                    } else {
                        TimetableRow(
                            slot = slot,
                            days = days,
                            assignments = assignments,
                            cellBounds = cellBounds,
                            onDragStart = onDragStart,
                            onDragPosition = onDragPosition,
                            onDragEnd = onDragEnd
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayHeader(days: List<String>) {
    Row(
        modifier = Modifier.width(115.dp + (days.size * 158).dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(115.dp)
                .height(42.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "TIME",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }

        days.forEach { day ->
            Surface(
                modifier = Modifier
                    .width(158.dp)
                    .padding(horizontal = 4.dp)
                    .height(42.dp),
                shape = RoundedCornerShape(11.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun TimetableRow(
    slot: ScheduleSlot,
    days: List<String>,
    assignments: MutableMap<CellKey, String>,
    cellBounds: MutableMap<CellKey, Rect>,
    onDragStart: (String, CellKey?) -> Unit,
    onDragPosition: (Offset) -> Unit,
    onDragEnd: () -> Unit
) {
    Row(
        modifier = Modifier.width(115.dp + (days.size * 158).dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimeLabel(
            time = slot.label,
            modifier = Modifier.width(115.dp)
        )

        days.forEach { day ->
            val key = CellKey(day = day, time = slot.id)
            TimetableCell(
                key = key,
                assignments = assignments,
                cellBounds = cellBounds,
                onDragStart = onDragStart,
                onDragPosition = onDragPosition,
                onDragEnd = onDragEnd,
                onDelete = { assignments.remove(it) }
            )
        }
    }
}

@Composable
private fun TimetableCell(
    key: CellKey,
    assignments: MutableMap<CellKey, String>,
    cellBounds: MutableMap<CellKey, Rect>,
    onDragStart: (String, CellKey?) -> Unit,
    onDragPosition: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDelete: (CellKey) -> Unit
) {
    DisposableEffect(key) {
        onDispose { cellBounds.remove(key) }
    }

    val subject = assignments[key]

    Box(
        modifier = Modifier
            .width(158.dp)
            .height(82.dp)
            .padding(horizontal = 4.dp, vertical = 3.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(13.dp)
            )
            .onGloballyPositioned { coordinates ->
                cellBounds[key] = coordinates.boundsInRoot()
            },
        contentAlignment = Alignment.Center
    ) {
        if (subject != null) {
            DraggableSubject(
                name = subject,
                modifier = Modifier.fillMaxSize(),
                compact = true,
                onDragStart = { onDragStart(subject, key) },
                onDragPosition = onDragPosition,
                onDragEnd = onDragEnd,
                onDelete = { onDelete(key) }
            )
        } else {
            Text(
                text = "+ Drop subject",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TimeLabel(
    time: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.height(82.dp).padding(end = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = time,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun BreakRow(
    slot: ScheduleSlot,
    daysCount: Int
) {
    Surface(
        modifier = Modifier
            .width(115.dp + (daysCount * 158).dp)
            .padding(vertical = 2.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFFF3E0)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "☕",
                fontSize = 16.sp
            )
            Spacer(Modifier.width(9.dp))
            Text(
                text = "BREAK  •  ${slot.label}",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF9A5B00),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// -----------------------------------------------------------------------------
// DRAGGABLE SUBJECT CARD
// -----------------------------------------------------------------------------

@Composable
private fun DraggableSubject(
    name: String,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    onDragStart: () -> Unit,
    onDragPosition: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDelete: () -> Unit = {}
) {
    var coordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    val latestOnDragStart by rememberUpdatedState(onDragStart)
    val latestOnDragPosition by rememberUpdatedState(onDragPosition)
    val latestOnDragEnd by rememberUpdatedState(onDragEnd)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(if (compact) 2.dp else 3.dp)
            .onGloballyPositioned { coordinates = it }
            .pointerInput(name) {
                detectDragGestures(
                    onDragStart = { position ->
                        latestOnDragStart()
                        coordinates?.let { coords ->
                            latestOnDragPosition(coords.localToRoot(position))
                        }
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        coordinates?.let { coords ->
                            latestOnDragPosition(coords.localToRoot(change.position))
                        }
                    },
                    onDragEnd = latestOnDragEnd,
                    onDragCancel = latestOnDragEnd
                )
            },
        shape = RoundedCornerShape(11.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (compact) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.secondaryContainer
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = if (compact) 5.dp else 9.dp,
                    vertical = if (compact) 3.dp else 5.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                modifier = Modifier.weight(1f),
                style = if (compact) {
                    MaterialTheme.typography.labelSmall
                } else {
                    MaterialTheme.typography.bodyMedium
                },
                fontWeight = FontWeight.SemiBold,
                color = if (compact) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSecondaryContainer
                },
                maxLines = if (compact) 2 else 1,
                overflow = TextOverflow.Ellipsis
            )

            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .width(if (compact) 32.dp else 40.dp)
                    .height(if (compact) 36.dp else 40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete $name",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// SUBJECT EDITOR - ENTER KEY AND BUTTON BOTH ADD THE SUBJECT
// -----------------------------------------------------------------------------

@Composable
private fun SubjectEditor(
    tableViewModel: TimeTableViewModel,
    modifier: Modifier = Modifier
) {
    var subjectName by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    fun addSubject() {
        val newName = subjectName.trim()
        when {
            newName.isEmpty() -> {
                error = "Enter a subject name."
            }
            tableViewModel.subjects.any { it.equals(newName, ignoreCase = true) } -> {
                error = "This subject already exists."
            }
            else -> {
                tableViewModel.addSub(newName)
                subjectName = ""
                error = ""
            }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.padding(11.dp)) {
            OutlinedTextField(
                value = subjectName,
                onValueChange = {
                    subjectName = it
                    if (error.isNotEmpty()) error = ""
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onPreviewKeyEvent { event ->
                        if (event.key == Key.Enter && event.type == KeyEventType.KeyUp) {
                            addSubject()
                            true
                        } else {
                            false
                        }
                    },
                label = { Text("Subject name") },
                placeholder = { Text("e.g. Data Structures") },
                singleLine = true,
                shape = RoundedCornerShape(11.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { addSubject() })
            )

            if (error.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { addSubject() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(11.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Add subject")
            }
        }
    }
}
