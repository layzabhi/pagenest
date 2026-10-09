package com.pagenest.pdf.domain.manager

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import com.pagenest.pdf.domain.model.PdfDocument
import com.pagenest.pdf.domain.model.ScanMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FolderAccessManager(private val context: Context) {

    suspend fun scanFolder(
        treeUri: Uri,
        scanMode: ScanMode
    ): List<PdfDocument> = withContext(Dispatchers.IO) {
        val results = mutableListOf<PdfDocument>()
        try {
            val rootDocId = DocumentsContract.getTreeDocumentId(treeUri)
            scanDocumentId(treeUri, rootDocId, scanMode, "", results)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        results
    }

    private fun scanDocumentId(
        treeUri: Uri,
        documentId: String,
        scanMode: ScanMode,
        currentPath: String,
        results: MutableList<PdfDocument>
    ) {
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, documentId)
        val projection = arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_MIME_TYPE,
            DocumentsContract.Document.COLUMN_SIZE,
            DocumentsContract.Document.COLUMN_LAST_MODIFIED
        )

        val cursor = context.contentResolver.query(childrenUri, projection, null, null, null)
        cursor?.use {
            val idCol = it.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
            val nameCol = it.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
            val mimeCol = it.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_MIME_TYPE)
            val sizeCol = it.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_SIZE)
            val modifiedCol = it.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_LAST_MODIFIED)

            while (it.moveToNext()) {
                val childId = it.getString(idCol)
                val displayName = it.getString(nameCol) ?: "Untitled.pdf"
                val mimeType = it.getString(mimeCol) ?: ""
                val size = if (!it.isNull(sizeCol)) it.getLong(sizeCol) else 0L
                val modified = if (!it.isNull(modifiedCol)) it.getLong(modifiedCol) else System.currentTimeMillis()

                if (mimeType == DocumentsContract.Document.MIME_TYPE_DIR) {
                    if (scanMode == ScanMode.RECURSIVE) {
                        val subPath = if (currentPath.isEmpty()) displayName else "$currentPath/$displayName"
                        scanDocumentId(treeUri, childId, scanMode, subPath, results)
                    }
                } else if (mimeType.equals("application/pdf", ignoreCase = true) || displayName.endsWith(".pdf", ignoreCase = true)) {
                    val docUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, childId)
                    results.add(
                        PdfDocument(
                            uriString = docUri.toString(),
                            displayName = displayName,
                            sizeBytes = size,
                            lastModified = modified,
                            folderUriString = treeUri.toString(),
                            relativePath = if (currentPath.isEmpty()) displayName else "$currentPath/$displayName"
                        )
                    )
                }
            }
        }
    }

    fun persistFolderPermission(uri: Uri) {
        try {
            val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            context.contentResolver.takePersistableUriPermission(uri, takeFlags)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
