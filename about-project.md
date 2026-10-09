# PageNest PDF

**A modern Android PDF reader with folder-based document management, persistent reading progress, and customizable application themes.**

PageNest PDF is a native Android application designed to provide a clean, efficient, and customizable PDF reading experience. Users can browse PDF files from selected folders, open and read documents, select and copy text, and automatically resume reading from their last saved position.

The application features a document library, reading history, persistent reading progress, light and dark modes, and a custom theme management system.

**Core principle:** Application themes and appearance settings affect only the app interface. PDF pages must retain their original appearance, including text, colors, photographs, diagrams, illustrations, and formatting.

---

## Table of Contents

- [1. Project Overview](#1-project-overview)
- [2. Project Objectives](#2-project-objectives)
- [3. Core Features](#3-core-features)
- [4. PDF Library and Folder Management](#4-pdf-library-and-folder-management)
- [5. PDF Viewer](#5-pdf-viewer)
- [6. Reading History and Persistent Progress](#6-reading-history-and-persistent-progress)
- [7. Dark Mode and Appearance](#7-dark-mode-and-appearance)
- [8. Custom Theme System](#8-custom-theme-system)
- [9. Search and Navigation](#9-search-and-navigation)
- [10. Technical Stack](#10-technical-stack)
- [11. System Architecture](#11-system-architecture)
- [12. Application Navigation](#12-application-navigation)
- [13. Data Storage and Persistence](#13-data-storage-and-persistence)
- [14. Security and Privacy](#14-security-and-privacy)
- [15. Development Roadmap](#15-development-roadmap)
- [16. Functional Requirements](#16-functional-requirements)
- [17. Known Limitations](#17-known-limitations)
- [18. Future Enhancements](#18-future-enhancements)
- [19. Project Status](#19-project-status)

---

## 1. Project Overview

PageNest PDF is a native Android PDF reader intended for users who want a simple way to organize, open, and read documents stored on their devices.

Unlike a basic viewer that only opens individual files, PageNest PDF provides a persistent document library, automatic reading-position restoration, reading history, and user-created interface themes.

The application is organized around four primary sections:

1. **Library:** Browse PDF documents discovered in selected folders.
2. **Reader:** View documents with scrolling, zooming, page navigation, and text selection.
3. **History:** Access previously opened documents and resume reading.
4. **Settings and Themes:** Customize the application interface and manage reading preferences.

The application follows an offline-first approach. Core functionality should work without an internet connection or an online account.

### Project Information

| Property | Description |
|---|---|
| Project name | PageNest PDF |
| Application type | Native Android application |
| Programming language | Kotlin |
| UI framework | Jetpack Compose |
| Build system | Gradle |
| Development environment | Visual Studio Code |
| Document format | PDF |
| Database | Room |
| Preferences | DataStore |
| Storage model | Local storage with user-authorized document access |
| Primary objective | Reliable PDF reading and document management |

---

## 2. Project Objectives

The primary objective is to build a fully functional PDF reader that provides reliable document access and reading continuity without modifying the original documents.

The project aims to:

- Provide folder-based PDF discovery and browsing.
- Support direct-folder and recursive-folder scanning.
- Render PDF documents with their original appearance.
- Support smooth scrolling and zooming.
- Allow text selection and copying where supported.
- Maintain reading history across application restarts.
- Automatically save and restore reading positions.
- Provide light mode, dark mode, and system-default appearance.
- Allow users to create, edit, save, apply, and delete custom themes.
- Store application data locally.
- Minimize unnecessary permissions and background operations.
- Maintain a modular architecture that supports future enhancements.

### Design Principles

**Original document integrity:** PDF content must remain visually unchanged regardless of the application's selected theme.

**Reading continuity:** Users should be able to resume reading without manually finding their previous page.

**Local-first operation:** Core reading functionality should not depend on cloud services.

**User-controlled access:** Users choose which folders the application can access.

**Customizability:** Users can personalize the interface without modifying source code.

**Performance:** The application should handle large PDFs without unnecessarily loading every page into memory.

---

## 3. Core Features

| Feature | Description | Priority |
|---|---|---|
| Folder selection | Select a folder through Android's document picker | Essential |
| Direct-folder scanning | Display PDFs immediately inside the selected folder | Essential |
| Recursive scanning | Include PDFs inside accessible subfolders | Essential |
| PDF library | Display discovered PDF documents | Essential |
| PDF rendering | Preserve original document content and appearance | Essential |
| Vertical scrolling | Scroll through multipage documents | Essential |
| Zoom controls | Pinch-to-zoom, zoom in, and zoom out | Essential |
| Page navigation | Navigate to a specific page | Essential |
| Text selection | Select text from supported PDFs | Essential |
| Copy to clipboard | Copy selected text | Essential |
| Reading history | Maintain previously opened documents | Essential |
| Persistent reading position | Restore the last-viewed page | Essential |
| Light mode | Display a light application interface | Essential |
| Dark mode | Display a dark application interface | Essential |
| System appearance | Follow Android's appearance setting | Essential |
| Custom themes | Create and save personalized themes | Essential |
| Theme editing | Modify existing custom themes | Essential |
| Theme management | Apply, duplicate, and delete custom themes | Essential |
| Sorting | Sort documents by name, date, or size | High |
| List and grid views | Change the library layout | High |
| Reading progress | Display the last page and progress percentage | High |
| Full-screen reading | Hide unnecessary reader controls | High |
| In-document search | Search for text within a PDF | High |

---

## 4. PDF Library and Folder Management

The library is the main entry point into the application.

Users should be able to select a folder and browse the PDFs discovered within it rather than selecting individual documents every time they want to read.

### 4.1 Folder Selection

The application will use Android's Storage Access Framework to allow users to select a folder.

The selected folder should remain accessible across application restarts when persisted permissions allow it.

Users must be able to change the selected folder whenever needed.

### 4.2 Folder Scanning Modes

The application will support two scanning modes.

**Direct-folder mode**

Displays only PDFs located immediately inside the selected folder.

Example:

```text
Documents/
├── Book1.pdf
├── Book2.pdf
├── Notes.pdf
├── Images/
│   └── ScannedDocument.pdf
└── Research/
    └── Paper.pdf
```

The library displays `Book1.pdf`, `Book2.pdf`, and `Notes.pdf`.

**Recursive-folder mode**

Searches the selected folder and its accessible subfolders.

The library displays all five PDF documents in the example above.

Users should be able to switch between these modes in Settings.

### 4.3 Library Information

Each document entry should display available information such as:

- Filename.
- File size.
- Total page count.
- Last-opened date.
- Last-viewed page.
- Reading progress percentage.

Metadata that is unavailable from the underlying document provider should be omitted or handled gracefully.

### 4.4 Sorting and Layout

Users should be able to sort documents by:

- Filename.
- Last modified date, when available.
- Last-opened date.
- File size, when available.
- Reading progress.

The library should support both list and grid layouts.

Grid layouts may display PDF page thumbnails generated by the rendering engine.

### 4.5 Library Refresh

Users should be able to refresh the library to discover new documents and update existing entries.

Refreshing should avoid duplicate records and preserve reading history wherever document identity can be established reliably.

The application should handle inaccessible folders without crashing or unnecessarily deleting saved history.

---

## 5. PDF Viewer

The PDF viewer is the core component of PageNest PDF.

It must support books, academic papers, technical documents, manuals, and PDFs containing images, diagrams, illustrations, and other visual content.

### 5.1 Original Document Appearance

PDF pages must retain their original appearance, including:

- Text and typography.
- Embedded fonts.
- Photographs and images.
- Diagrams and illustrations.
- Original colors.
- Page backgrounds.
- Vector graphics.
- Layout and spacing.
- Page dimensions and orientation.

Changing the application's theme must never recolor or invert PDF content.

### 5.2 Scrolling

The viewer should support smooth vertical scrolling through multipage documents.

It should prioritize visible pages, avoid unnecessary rendering of distant pages, and maintain responsive navigation through long documents.

### 5.3 Zooming

The viewer should support:

- Pinch-to-zoom gestures.
- Zoom-in controls.
- Zoom-out controls.
- Appropriate minimum and maximum zoom levels.
- Maintaining a useful viewport when zooming.

Zooming should not unintentionally navigate to a different page.

### 5.4 Page Navigation

Users should be able to:

- Scroll through pages.
- Jump to a specific page.
- View the current page number.
- View the total page count.
- Resume from the saved page when reopening a document.

Page navigation should validate page numbers and handle invalid input gracefully.

### 5.5 Text Selection and Copying

For PDFs containing extractable text, users should be able to select text directly from the document and copy it to the Android clipboard.

Expected behavior:

1. Select text within the PDF.
2. Display the selection clearly.
3. Copy the selected passage.
4. Paste the copied text into another application.

The implementation should preserve reasonable reading order and spacing wherever supported by the PDF engine.

Text selection should use the capabilities of the chosen PDF rendering library or a compatible text layer.

### 5.6 Scanned PDFs

Scanned PDFs may contain page images rather than actual text.

Text selection and search cannot be guaranteed for such documents without OCR or another suitable text-recognition mechanism.

The initial implementation will prioritize selectable text in text-based PDFs. OCR may be considered as a future enhancement.

### 5.7 Full-Screen Reading

An optional full-screen mode should hide unnecessary interface controls and provide a distraction-free reading experience.

Reader controls should remain accessible when required.

---

## 6. Reading History and Persistent Progress

PageNest PDF must remember where users stopped reading and restore their positions when they reopen documents.

### 6.1 Automatic Position Saving

When a document is opened, the application should retrieve its saved reading position.

- If a saved position exists, resume from that page.
- Otherwise, open the document at the beginning.
- Track the currently visible page.
- Save reading progress periodically.
- Save progress when leaving the reader.
- Restore the last successfully saved position after an application restart.

Reading progress must not rely exclusively on the application closing normally.

### 6.2 Example

Suppose a user opens `MachineLearning.pdf`, reads through page 146 of 420, and closes the application.

The application saves the reading position.

When the same document is opened again, the reader should return to page 146.

### 6.3 History Screen

The history screen should display:

- Document name.
- Last-viewed page.
- Total page count.
- Reading progress percentage.
- Last-opened timestamp.

Users should be able to reopen a document directly from history and resume reading.

They should also be able to remove history entries without deleting the original PDF.

### 6.4 Reading Progress

Reading progress can be calculated as:

```text
Progress (%) = (Last viewed page / Total pages) × 100
```

For example:

```text
Last viewed page: 146
Total pages: 420

Progress = (146 / 420) × 100
         = 34.76%
```

The interface may display the rounded value as `35%`.

### 6.5 Document Identity

Filenames alone must not be used to identify documents because different folders can contain PDFs with identical names.

The application should use document URIs and other available identifiers to distinguish documents.

If a document is moved, renamed, deleted, or becomes inaccessible, the application should handle the situation gracefully.

Where document identity cannot be established reliably, the application must avoid incorrectly merging reading histories.

### 6.6 Efficient Persistence

Reading progress should be saved without generating unnecessary database writes for every scroll event.

The application should debounce frequent page changes and persist progress periodically and when leaving the reader.

---

## 7. Dark Mode and Appearance

PageNest PDF will provide three appearance modes.

### Light Mode

A light application interface with appropriate background colors, text contrast, and accent colors.

### Dark Mode

A dark application interface designed for comfortable navigation in low-light environments.

### System Default

Automatically follows the device's configured appearance.

### Scope of Appearance Changes

Appearance settings affect only the application interface, including:

- Backgrounds.
- Toolbars.
- Navigation elements.
- Buttons.
- Cards.
- Dialogs.
- Settings screens.
- Library metadata.
- Reader controls.

They must not change:

- PDF page colors.
- Document text colors.
- Photographs.
- Diagrams.
- Illustrations.
- Page backgrounds.
- PDF graphics or formatting.

For example, the application may use a black toolbar and dark library while displaying a PDF page with its original white background and black text.

The selected appearance mode must persist across application restarts.

---

## 8. Custom Theme System

The application will include a dedicated Themes section where users can create, save, edit, duplicate, apply, and delete custom themes.

Themes customize the application interface only. There will be no PDF page recoloring, inversion, or document text recoloring.

### 8.1 Built-In Themes

The application may include the following predefined themes:

- Light.
- Dark.
- Midnight.
- AMOLED Black.
- Forest.
- Ocean.
- Lavender.
- Paper.

All built-in themes should work offline.

### 8.2 Theme Creation

Users should be able to create themes by selecting colors and assigning a theme name.

The theme editor should display a live preview that updates when the user changes colors.

### 8.3 Customizable Properties

| Property | Description |
|---|---|
| Theme name | Name displayed in the theme library |
| Background color | Main application background |
| Surface color | Cards, panels, and dialogs |
| Primary color | Main accent color |
| Secondary color | Supporting accent color |
| Primary text color | Main interface text |
| Secondary text color | Supporting text and metadata |
| Toolbar color | Application toolbar background |
| Navigation color | Navigation elements |
| Border color | Separators and component boundaries |

Additional application-interface properties may be introduced as needed.

### 8.4 Theme Configuration Example

```json
{
  "name": "Midnight",
  "background": "#10141C",
  "surface": "#1A2230",
  "primary": "#7C9CFF",
  "secondary": "#9BAAC7",
  "text": "#F2F5FC",
  "secondaryText": "#A8B2C5",
  "toolbar": "#10141C",
  "navigation": "#151B26",
  "border": "#30394A"
}
```

This is an illustrative configuration. The final implementation may use Kotlin data classes and Room entities.

### 8.5 Theme Management

**Create:** Make a new custom theme.

**Apply:** Change the application interface to use a saved theme.

**Edit:** Modify an existing custom theme.

**Duplicate:** Create a copy of a theme for further customization.

**Delete:** Remove a user-created theme.

**Preview:** Review the interface appearance before applying the theme.

**Reset:** Restore the default appearance.

Built-in themes should be protected from accidental deletion.

### 8.6 Theme Persistence

Custom themes must remain available after application restarts.

Theme data should be stored locally, and creating a theme must not require modifying source code or rebuilding the application.

### 8.7 Theme Validation

Before saving a theme, the application should validate:

- Theme name.
- Color formats.
- Required properties.
- Text and background contrast.
- Duplicate names.

The selected theme should update the interface immediately.

---

## 9. Search and Navigation

### 9.1 Library Navigation

Users should be able to browse discovered PDFs, switch between list and grid views, sort documents, and open a PDF with a single tap.

The selected folder and library preferences should be preserved when appropriate.

### 9.2 In-Document Search

The reader should support searching for words and phrases within PDFs containing searchable text.

Expected capabilities include:

- Search input.
- Match counts where supported.
- Navigation to matching results.
- Next and previous match controls.
- Navigation to the page containing a result.
- Appropriate feedback when no results are found.

Search in scanned, image-only PDFs may require OCR and is not guaranteed in the initial version.

### 9.3 Page Navigation

Users should be able to enter a page number and navigate directly to it.

Invalid page numbers should be handled gracefully without crashing the reader.

---

## 10. Technical Stack

The application will use native Android technologies.

| Technology | Purpose |
|---|---|
| Kotlin | Primary programming language |
| Jetpack Compose | Application UI |
| Android SDK | Android platform APIs |
| Storage Access Framework | Folder and document access |
| Dedicated PDF rendering library | PDF parsing, rendering, and viewer functionality |
| Room | Local relational database |
| SQLite | Database engine |
| DataStore | Application preferences |
| Kotlin Coroutines | Asynchronous operations |
| Kotlin Flow | Reactive state and data updates |
| Android Clipboard APIs | Copy selected text |
| Gradle | Build and dependency management |

### PDF Rendering Engine

A dedicated PDF rendering library will be selected based on:

- Rendering quality.
- Text selection and copying.
- Zoom and scrolling behavior.
- Large-document performance.
- Memory usage.
- Android compatibility.
- Maintenance status.
- License and distribution requirements.

Android's built-in `PdfRenderer` is an alternative for page rendering, but it does not provide a complete reader interface or built-in text selection.

The selected engine must meet the application's functional requirements without requiring an unnecessarily complex custom rendering implementation.

### Android Compatibility

The initial target is Android 8.0 or later, subject to the requirements of the selected PDF library.

The final minimum SDK and dependency versions will be determined during implementation.

---

## 11. System Architecture

The application will use a modular architecture that separates the user interface, business logic, PDF operations, and data persistence.

### 11.1 Presentation Layer

Responsible for:

- Library screens.
- PDF viewer screens.
- History screens.
- Settings screens.
- Theme management.
- UI state and user interactions.
- Application navigation.

Jetpack Compose and ViewModels may be used to manage interface state.

### 11.2 Domain and Application Logic

Responsible for:

- Discovering PDF documents.
- Opening documents.
- Tracking the current page.
- Saving reading progress.
- Restoring reading positions.
- Managing history.
- Validating themes.
- Coordinating application settings.

### 11.3 Data Layer

Responsible for:

- Room database operations.
- Document metadata.
- Reading history.
- Reading-position storage.
- Custom theme persistence.
- Application preferences.

### 11.4 PDF and Storage Layer

Responsible for:

- Folder selection.
- Persisted folder permissions.
- Recursive directory traversal.
- PDF discovery.
- Document opening.
- PDF rendering integration.
- Page navigation.
- Text selection integration.
- Document availability checks.

### 11.5 Main Components

| Component | Responsibility |
|---|---|
| `PdfLibraryManager` | PDF discovery and library management |
| `PdfDocumentManager` | Document opening and management |
| `PdfViewerManager` | Viewer operations and rendering coordination |
| `ReadingProgressRepository` | Reading-position persistence |
| `HistoryRepository` | Reading history |
| `ThemeRepository` | Custom theme storage |
| `ThemeManager` | Applying application themes |
| `SettingsRepository` | Application preferences |
| `FolderAccessManager` | Folder permissions and access |

These names describe logical responsibilities and may be adjusted during implementation.

---

## 12. Application Navigation

The application will have four principal destinations.

```text
PageNest PDF
│
├── Library
│   ├── Document List
│   ├── Grid View
│   ├── Folder Selection
│   ├── Scanning Preferences
│   └── PDF Viewer
│       ├── Scrolling and Zoom
│       ├── Page Navigation
│       ├── Text Selection
│       └── In-Document Search
│
├── History
│   ├── Recently Opened Documents
│   ├── Reading Progress
│   └── Resume Reading
│
└── Settings
    ├── Appearance
    │   ├── Light Mode
    │   ├── Dark Mode
    │   └── System Default
    │
    ├── Themes
    │   ├── Available Themes
    │   ├── Create Theme
    │   ├── Edit Theme
    │   ├── Duplicate Theme
    │   └── Delete Theme
    │
    ├── Library Preferences
    │   ├── Recursive Scanning
    │   ├── Default View
    │   └── Sorting
    │
    └── Reading Preferences
        └── Full-Screen Reading
```

Navigation should preserve relevant state. Returning from the reader should restore the previous library or history context rather than unnecessarily resetting the application.

---

## 13. Data Storage and Persistence

The application will store reading history, document metadata, custom themes, and preferences locally.

### 13.1 Room Database

Room will store structured information such as:

- Document identifiers.
- Document URIs.
- Display names.
- Last-viewed pages.
- Total page counts.
- Last-opened timestamps.
- Reading progress.
- Custom theme definitions, where appropriate.

An illustrative Kotlin model is:

```kotlin
data class ReadingProgress(
    val documentUri: String,
    val displayName: String,
    val lastViewedPage: Int,
    val totalPages: Int?,
    val lastOpenedAt: Long
)
```

This represents the intended data model rather than a complete database entity.

The actual implementation should include appropriate primary keys, database annotations, indexes, and migration handling.

### 13.2 DataStore

DataStore will store application preferences, including:

- Appearance mode.
- Active theme identifier.
- Folder scanning mode.
- Preferred library layout.
- Sorting preference.
- Reading settings.

### 13.3 Persistence Requirements

- Reading history must survive application restarts.
- Reading progress must be saved and restored.
- Custom themes must remain available.
- Preferences must persist.
- Database changes must be handled safely.
- Removing a history entry must not delete the source PDF.

Clearing application data or uninstalling the application may remove local history, preferences, and custom themes.

---

## 14. Security and Privacy

PageNest PDF is designed for local document access and offline reading.

### 14.1 Local Data

Reading history, preferences, and custom themes should be stored locally.

The initial implementation should not upload PDF documents or reading history to a server.

### 14.2 Permissions

The application should request only permissions necessary for its functionality.

Users must explicitly authorize folder access.

### 14.3 Clipboard

Selected text should be copied using Android's clipboard APIs.

Clipboard contents must not be silently transmitted elsewhere.

### 14.4 Source Document Integrity

Ordinary reading operations must not modify source PDF files.

Removing a library entry or history record must not delete the underlying document.

### 14.5 Offline Operation

Core functionality must not require an internet connection or online account.

Any future network-dependent feature must clearly document its purpose and privacy implications.

---

## 15. Development Roadmap

Development will proceed in stages, prioritizing the core reader before introducing additional features.

### Phase 1: Project Foundation

- Initialize the Kotlin Android project.
- Configure Jetpack Compose.
- Establish the application architecture.
- Select a suitable PDF rendering library.

**Deliverable:** A functional project foundation.

### Phase 2: Library and Folder Access

- Implement folder selection.
- Persist folder permissions.
- Implement direct-folder scanning.
- Implement recursive scanning.
- Display discovered PDFs.
- Add sorting and layout options.
- Handle inaccessible folders.

**Deliverable:** A working PDF library.

### Phase 3: PDF Viewer

- Integrate the PDF rendering engine.
- Implement page rendering.
- Add vertical scrolling.
- Add pinch-to-zoom and zoom controls.
- Implement page navigation.
- Implement text selection and copying.
- Preserve the original document appearance.

**Deliverable:** A functional PDF viewer.

### Phase 4: Reading History

- Configure Room.
- Store document metadata.
- Implement reading-position persistence.
- Restore the last-viewed page.
- Build the history screen.
- Add progress indicators.
- Handle duplicate filenames and inaccessible documents.

**Deliverable:** Persistent reading history and reading progress.

### Phase 5: Appearance and Custom Themes

- Implement light mode.
- Implement dark mode.
- Implement system-default appearance.
- Build the Themes screen.
- Add theme creation and editing.
- Add theme duplication and deletion.
- Persist custom themes.
- Apply theme changes immediately.

**Deliverable:** A customizable application interface.

### Phase 6: Search and Reader Improvements

- Add in-document search.
- Improve page navigation.
- Add full-screen reading.
- Improve loading and error states.
- Optimize rendering and caching.

**Deliverable:** An improved reading experience.

### Phase 7: Finalization

- Resolve usability issues.
- Improve performance.
- Verify document integrity.
- Review accessibility.
- Prepare the application for distribution.

**Deliverable:** A complete release candidate.

---

## 16. Functional Requirements

### FR-01: Folder Selection

Users must be able to select a directory and access it again when permissions permit.

### FR-02: Folder Scanning

The application must support direct-folder and recursive scanning without creating unnecessary duplicate entries.

### FR-03: PDF Rendering

Valid PDFs must render with their original text, images, colors, diagrams, and formatting.

### FR-04: Zoom and Scrolling

Users must be able to scroll through documents and zoom using gestures and controls.

### FR-05: Text Selection

Users must be able to select and copy text from supported PDFs.

### FR-06: Reading History

Previously opened documents must remain accessible through a persistent history screen.

### FR-07: Reading Position

Reopening a document must restore its last successfully saved reading position.

### FR-08: Independent Progress

Different PDFs, including files with identical filenames, must maintain independent reading progress whenever they can be distinguished reliably.

### FR-09: Appearance

Light mode, dark mode, and system-default appearance must be supported and persisted.

### FR-10: Custom Themes

Users must be able to create, edit, duplicate, apply, and delete custom themes.

### FR-11: Theme Scope

Theme changes must affect only the application interface, never PDF page content.

### FR-12: Offline Functionality

Core reading, history, and theme functionality must work without internet access.

### FR-13: Source Integrity

Opening, scrolling, zooming, copying text, and changing themes must not modify the source PDF.

### FR-14: Error Handling

Invalid PDFs, unavailable files, revoked folder permissions, and invalid navigation inputs must be handled gracefully.

---

## 17. Known Limitations

### Scanned PDFs

Scanned PDFs may contain only page images. Text selection and search may require OCR.

### Folder Permissions

Document access depends on persisted Android permissions and the underlying document provider.

### Document Identity

Moving or renaming a PDF may change its URI. Reading progress can only be restored reliably when the application can establish the document's identity.

### PDF Rendering Library

The final rendering, selection, search, and password-handling capabilities depend on the chosen PDF library.

### Large Documents

Very large PDFs and high-resolution images may require additional memory management and rendering optimization.

### Theme Scope

Custom themes change the application interface only. They do not modify PDF content.

---

## 18. Future Enhancements

Potential enhancements for later versions include:

- OCR for scanned PDFs.
- Named reading bookmarks.
- Notes and annotations.
- PDF table-of-contents navigation.
- Favorites and recently opened documents.
- Advanced library filters.
- Improved document metadata.
- Theme import and export.
- Reading statistics.
- Accessibility improvements.
- Advanced rendering and caching optimization.

These enhancements are optional and should not delay the implementation of the core PDF reader.

---

## 19. Project Status

PageNest PDF is intended to become a fully functional Android PDF reader with folder-based document discovery, persistent reading progress, reading history, and customizable application themes.

The initial release will prioritize:

- Folder-based PDF discovery.
- Direct and recursive scanning.
- Original PDF rendering.
- Scrolling, zooming, and page navigation.
- Text selection and copying.
- Persistent reading history.
- Automatic reading-position restoration.
- Light, dark, and system-default appearance.
- User-created interface themes.
- Offline operation.
- Reliable document access.

The PDF rendering library, package identifier, and project license will be finalized during development.

**The goal is to build a dependable PDF reader that remembers where users stopped reading and allows them to customize the application without changing their documents.**