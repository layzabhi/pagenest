package com.pagenest.pdf.domain.model

data class PdfDocument(
    val uriString: String,
    val displayName: String,
    val sizeBytes: Long,
    val lastModified: Long,
    val pageCount: Int? = null,
    val folderUriString: String? = null,
    val relativePath: String? = null
) {
    val formattedSize: String
        get() {
            if (sizeBytes <= 0) return "Unknown size"
            val kb = sizeBytes / 1024.0
            val mb = kb / 1024.0
            val gb = mb / 1024.0
            return when {
                gb >= 1.0 -> String.format("%.2f GB", gb)
                mb >= 1.0 -> String.format("%.1f MB", mb)
                else -> String.format("%.0f KB", kb)
            }
        }
}
