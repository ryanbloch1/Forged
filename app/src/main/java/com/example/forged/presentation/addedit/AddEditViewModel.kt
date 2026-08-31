package com.example.forged.presentation.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forged.domain.model.BodyPart
import com.example.forged.domain.model.Exercise
import com.example.forged.domain.model.ExerciseEntry
import com.example.forged.domain.model.Session
import com.example.forged.domain.model.SetEntry
import com.example.forged.domain.usecase.GetSessionUseCase
import com.example.forged.domain.usecase.ObserveExercisesUseCase
import com.example.forged.domain.usecase.SaveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

data class DraftSet(
    val id: String = UUID.randomUUID().toString(),
    val reps: String = "",
    val weightKg: String = "",
)

data class DraftExercise(
    val id: String = UUID.randomUUID().toString(),
    val exercise: Exercise,
    val durationMinutes: String = "",
    val sets: List<DraftSet> = listOf(DraftSet()),
)

data class AddEditUiState(
    val sessionId: String = UUID.randomUUID().toString(),
    val isEditing: Boolean = false,
    val date: LocalDate = LocalDate.now(),
    val selectedBodyParts: Set<BodyPart> = emptySet(),
    val exercises: List<DraftExercise> = emptyList(),
    val notes: String = "",
    val durationMinutes: String = "",
    val showExercisePicker: Boolean = false,
)

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val saveSession: SaveSessionUseCase,
    private val getSession: GetSessionUseCase,
    observeExercises: ObserveExercisesUseCase,
) : ViewModel() {

    val availableBodyParts: List<BodyPart> = listOf(
        BodyPart(id = "chest", name = "Chest"),
        BodyPart(id = "back", name = "Back"),
        BodyPart(id = "legs", name = "Legs"),
        BodyPart(id = "shoulders", name = "Shoulders"),
        BodyPart(id = "biceps", name = "Biceps"),
        BodyPart(id = "triceps", name = "Triceps"),
    )

    private val _uiState = MutableStateFlow(AddEditUiState())
    val uiState: StateFlow<AddEditUiState> = _uiState.asStateFlow()

    val catalog: StateFlow<List<Exercise>> = observeExercises()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun prepare(sessionId: String?) {
        if (sessionId.isNullOrBlank()) {
            _uiState.value = AddEditUiState()
            return
        }
        viewModelScope.launch {
            val session = getSession(sessionId) ?: return@launch
            _uiState.value = session.toUiState(availableBodyParts)
        }
    }

    fun onBodyPartToggled(bodyPart: BodyPart) {
        _uiState.update { state ->
            val selected = if (bodyPart in state.selectedBodyParts) {
                state.selectedBodyParts - bodyPart
            } else {
                state.selectedBodyParts + bodyPart
            }
            state.copy(selectedBodyParts = selected)
        }
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(date = date) }
    }

    fun onNotesChanged(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun onDurationChanged(value: String) {
        _uiState.update { it.copy(durationMinutes = value.filter { ch -> ch.isDigit() }) }
    }

    fun showExercisePicker(show: Boolean) {
        _uiState.update { it.copy(showExercisePicker = show) }
    }

    fun addExercise(exercise: Exercise) {
        _uiState.update { state ->
            state.copy(
                exercises = state.exercises + DraftExercise(exercise = exercise),
                showExercisePicker = false,
            )
        }
    }

    fun removeExercise(entryId: String) {
        _uiState.update { state ->
            state.copy(exercises = state.exercises.filterNot { it.id == entryId })
        }
    }

    fun addSet(entryId: String) {
        _uiState.update { state ->
            state.copy(
                exercises = state.exercises.map { entry ->
                    if (entry.id == entryId) entry.copy(sets = entry.sets + DraftSet()) else entry
                },
            )
        }
    }

    fun removeSet(entryId: String, setId: String) {
        _uiState.update { state ->
            state.copy(
                exercises = state.exercises.map { entry ->
                    if (entry.id != entryId) entry
                    else entry.copy(sets = entry.sets.filterNot { it.id == setId }.ifEmpty { listOf(DraftSet()) })
                },
            )
        }
    }

    fun updateSet(entryId: String, setId: String, reps: String? = null, weightKg: String? = null) {
        _uiState.update { state ->
            state.copy(
                exercises = state.exercises.map { entry ->
                    if (entry.id != entryId) entry
                    else entry.copy(
                        sets = entry.sets.map { set ->
                            if (set.id != setId) set
                            else set.copy(
                                reps = reps ?: set.reps,
                                weightKg = weightKg ?: set.weightKg,
                            )
                        },
                    )
                },
            )
        }
    }

    fun updateExerciseDuration(entryId: String, duration: String) {
        _uiState.update { state ->
            state.copy(
                exercises = state.exercises.map { entry ->
                    if (entry.id == entryId) {
                        entry.copy(durationMinutes = duration.filter { it.isDigit() })
                    } else {
                        entry
                    }
                },
            )
        }
    }

    fun onSave(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            val exerciseDurations = state.exercises.mapNotNull { it.durationMinutes.toIntOrNull() }
            val duration = state.durationMinutes.toIntOrNull()
                ?: exerciseDurations.sum().takeIf { it > 0 }
                ?: 0

            val session = Session(
                id = state.sessionId,
                date = state.date,
                startTime = null,
                endTime = null,
                durationMinutes = duration,
                notes = state.notes.ifBlank { null },
                bodyParts = state.selectedBodyParts.map { it.name }.sorted(),
                exercises = state.exercises.mapIndexed { index, draft ->
                    ExerciseEntry(
                        id = draft.id,
                        exercise = draft.exercise,
                        orderIndex = index,
                        durationMinutes = draft.durationMinutes.toIntOrNull(),
                        sets = draft.sets.mapIndexed { setIndex, set ->
                            SetEntry(
                                id = set.id,
                                setNumber = setIndex + 1,
                                reps = set.reps.toIntOrNull() ?: 0,
                                weightKg = set.weightKg.toDoubleOrNull(),
                            )
                        },
                    )
                },
            )
            saveSession(session)
            onSuccess()
        }
    }
}

private fun Session.toUiState(catalog: List<BodyPart>): AddEditUiState {
    val selected = bodyParts.mapNotNull { name ->
        catalog.firstOrNull { it.name.equals(name, ignoreCase = true) }
    }.toSet()

    return AddEditUiState(
        sessionId = id,
        isEditing = true,
        date = date,
        selectedBodyParts = selected,
        notes = notes.orEmpty(),
        durationMinutes = durationMinutes.takeIf { it > 0 }?.toString().orEmpty(),
        exercises = exercises.map { entry ->
            DraftExercise(
                id = entry.id,
                exercise = entry.exercise,
                durationMinutes = entry.durationMinutes?.toString().orEmpty(),
                sets = entry.sets.map { set ->
                    DraftSet(
                        id = set.id,
                        reps = set.reps.takeIf { it > 0 }?.toString().orEmpty(),
                        weightKg = set.weightKg?.toString().orEmpty(),
                    )
                }.ifEmpty { listOf(DraftSet()) },
            )
        },
    )
}
