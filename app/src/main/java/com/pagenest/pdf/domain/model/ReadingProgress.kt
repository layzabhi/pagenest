package com.pagenest.pdf.domain.model

data class ReadingProgress(
    val documentUri: String,
    val displayName: String,
    val lastViewedPage: Int,
    val totalPages: Int,
    val lastOpenedTimestamp: Long
) {
    val progressPercent: Int
        get() {
            if (totalPages <= 0) return 0
            return ((lastViewedPage.toDouble() / totalPages.toDouble()) * 100)
                .toInt()
                .coerceIn(0, 100)
        }

    val pageProgressText: String
        get() = if (totalPages > 0) "p. $lastViewedPage / $totalPages" else "p. $lastViewedPage"

    val relativeTimeText: String
        get() {
            val now = System.currentTimeMillis()
            val diffMs = now - lastOpenedTimestamp
            if (diffMs < 0) return "Just now"
            val seconds = diffMs / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24

            return when {
                days > 30 -> "Over a month ago"
                days > 1 -> "$days days ago"
                days == 1L -> "Yesterday"
                hours > 1 -> "$hours hours ago"
                hours == 1L -> "1 hour ago"
                minutes > 1 -> "$minutes mins ago"
                else -> "Just now"
            }
        }
}
