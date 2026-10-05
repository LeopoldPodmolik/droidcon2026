package com.example.nbagameday.ui.standings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nbagameday.data.remote.StandingsResponse
import com.example.nbagameday.ui.ErrorScreen

@Composable
fun StandingsScreen(
    viewModel: StandingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is StandingsUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is StandingsUiState.Success -> {
            StandingsContent(state.data)
        }
        is StandingsUiState.Error -> {
            ErrorScreen(message = state.message, onRetry = { viewModel.loadStandings() })
        }
    }
}

@Composable
fun StandingsContent(data: StandingsResponse) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "${data.conference} Conference Standings",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Rank", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Text("Team", modifier = Modifier.weight(3f), fontWeight = FontWeight.Bold)
            Text("W-L", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold)
            Text("GB", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
        }
        Divider()

        LazyColumn {
            items(data.standings) { team ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(team.rank.toString(), modifier = Modifier.weight(1f))
                    Text(team.teamName, modifier = Modifier.weight(3f))
                    Text("${team.wins}-${team.losses}", modifier = Modifier.weight(1.5f))
                    Text(team.gamesBack.toString(), modifier = Modifier.weight(1f))
                }
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            }
        }
    }
}
