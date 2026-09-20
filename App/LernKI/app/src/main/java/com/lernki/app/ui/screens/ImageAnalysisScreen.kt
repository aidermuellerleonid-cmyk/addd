package com.lernki.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lernki.app.ui.components.ErrorBlock
import com.lernki.app.ui.components.ImagePickerRow
import com.lernki.app.ui.components.LoadingBlock
import com.lernki.app.viewmodel.ImageAnalysisViewModel
import com.lernki.app.viewmodel.ViewModelFactory

/**
 * Abschnitt 10: nach dem Hochladen erkennt die App, was auf dem Bild zu
 * sehen ist, und der Nutzer waehlt danach, was damit passieren soll.
 */
@Composable
fun ImageAnalysisScreen(
    factory: ViewModelFactory,
    onGoToSummarize: () -> Unit,
    onGoToMathSolver: () -> Unit,
    onGoToLearningMode: () -> Unit,
    onGoToChat: () -> Unit
) {
    val viewModel: ImageAnalysisViewModel = viewModel(factory = factory)
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Bild analysieren", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Lade ein Bild hoch – die App erkennt automatisch, worum es sich handelt.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        ImagePickerRow(
            images = state.image?.let { listOf(it) } ?: emptyList(),
            onImagesAdded = { list -> list.firstOrNull()?.let(viewModel::onImageSelected) },
            onImageRemoved = { viewModel.reset() }
        )

        state.image?.let { bmp ->
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(top = 12.dp)
            )
        }

        if (state.isAnalyzing) {
            LoadingBlock("Bildinhalt wird erkannt …")
        }

        state.errorMessage?.let { ErrorBlock(it) }

        if (state.confidenceIsLow) {
            ErrorBlock("Der Inhalt konnte nicht eindeutig erkannt werden. Bitte lade ein schärferes Bild hoch.")
        }

        state.detectedType?.let { type ->
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Erkannt als: ${readableType(type)}", style = MaterialTheme.typography.titleMedium)
                    state.shortDescription?.let {
                        Text(it, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }

            Text(
                "Was möchtest du damit machen?",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
            )

            val isMath = type.equals("MATH_TASK", ignoreCase = true)

            Column {
                Button(onClick = onGoToSummarize, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Text("Zusammenfassen")
                }
                if (isMath) {
                    Button(onClick = onGoToMathSolver, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        Text("Aufgabe lösen")
                    }
                }
                Button(onClick = onGoToChat, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Text("Erklären / Mit der KI chatten")
                }
                Button(onClick = onGoToLearningMode, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Text("Lernzettel erstellen")
                }
                Button(onClick = onGoToLearningMode, modifier = Modifier.fillMaxWidth()) {
                    Text("Quiz erstellen")
                }
            }
        }
    }
}

private fun readableType(type: String): String = when (type.uppercase()) {
    "TEXT" -> "Normaler Text"
    "MATH_TASK" -> "Matheaufgabe"
    "DIAGRAM" -> "Diagramm"
    "TABLE" -> "Tabelle"
    "WORKSHEET" -> "Arbeitsblatt"
    "DRAWING" -> "Zeichnung"
    "FORMULA" -> "Formel"
    else -> "Unbekannt"
}
