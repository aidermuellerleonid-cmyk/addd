package com.lernki.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
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
import com.lernki.app.ui.components.ErrorBlock
import com.lernki.app.ui.components.ImagePickerRow
import com.lernki.app.ui.components.LoadingBlock
import com.lernki.app.ui.components.ResultCard
import com.lernki.app.viewmodel.LearningMaterialType
import com.lernki.app.viewmodel.LearningViewModel
import com.lernki.app.viewmodel.ViewModelFactory

@Composable
fun LearningModeScreen(factory: ViewModelFactory) {
    val viewModel: LearningViewModel = viewModel(factory = factory)
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Lernmodus", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Lade Text oder ein Bild hoch und lass daraus automatisch Lernmaterial erstellen.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        OutlinedTextField(
            value = state.inputText,
            onValueChange = viewModel::onTextChanged,
            label = { Text("Text (optional)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            minLines = 3
        )

        ImagePickerRow(
            images = state.images,
            onImagesAdded = viewModel::addImages,
            onImageRemoved = { }
        )

        Text(
            "Was soll erstellt werden?",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(LearningMaterialType.values().toList()) { type ->
                FilterChip(
                    selected = state.selectedType == type,
                    onClick = { viewModel.onTypeSelected(type) },
                    label = { Text(type.label) }
                )
            }
        }

        Button(
            onClick = viewModel::generate,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = !state.isLoading
        ) {
            Text("Erstellen")
        }

        if (state.isLoading) {
            LoadingBlock("Lernmaterial wird erstellt …")
        }

        state.errorMessage?.let { ErrorBlock(it) }

        state.resultMarkdown?.let { markdown ->
            Column(modifier = Modifier.padding(top = 16.dp)) {
                ResultCard(
                    title = state.resultTitle ?: type_label(state),
                    content = markdown,
                    onSave = viewModel::saveToHistory
                )
            }
        }

        state.flashcards?.let { cards ->
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Text("Karteikarten", style = MaterialTheme.typography.titleMedium)
                cards.forEach { card ->
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(card.front, fontWeight = FontWeight.Bold)
                            Text(card.back, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
                Button(onClick = viewModel::saveToHistory, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Im Verlauf speichern")
                }
            }
        }

        state.quiz?.let { questions ->
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Text("Quiz", style = MaterialTheme.typography.titleMedium)
                questions.forEachIndexed { index, q ->
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("${index + 1}. ${q.question}", fontWeight = FontWeight.Bold)
                            q.options.forEachIndexed { i, option ->
                                val prefix = if (i == q.correctOptionIndex) "✅" else "▫️"
                                Text("$prefix $option", modifier = Modifier.padding(top = 4.dp))
                            }
                            Text(
                                q.explanation,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }
                }
                Button(onClick = viewModel::saveToHistory, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Im Verlauf speichern")
                }
            }
        }
    }
}

private fun type_label(state: com.lernki.app.viewmodel.LearningUiState): String = state.selectedType.label
