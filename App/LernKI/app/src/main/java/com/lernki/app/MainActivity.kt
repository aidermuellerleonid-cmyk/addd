package com.lernki.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lernki.app.nav.Destination
import com.lernki.app.ui.screens.ChatScreen
import com.lernki.app.ui.screens.HistoryScreen
import com.lernki.app.ui.screens.HomeScreen
import com.lernki.app.ui.screens.ImageAnalysisScreen
import com.lernki.app.ui.screens.LearningModeScreen
import com.lernki.app.ui.screens.MathSolverScreen
import com.lernki.app.ui.screens.SummarizeScreen
import com.lernki.app.ui.theme.LernKiTheme
import com.lernki.app.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val container = (application as LernKiApplication).container
        val viewModelFactory = ViewModelFactory(container)

        setContent {
            LernKiTheme {
                LernKiApp(viewModelFactory)
            }
        }
    }
}

private data class BottomItem(val destination: Destination, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun LernKiApp(factory: ViewModelFactory) {
    val navController = rememberNavController()

    val bottomItems = listOf(
        BottomItem(Destination.Home, "Start", Icons.Filled.Home),
        BottomItem(Destination.Chat, "Chat", Icons.Filled.Chat),
        BottomItem(Destination.History, "Verlauf", Icons.Filled.History)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomItems.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any { it.route == item.destination.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Destination.Home.route) {
                HomeScreen(
                    onOpenImageAnalysis = { navController.navigate(Destination.ImageAnalysis.route) },
                    onOpenSummarize = { navController.navigate(Destination.Summarize.route) },
                    onOpenMathSolver = { navController.navigate(Destination.MathSolver.route) },
                    onOpenLearningMode = { navController.navigate(Destination.LearningMode.route) },
                    onOpenChat = { navController.navigate(Destination.Chat.route) }
                )
            }
            composable(Destination.Summarize.route) { SummarizeScreen(factory) }
            composable(Destination.MathSolver.route) { MathSolverScreen(factory) }
            composable(Destination.LearningMode.route) { LearningModeScreen(factory) }
            composable(Destination.Chat.route) { ChatScreen(factory) }
            composable(Destination.History.route) { HistoryScreen(factory) }
            composable(Destination.ImageAnalysis.route) {
                ImageAnalysisScreen(
                    factory = factory,
                    onGoToSummarize = { navController.navigate(Destination.Summarize.route) },
                    onGoToMathSolver = { navController.navigate(Destination.MathSolver.route) },
                    onGoToLearningMode = { navController.navigate(Destination.LearningMode.route) },
                    onGoToChat = { navController.navigate(Destination.Chat.route) }
                )
            }
        }
    }
}
