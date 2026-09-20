package com.lernki.app.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lernki.app.data.local.HistoryCategory
import com.lernki.app.data.remote.ImagePayload
import com.lernki.app.data.remote.MathTask
import com.lernki.app.data.repository.AiRepository
import com.lernki.app.data.repository.HistoryRepository
import com.lernki.app.util.ImageUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MathSolverUiState(
    val inputText: String = "",
    val images: List<Bitmap> = emptyList(),
    val isLoading: Boolean = false,
    val tasks: List<MathTask> = emptyList(),
    val expandedTaskNumber: Int? = null,
    val errorMessage: String? = null
)

class MathSolverViewModel(
    private val aiRepository: AiRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MathSolverUiState())
    val uiState: StateFlow<MathSolverUiState> = _uiState

    fun onTextChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun addImages(bitmaps: List<Bitmap>) {
        _uiState.update { it.copy(images = it.images + bitmaps) }
    }

    fun toggleExpanded(taskNumber: Int) {
        _uiState.update {
            it.copy(expandedTaskNumber = if (it.expandedTaskNumber == taskNumber) null else taskNumber)
        }
    }

    fun solve() {
        val state = _uiState.value
        if (state.inputText.isBlank() && state.images.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Bitte eine Aufgabe eingeben oder fotografieren.") }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null, tasks = emptyList()) }

        viewModelScope.launch {
            val payloads: List<ImagePayload> = state.images.map { ImageUtils.bitmapToPayload(it) }
            val result = aiRepository.solveMath(
                text = state.inputText.ifBlank { null },
                images = payloads
            )
            result.onSuccess { response ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        tasks = response.tasks,
                        expandedTaskNumber = response.tasks.firstOrNull()?.taskNumber
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Die Aufgabe konnte nicht geloest werden: ${e.message ?: "Unbekannter Fehler"}"
                    )
                }
            }
        }
    }

    fun saveTaskToHistory(task: MathTask) {
        viewModelScope.launch {
            val content = buildString {
                appendLine(task.recognizedProblem)
                appendLine()
                task.steps.forEach { appendLine(it) }
                appendLine()
                appendLine("Ergebnis: ${task.finalAnswer}")
            }
            historyRepository.save(
                HistoryCategory.MATHE,
                "Aufgabe ${task.taskNumber}: ${task.recognizedProblem.take(40)}",
                content
            )
        }
    }

    fun reset() {
        _uiState.value = MathSolverUiState()
    }
}
