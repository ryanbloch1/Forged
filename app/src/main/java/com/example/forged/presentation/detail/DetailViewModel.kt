package com.example.forged.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forged.domain.model.Session
import com.example.forged.domain.usecase.DeleteSessionUseCase
import com.example.forged.domain.usecase.GetSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Ready(val session: Session) : DetailUiState
    data object Missing : DetailUiState
}

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getSession: GetSessionUseCase,
    private val deleteSession: DeleteSessionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun load(sessionId: String) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            val session = getSession(sessionId)
            _uiState.value = if (session != null) {
                DetailUiState.Ready(session)
            } else {
                DetailUiState.Missing
            }
        }
    }

    fun delete(onDone: () -> Unit) {
        val session = (_uiState.value as? DetailUiState.Ready)?.session ?: return
        viewModelScope.launch {
            deleteSession(session.id)
            onDone()
        }
    }
}
