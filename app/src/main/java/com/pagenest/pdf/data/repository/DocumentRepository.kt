package com.pagenest.pdf.data.repository

import com.pagenest.pdf.data.local.dao.DocumentDao
import com.pagenest.pdf.data.local.entity.DocumentEntity
import com.pagenest.pdf.domain.model.PdfDocument
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DocumentRepository(private val documentDao: DocumentDao) {

    fun getAllDocuments(): Flow<List<PdfDocument>> {
        return documentDao.getAllDocuments().map { list ->
            list.map { it.toDomain() }
        }
    }

    fun getDocumentsByFolder(folderUri: String): Flow<List<PdfDocument>> {
        return documentDao.getDocumentsByFolder(folderUri).map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun getDocumentByUri(uri: String): PdfDocument? {
        return documentDao.getDocumentByUri(uri)?.toDomain()
    }

    suspend fun saveDocuments(documents: List<PdfDocument>) {
        documentDao.insertDocuments(documents.map { DocumentEntity.fromDomain(it) })
    }

    suspend fun clearFolderDocuments(folderUri: String) {
        documentDao.deleteByFolder(folderUri)
    }

    suspend fun clearAll() {
        documentDao.clearAll()
    }
}
