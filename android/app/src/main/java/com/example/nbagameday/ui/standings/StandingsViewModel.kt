package com.example.nbagameday.ui.standings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nbagameday.data.remote.NbaApiService
import com.example.nbagameday.data.remote.StandingsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class StandingsUiState {
    object Loading : StandingsUiState()
    data class Success(val data: StandingsResponse) : StandingsUiState()
    data class Error(val message: String) : StandingsUiState()
}

@HiltViewModel
class StandingsViewModel @Inject constructor(
    private val apiService: NbaApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<StandingsUiState>(StandingsUiState.Loading)
    val uiState: StateFlow<StandingsUiState> = _uiState.asStateFlow()

    init {
        loadStandings()
    }

    fun loadStandings() {
        viewModelScope.launch {
            _uiState.value = StandingsUiState.Loading
            try {
                val response = apiService.getStandings()
                if (response.status == "success" && response.data != null) {
                    _uiState.value = StandingsUiState.Success(response.data)
                } else {
                    _uiState.value = StandingsUiState.Error(response.error?.message ?: "Unknown error")
                }
            } catch (e: Exception) {
                _uiState.value = StandingsUiState.Error(e.message ?: "Network error")
            }
        }
    }
}
