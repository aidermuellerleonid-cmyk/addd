package com.lernki.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
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
import com.lernki.app.data.remote.MathTask
import com.lernki.app.ui.components.ErrorBlock
import com.lernki.app.ui.components.ImagePickerRow
import com.lernki.app.ui.components.LoadingBlock
import com.lernki.app.viewmodel.MathSolverViewModel
import com.lernki.app.viewmodel.ViewModelFactory

@Composable
fun MathSolverScreen(factory: ViewModelFactory) {
    val viewModel: MathSolverViewModel = viewModel(factory = factory)
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Mathe lösen", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Fotografiere eine oder mehrere Aufgaben oder tippe sie ein – inklusive vollständigem Rechenweg.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        OutlinedTextField(
            value = state.inputText,
            onValueChange = viewModel::onTextChanged,
            label = { Text("Aufgabe eintippen (optional)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            minLines = 2
        )

        ImagePickerRow(
            images = state.images,
            onImagesAdded = viewModel::addImages,
            onImageRemoved = { }
        )

        Button(
            onClick = viewModel::solve,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = !state.isLoading
        ) {
            Text("Aufgabe(n) lösen")
        }

        if (state.isLoading) {
            LoadingBlock("Aufgabe wird erkannt und Schritt für Schritt gelöst …")
        }

        state.errorMessage?.let { ErrorBlock(it) }

        state.tasks.forEach { task ->
            Column(modifier = Modifier.padding(top = 16.dp)) {
                MathTaskCard(
                    task = task,
                    expanded = state.expandedTaskNumber == task.taskNumber,
                    onToggle = { viewModel.toggleExpanded(task.taskNumber) },
                    onSave = { viewModel.saveTaskToHistory(task) }
                )
            }
        }
    }
}

@Composable
private fun MathTaskCard(
    task: MathTask,
    expanded: Boolean,
    onToggle: () -> Unit,
    onSave: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() },
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Aufgabe ${task.taskNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (task.confidenceIsLow) {
                    Icon(
                        Icons.Filled.WarningAmber,
                        contentDescription = "Unsicher erkannt",
                        tint = MaterialTheme.colorScheme.error
                    )
                } else {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = "Erkannt",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                task.recognizedProblem,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    task.note?.let {
                        ErrorBlock(it)
                    }
                    task.steps.forEach { step ->
                        Text(
                            step,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                    Text(
                        "Ergebnis: ${task.finalAnswer}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Row(modifier = Modifier.padding(top = 8.dp)) {
                        IconButton(onClick = onSave) {
                            Icon(Icons.Filled.Save, contentDescription = "Im Verlauf speichern")
                        }
                    }
                }
            }
        }
    }
}
