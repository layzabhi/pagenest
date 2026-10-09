# PageNest PDF — Design System Specification

Extracted from Stitch Project `7713614347770007191`.

## Brand & Style

Modern Utilitarian / Calibrated Material 3 (Material You).
Disciplined minimalism, crisp tonal planar layering, 4dp/8dp baseline grid, soft pill-based interactive elements.

## Color Tokens

### Light Theme (Default)
| Token | Hex | Usage |
|---|---|---|
| Primary | `#4969D8` | Primary actions, FAB, active tabs, progress bars |
| Primary Darker | `#2C4FBE` | Focused / prominent interactive states |
| Primary Container / Fixed | `#DCE1FF` / `#EDF1FF` | Container tints, active pill chips, search focus fill |
| Background / Canvas | `#F8F9FB` / `#F7F8FA` | Neutral non-glare canvas |
| Surface | `#FFFFFF` | Document cards, bottom sheets, toolbars |
| Surface Container Low | `#F2F4F6` | Subtle containers |
| Surface Container | `#EDEEF0` | Default container tone |
| On-Surface (Primary Text) | `#191C1E` / `#202534` | Headings, titles, high-contrast text |
| On-Surface-Variant (Secondary Text) | `#595E6A` / `#747B8C` | File metadata, page counts, secondary labels |
| Outline / Divider | `#E1E2E4` / `#E6E9F0` | Dividers, card strokes, list item lines |
| Error | `#BA1A1A` | Delete dialogs, error banners |

### Dark Theme Variant
| Token | Hex | Usage |
|---|---|---|
| Dark Canvas | `#111318` | Deep obsidian canvas |
| Dark Surface | `#1B1E25` | Elevated cards, sheets, dialogs |
| Dark Primary Accent | `#9BAEFF` | Periwinkle blue |
| Dark Primary Text | `#E2E2EA` | High-contrast soft off-white |
| Dark Secondary Text | `#8E92A0` | Metadata mid-tone |
| Dark Container | `#232A42` | Active chip fill |
| Dark Divider | `#292D38` | Structural borders |

## Typography
- Font Family: **Roboto Flex** / Inter / System Sans-Serif
- Display: 40px Bold / 32px Mobile
- Headline: 28px/24px/20px SemiBold
- Title: 18px/16px/14px Medium-SemiBold
- Body: 16px/14px/12px Regular
- Label: 14px/12px/11px Medium-SemiBold (Badges, Chips, Bottom Nav)

## Visual Components & Metrics
- **Document Card (Grid)**: 16dp rounded corners, 1dp border `#E6E9F0`, aspect ratio cover thumbnail + title + progress bar + 3-dot action button.
- **Document Row (List)**: 72dp item height, 48dp leading thumbnail, title + metadata + progress bar.
- **Bottom Navigation**: 80dp M3 bar, 4 items: Library, History, Themes, Settings. Active state: `#EDF1FF` capsule with `#4969D8` icon.
- **Floating Controls**: Reader top bar (Back, Title, Search, Bookmark, Overflow) and bottom scrubber capsule (Page slider, current/total page indicator, zoom toggles).
- **Theme Editor**: Live interactive preview card, color swatch pickers (primary, background, surface, text, etc.), preset color palettes, save & apply action.
