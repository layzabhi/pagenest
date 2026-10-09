package com.pagenest.pdf.domain.model

enum class AppearanceMode(val title: String) {
    SYSTEM("System Default"),
    LIGHT("Light Mode"),
    DARK("Dark Mode")
}

enum class SortOption(val title: String) {
    NAME_ASC("Name (A-Z)"),
    NAME_DESC("Name (Z-A)"),
    DATE_MODIFIED_DESC("Recently Modified"),
    DATE_OPENED_DESC("Recently Opened"),
    SIZE_DESC("File Size"),
    PROGRESS_DESC("Reading Progress")
}

enum class ViewMode(val title: String) {
    GRID("Grid View"),
    LIST("List View")
}

enum class ScanMode(val title: String, val description: String) {
    DIRECT("Direct Folder", "Only PDFs located directly inside the selected folder"),
    RECURSIVE("Recursive Scanning", "PDFs inside the selected folder and all subfolders")
}
