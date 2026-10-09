package com.pagenest.pdf.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pagenest.pdf.domain.model.ReadingProgress

@Entity(tableName = "reading_progress")
data class ReadingProgressEntity(
    @PrimaryKey val documentUri: String,
    val displayName: String,
    val lastViewedPage: Int,
    val totalPages: Int,
    val lastOpenedTimestamp: Long
) {
    fun toDomain(): ReadingProgress = ReadingProgress(
        documentUri = documentUri,
        displayName = displayName,
        lastViewedPage = lastViewedPage,
        totalPages = totalPages,
        lastOpenedTimestamp = lastOpenedTimestamp
    )

    companion object {
        fun fromDomain(progress: ReadingProgress): ReadingProgressEntity = ReadingProgressEntity(
            documentUri = progress.documentUri,
            displayName = progress.displayName,
            lastViewedPage = progress.lastViewedPage,
            totalPages = progress.totalPages,
            lastOpenedTimestamp = progress.lastOpenedTimestamp
        )
    }
}
