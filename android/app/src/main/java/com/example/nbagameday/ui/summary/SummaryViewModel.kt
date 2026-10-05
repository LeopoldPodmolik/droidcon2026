package com.example.nbagameday.ui.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nbagameday.data.remote.LeagueSummaryResponse
import com.example.nbagameday.data.remote.NbaApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SummaryUiState {
    object Loading : SummaryUiState()
    data class Success(val data: LeagueSummaryResponse) : SummaryUiState()
    data class Error(val message: String) : SummaryUiState()
}

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val apiService: NbaApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<SummaryUiState>(SummaryUiState.Loading)
    val uiState: StateFlow<SummaryUiState> = _uiState.asStateFlow()

    init {
        loadSummary()
    }

    fun loadSummary() {
        viewModelScope.launch {
            _uiState.value = SummaryUiState.Loading
            try {
                val response = apiService.getLeagueSummary()
                if (response.status == "success" && response.data != null) {
                    _uiState.value = SummaryUiState.Success(response.data)
                } else {
                    _uiState.value = SummaryUiState.Error(response.error?.message ?: "Unknown error")
                }
            } catch (e: Exception) {
                _uiState.value = SummaryUiState.Error(e.message ?: "Network error")
            }
        }
    }
}
