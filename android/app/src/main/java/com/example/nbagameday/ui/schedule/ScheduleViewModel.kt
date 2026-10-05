package com.example.nbagameday.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nbagameday.data.remote.GamesResponse
import com.example.nbagameday.data.remote.NbaApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ScheduleUiState {
    object Loading : ScheduleUiState()
    data class Success(val data: GamesResponse, val isLive: Boolean) : ScheduleUiState()
    data class Error(val message: String) : ScheduleUiState()
}

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val apiService: NbaApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScheduleUiState>(ScheduleUiState.Loading)
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    init {
        loadGames(isLive = false)
    }

    fun loadGames(isLive: Boolean) {
        viewModelScope.launch {
            _uiState.value = ScheduleUiState.Loading
            try {
                val response = if (isLive) {
                    apiService.getLiveScores()
                } else {
                    apiService.getGames()
                }

                if (response.status == "success" && response.data != null) {
                    _uiState.value = ScheduleUiState.Success(response.data, isLive)
                } else {
                    _uiState.value = ScheduleUiState.Error(response.error?.message ?: "Unknown error")
                }
            } catch (e: Exception) {
                _uiState.value = ScheduleUiState.Error(e.message ?: "Network error")
            }
        }
    }
}
