package com.lernki.app.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lernki.app.data.local.HistoryCategory
import com.lernki.app.data.remote.Flashcard
import com.lernki.app.data.remote.ImagePayload
import com.lernki.app.data.remote.QuizQuestion
import com.lernki.app.data.repository.AiRepository
import com.lernki.app.data.repository.HistoryRepository
import com.lernki.app.util.ImageUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class LearningMaterialType(val label: String, val apiValue: String, val historyCategory: HistoryCategory) {
    SUMMARY("Zusammenfassung", "SUMMARY", HistoryCategory.ZUSAMMENFASSUNG),
    LERNZETTEL("Lernzettel", "LERNZETTEL", HistoryCategory.LERNZETTEL),
    FLASHCARDS("Karteikarten", "FLASHCARDS", HistoryCategory.KARTEIKARTEN),
    QUIZ("Quiz", "QUIZ", HistoryCategory.QUIZ),
    KEY_TERMS("Wichtige Begriffe", "KEY_TERMS", HistoryCategory.LERNZETTEL),
    QUESTIONS("Fragen zum Text", "QUESTIONS", HistoryCategory.LERNZETTEL),
    EXAM_SIM("Prüfungssimulation", "EXAM_SIM", HistoryCategory.QUIZ)
}

data class LearningUiState(
    val inputText: String = "",
    val images: List<Bitmap> = emptyList(),
    val selectedType: LearningMaterialType = LearningMaterialType.LERNZETTEL,
    val isLoading: Boolean = false,
    val resultTitle: String? = null,
    val resultMarkdown: String? = null,
    val flashcards: List<Flashcard>? = null,
    val quiz: List<QuizQuestion>? = null,
    val errorMessage: String? = null
)

class LearningViewModel(
    private val aiRepository: AiRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LearningUiState())
    val uiState: StateFlow<LearningUiState> = _uiState

    fun onTextChanged(text: String) = _uiState.update { it.copy(inputText = text) }

    fun addImages(bitmaps: List<Bitmap>) = _uiState.update { it.copy(images = it.images + bitmaps) }

    fun onTypeSelected(type: LearningMaterialType) = _uiState.update { it.copy(selectedType = type) }

    fun generate() {
        val state = _uiState.value
        if (state.inputText.isBlank() && state.images.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Bitte Text oder Bild fuer den Lernmodus angeben.") }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val payloads: List<ImagePayload> = state.images.map { ImageUtils.bitmapToPayload(it) }
            val result = aiRepository.generateLearningMaterial(
                text = state.inputText.ifBlank { null },
                images = payloads,
                materialType = state.selectedType.apiValue
            )
            result.onSuccess { response ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        resultTitle = response.title,
                        resultMarkdown = response.markdown,
                        flashcards = response.flashcards,
                        quiz = response.quiz
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Lernmaterial konnte nicht erstellt werden: ${e.message ?: "Unbekannter Fehler"}"
                    )
                }
            }
        }
    }

    fun saveToHistory() {
        val state = _uiState.value
        val title = state.resultTitle ?: return
        val content = state.resultMarkdown
            ?: state.flashcards?.joinToString("\n") { "${it.front} -> ${it.back}" }
            ?: state.quiz?.joinToString("\n") { it.question }
            ?: return
        viewModelScope.launch {
            historyRepository.save(state.selectedType.historyCategory, title, content)
        }
    }

    fun reset() {
        _uiState.value = LearningUiState()
    }
}
