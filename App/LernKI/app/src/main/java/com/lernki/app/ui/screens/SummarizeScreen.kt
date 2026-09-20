package com.lernki.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lernki.app.ui.components.ErrorBlock
import com.lernki.app.ui.components.ImagePickerRow
import com.lernki.app.ui.components.LoadingBlock
import com.lernki.app.ui.components.ResultCard
import com.lernki.app.ui.components.StyleChipRow
import com.lernki.app.viewmodel.SummarizeViewModel
import com.lernki.app.viewmodel.ViewModelFactory

@Composable
fun SummarizeScreen(factory: ViewModelFactory) {
    val viewModel: SummarizeViewModel = viewModel(factory = factory)
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Zusammenfassen", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Text eingeben oder Bild(er) mit Text hochladen – die KI erkennt den Inhalt automatisch.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        OutlinedTextField(
            value = state.inputText,
            onValueChange = viewModel::onTextChanged,
            label = { Text("Text (optional, wenn du Bilder hochlädst)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            minLines = 3
        )

        ImagePickerRow(
            images = state.images,
            onImagesAdded = viewModel::addImages,
            onImageRemoved = viewModel::removeImage
        )

        Text(
            "Art der Zusammenfassung",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        StyleChipRow(
            options = viewModel.availableStyles,
            selected = state.style,
            onSelected = viewModel::onStyleSelected
        )

        Button(
            onClick = viewModel::summarize,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = !state.isLoading
        ) {
            Text("Zusammenfassen")
        }

        if (state.isLoading) {
            LoadingBlock("Text wird analysiert und zusammengefasst …")
        }

        state.errorMessage?.let { ErrorBlock(it) }

        if (state.wasUnclear) {
            ErrorBlock("Der Text konnte nicht eindeutig erkannt werden. Bitte lade ein schärferes Bild hoch.")
        }

        state.resultMarkdown?.let { markdown ->
            Column(modifier = Modifier.padding(top = 16.dp)) {
                ResultCard(
                    title = state.resultTitle ?: "Zusammenfassung",
                    content = markdown,
                    onEdit = null,
                    onSave = viewModel::saveToHistory
                )
            }
        }
    }
}
