package com.example.forged.presentation.history

import com.example.forged.domain.model.Session

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data class Success(val sessions: List<Session>) : HistoryUiState
    data class Error(val message: String) : HistoryUiState
}
