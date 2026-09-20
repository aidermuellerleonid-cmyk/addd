package com.lernki.app.data.repository

import com.lernki.app.data.local.HistoryCategory
import com.lernki.app.data.local.HistoryDao
import com.lernki.app.data.local.HistoryEntity
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val dao: HistoryDao) {

    fun observeAll(): Flow<List<HistoryEntity>> = dao.observeAll()

    fun search(query: String): Flow<List<HistoryEntity>> =
        if (query.isBlank()) dao.observeAll() else dao.search(query)

    fun observeByCategory(category: HistoryCategory): Flow<List<HistoryEntity>> =
        dao.observeByCategory(category)

    suspend fun save(
        category: HistoryCategory,
        title: String,
        content: String
    ): Long = dao.insert(HistoryEntity(category = category, title = title, content = content))

    suspend fun update(item: HistoryEntity) = dao.update(item)

    suspend fun delete(item: HistoryEntity) = dao.delete(item)

    suspend fun getById(id: Long): HistoryEntity? = dao.getById(id)
}
