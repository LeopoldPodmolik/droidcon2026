package com.example.nbagameday.ui.schedule

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
import com.example.nbagameday.data.remote.Game
import com.example.nbagameday.data.remote.GamesResponse
import com.example.nbagameday.ui.ErrorScreen

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Schedule",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = { 
                val currentState = uiState as? ScheduleUiState.Success
                viewModel.loadGames(isLive = currentState?.isLive != true) 
            }) {
                val buttonText = if ((uiState as? ScheduleUiState.Success)?.isLive == true) {
                    "Switch to Scheduled"
                } else {
                    "Switch to Live (v2)"
                }
                Text(buttonText)
            }
        }

        when (val state = uiState) {
            is ScheduleUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ScheduleUiState.Success -> {
                ScheduleContent(state.data)
            }
            is ScheduleUiState.Error -> {
                ErrorScreen(message = state.message, onRetry = { viewModel.loadGames(false) })
            }
        }
    }
}

@Composable
fun ScheduleContent(data: GamesResponse) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (data.games.isEmpty()) {
            item {
                Text("No games scheduled.")
            }
        }
        
        items(data.games) { game ->
            GameCard(game)
        }
    }
}

@Composable
fun GameCard(game: Game) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (game.status == "live") "LIVE - Period ${game.period ?: ""} ${game.gameClock ?: ""}" 
                           else "Scheduled: ${game.startTime}",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (game.status == "live") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (game.status == "live") {
                    Badge(containerColor = MaterialTheme.colorScheme.error) {
                        Text("LIVE")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(game.awayTeam.name, fontWeight = FontWeight.SemiBold)
                    if (game.status == "live" && game.awayTeam.score != null) {
                        Text(text = game.awayTeam.score.toString(), style = MaterialTheme.typography.titleLarge)
                    }
                }
                Text(" @ ")
                Column(horizontalAlignment = Alignment.End) {
                    Text(game.homeTeam.name, fontWeight = FontWeight.SemiBold)
                    if (game.status == "live" && game.homeTeam.score != null) {
                        Text(text = game.homeTeam.score.toString(), style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
            
            if (game.status == "live" && !game.lastPlayDescription.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Last Play: ${game.lastPlayDescription}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
