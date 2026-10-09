package com.pagenest.pdf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class DocumentFilter(val label: String) {
    ALL("All"),
    IN_PROGRESS("In Progress"),
    UNREAD("Unread"),
    COMPLETED("Completed")
}

@Composable
fun FilterChipGroup(
    selectedFilter: DocumentFilter,
    onFilterSelected: (DocumentFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DocumentFilter.entries.forEach { filter ->
            val isSelected = filter == selectedFilter
            val bgColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
            val textColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            val borderModifier = if (isSelected) Modifier else Modifier.border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                CircleShape
            )

            Box(
                modifier = Modifier
                    .height(32.dp)
                    .clip(CircleShape)
                    .then(borderModifier)
                    .background(bgColor)
                    .clickable { onFilterSelected(filter) }
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = filter.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = textColor
                )
            }
        }
    }
}
