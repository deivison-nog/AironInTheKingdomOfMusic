# Airon no Reino da Música 🎵

An Android game written in **Kotlin** where the hero **Airon** must defeat musical enemies across 8 epic regions by correctly answering music theory challenges on a virtual piano keyboard.

## Package name
`com.info85.aironmusic`

## Gameplay overview
- Tap piano keys to answer challenges before the timer runs out.
- Correct answers deal damage to the enemy; wrong answers and timeouts damage Airon.
- Defeat the required number of enemies to conquer a region.

### Challenge types
| Mode | Description |
|------|-------------|
| **Single note** | Tap the displayed note |
| **Sequence** | Tap a sequence of 3–4 notes in order |
| **Scale** | Play a complete major, natural-minor, or harmonic-minor scale |
| **Chord** | Tap all notes of a triad (any order) |

### Regions (8 worlds)
1. 🌲 Floresta das Notas — natural notes, single mode
2. 🌋 Vulcão das Alterações — sharps, single + sequence
3. 🏰 Castelo Cromático — all 12 chromatic notes
4. 🌊 Oceano das Escalas Maiores — major scales
5. 🌙 Templo Lunar — natural & harmonic minor scales
6. ⚡ Torre da Harmonia — chords
7. 🌀 Portal Arcano — mixed challenges
8. 👑 Palácio Real — master level, all types, tight timer

## Project structure
```
app/src/main/
├── AndroidManifest.xml
├── java/com/info85/aironmusic/
│   ├── MainActivity.kt          ← Home screen
│   ├── MapSelectActivity.kt     ← Region selection (RecyclerView)
│   ├── GameActivity.kt          ← Main game loop
│   ├── model/
│   │   ├── ChallengeType.kt
│   │   ├── Challenge.kt
│   │   ├── Scale.kt
│   │   ├── ChordDef.kt
│   │   ├── Region.kt
│   │   ├── GamePhase.kt
│   │   ├── GameState.kt
│   │   └── GameData.kt          ← All region/challenge data + generator
│   ├── ui/
│   │   ├── PianoKeyboardView.kt ← Custom View (white+black keys, touch)
│   │   └── GameViewModel.kt     ← Game engine (timer, HP, score, combo)
│   └── util/
│       └── SoundManager.kt      ← AudioTrack sine-wave tone generator
└── res/
    ├── layout/   activity_main, activity_map_select, activity_game, item_region
    ├── values/   strings, colors, themes
    └── drawable/ card_background, ic_launcher_foreground
```

## Building

### Requirements
- Android Studio Hedgehog (2023.1.1) or later **or** Gradle 8.4 + JDK 17
- Android SDK 34

### Steps
```bash
# Generate the Gradle wrapper jar (first time only, requires Gradle installed)
gradle wrapper --gradle-version=8.4

# Build a debug APK
./gradlew assembleDebug
```

The generated APK is at `app/build/outputs/apk/debug/app-debug.apk`.

> **Android Studio**: Open the project root folder. Android Studio will handle the Gradle wrapper setup automatically.

## Min SDK
API 24 (Android 7.0 Nougat) and above.
