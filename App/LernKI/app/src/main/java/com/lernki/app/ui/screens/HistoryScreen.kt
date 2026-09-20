package com.lernki.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lernki.app.viewmodel.HistoryViewModel
import com.lernki.app.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(factory: ViewModelFactory) {
    val viewModel: HistoryViewModel = viewModel(factory = factory)
    val query by viewModel.query.collectAsState()
    val items by viewModel.items.collectAsState()
    val dateFormat = remember_formatter()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Verlauf", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = query,
            onValueChange = viewModel::onQueryChanged,
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            placeholder = { Text("Verlauf durchsuchen …") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        )

        if (items.isEmpty()) {
            Text(
                "Noch keine gespeicherten Ergebnisse.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        LazyColumn {
            items(items, key = { it.id }) { entry ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(entry.title, fontWeight = FontWeight.Bold)
                            Text(
                                entry.category.name.lowercase().replaceFirstChar { it.uppercase() } +
                                    " · " + dateFormat.format(Date(entry.createdAtEpochMillis)),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        IconButton(onClick = { viewModel.delete(entry) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Löschen")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun remember_formatter(): SimpleDateFormat =
    androidx.compose.runtime.remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY) }
