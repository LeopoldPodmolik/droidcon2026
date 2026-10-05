package com.example.nbagameday.ui.summary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nbagameday.data.remote.LeagueSummaryResponse
import com.example.nbagameday.ui.ErrorScreen

@Composable
fun SummaryScreen(
    viewModel: SummaryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is SummaryUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is SummaryUiState.Success -> {
            SummaryContent(state.data)
        }
        is SummaryUiState.Error -> {
            ErrorScreen(message = state.message, onRetry = { viewModel.loadSummary() })
        }
    }
}

@Composable
fun SummaryContent(data: LeagueSummaryResponse) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "${data.leagueFullName} (${data.sportsLeague})",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Season: ${data.season} - ${data.seasonType}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (data.featuredPlayer != null) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Featured Player",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = data.featuredPlayer.name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "${data.featuredPlayer.position} | ${data.featuredPlayer.teamName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        data.featuredPlayer.stats.forEach { (stat, value) ->
                            Text(
                                text = "$stat: $value",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        if (data.teams.isNotEmpty()) {
            item {
                Text(
                    text = "Teams",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(data.teams) { team ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "${team.city} ${team.name}", fontWeight = FontWeight.Medium)
                        Text(text = "${team.conference} | ${team.division}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
