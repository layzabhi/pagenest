package com.pagenest.pdf.data.repository

import com.pagenest.pdf.data.local.dao.ReadingProgressDao
import com.pagenest.pdf.data.local.entity.ReadingProgressEntity
import com.pagenest.pdf.domain.model.ReadingProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReadingProgressRepository(private val readingProgressDao: ReadingProgressDao) {

    fun getAllReadingProgress(): Flow<List<ReadingProgress>> {
        return readingProgressDao.getAllReadingProgress().map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun getProgress(documentUri: String): ReadingProgress? {
        return readingProgressDao.getProgressByUri(documentUri)?.toDomain()
    }

    fun observeProgress(documentUri: String): Flow<ReadingProgress?> {
        return readingProgressDao.observeProgressByUri(documentUri).map { it?.toDomain() }
    }

    suspend fun saveProgress(
        documentUri: String,
        displayName: String,
        lastViewedPage: Int,
        totalPages: Int
    ) {
        val entity = ReadingProgressEntity(
            documentUri = documentUri,
            displayName = displayName,
            lastViewedPage = lastViewedPage,
            totalPages = totalPages,
            lastOpenedTimestamp = System.currentTimeMillis()
        )
        readingProgressDao.insertProgress(entity)
    }

    suspend fun deleteProgress(documentUri: String) {
        readingProgressDao.deleteProgress(documentUri)
    }

    suspend fun clearAllHistory() {
        readingProgressDao.clearAllHistory()
    }
}
