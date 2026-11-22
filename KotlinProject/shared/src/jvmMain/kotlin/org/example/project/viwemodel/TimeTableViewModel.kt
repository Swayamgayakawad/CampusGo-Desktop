
package org.example.project.viwemodel

import androidx.compose.runtime.mutableStateListOf

class TimeTableViewModel {

    val subjects = mutableStateListOf<String>()

    fun addSub(subject: String) {
        if (subject.isNotBlank()) {
            subjects.add(subject)
        }
    }

    fun removeSub(subject: String) {
        subjects.remove(subject)
    }
}
