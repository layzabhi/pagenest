package com.pagenest.pdf.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pagenest.pdf.domain.model.PdfDocument

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey val uriString: String,
    val displayName: String,
    val sizeBytes: Long,
    val lastModified: Long,
    val pageCount: Int?,
    val folderUriString: String?,
    val relativePath: String?
) {
    fun toDomain(): PdfDocument = PdfDocument(
        uriString = uriString,
        displayName = displayName,
        sizeBytes = sizeBytes,
        lastModified = lastModified,
        pageCount = pageCount,
        folderUriString = folderUriString,
        relativePath = relativePath
    )

    companion object {
        fun fromDomain(doc: PdfDocument): DocumentEntity = DocumentEntity(
            uriString = doc.uriString,
            displayName = doc.displayName,
            sizeBytes = doc.sizeBytes,
            lastModified = doc.lastModified,
            pageCount = doc.pageCount,
            folderUriString = doc.folderUriString,
            relativePath = doc.relativePath
        )
    }
}
