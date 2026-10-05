package com.example.nbagameday.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.nbagameday.ui.schedule.ScheduleScreen
import com.example.nbagameday.ui.standings.StandingsScreen
import com.example.nbagameday.ui.summary.SummaryScreen

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Summary : Screen("summary", "Summary", Icons.Filled.Home)
    object Schedule : Screen("schedule", "Schedule", Icons.Filled.DateRange)
    object Standings : Screen("standings", "Standings", Icons.Filled.List)
}

val bottomNavItems = listOf(
    Screen.Summary,
    Screen.Schedule,
    Screen.Standings
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.Summary.route, Modifier.padding(innerPadding)) {
            composable(Screen.Summary.route) { SummaryScreen() }
            composable(Screen.Schedule.route) { ScheduleScreen() }
            composable(Screen.Standings.route) { StandingsScreen() }
        }
    }
}
