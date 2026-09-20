package com.lernki.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lernki.app.di.AppContainer

class ViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            SummarizeViewModel::class.java -> SummarizeViewModel(container.aiRepository, container.historyRepository)
            MathSolverViewModel::class.java -> MathSolverViewModel(container.aiRepository, container.historyRepository)
            LearningViewModel::class.java -> LearningViewModel(container.aiRepository, container.historyRepository)
            ChatViewModel::class.java -> ChatViewModel(container.aiRepository)
            HistoryViewModel::class.java -> HistoryViewModel(container.historyRepository)
            ImageAnalysisViewModel::class.java -> ImageAnalysisViewModel(container.aiRepository)
            else -> throw IllegalArgumentException("Unbekanntes ViewModel: $modelClass")
        } as T
    }
}
