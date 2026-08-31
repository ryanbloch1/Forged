package com.example.forged.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forged.domain.model.Session
import com.example.forged.domain.usecase.ObserveSessionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    observeSessions: ObserveSessionsUseCase,
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> = observeSessions()
        .map<List<Session>, HistoryUiState> { sessions ->
            HistoryUiState.Success(sessions.sortedByDescending { it.date })
        }
        .onStart { emit(HistoryUiState.Loading) }
        .catch { throwable ->
            emit(HistoryUiState.Error(throwable.message ?: "Failed to load sessions"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = HistoryUiState.Loading,
        )
}
