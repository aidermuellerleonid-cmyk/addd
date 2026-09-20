package com.lernki.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class HomeFeature(val emoji: String, val title: String, val onClick: () -> Unit)

@Composable
fun HomeScreen(
    onOpenImageAnalysis: () -> Unit,
    onOpenSummarize: () -> Unit,
    onOpenMathSolver: () -> Unit,
    onOpenLearningMode: () -> Unit,
    onOpenChat: () -> Unit
) {
    val features = listOf(
        HomeFeature("📷", "Bild analysieren", onOpenImageAnalysis),
        HomeFeature("📝", "Text zusammenfassen", onOpenSummarize),
        HomeFeature("🧮", "Mathe lösen", onOpenMathSolver),
        HomeFeature("📚", "Lernzettel erstellen") { onOpenLearningMode() },
        HomeFeature("🧠", "Quiz erstellen") { onOpenLearningMode() },
        HomeFeature("💬", "KI-Chat", onOpenChat)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("LernKI", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Deine KI-Unterstützung zum Lernen",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )
        Text(
            "KI-Lernen",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(features) { feature ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.1f)
                        .clickable { feature.onClick() },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(feature.emoji, style = MaterialTheme.typography.headlineMedium)
                        Text(
                            feature.title,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
