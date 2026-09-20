package com.lernki.app.di

import android.content.Context
import com.lernki.app.BuildConfig
import com.lernki.app.data.local.AppDatabase
import com.lernki.app.data.remote.AiApiService
import com.lernki.app.data.repository.AiRepository
import com.lernki.app.data.repository.BackendAiRepository
import com.lernki.app.data.repository.HistoryRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Zentraler, sehr einfacher Dependency-Container (kein Hilt/Koin noetig).
 * Baut die Verbindung Frontend (UI/ViewModels) <-> Backend/KI sauber ab
 * (siehe Abschnitt 12).
 */
class AppContainer(context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS) // KI-Antworten koennen etwas dauern
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BASIC
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }
        )
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BACKEND_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val aiApiService: AiApiService = retrofit.create(AiApiService::class.java)

    // Austauschbar: hier koennte eine andere AiRepository-Implementierung
    // eingesetzt werden (z.B. direkte Anbindung an ein anderes Modell),
    // ohne dass ViewModels/UI angepasst werden muessen.
    val aiRepository: AiRepository = BackendAiRepository(aiApiService)

    private val database = AppDatabase.getInstance(context)
    val historyRepository = HistoryRepository(database.historyDao())
}
