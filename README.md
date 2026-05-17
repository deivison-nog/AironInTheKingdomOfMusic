# Airon no Reino da Música 🎵

An Android RPG game written in **Kotlin** where the hero **Airon** wakes up in the mystical Kingdom of Music. To return home he must defeat the **Six Great Bardos** and collect the six *Song Fragments* needed to play the **Melodia do Retorno**.

Gameplay is driven by a virtual piano keyboard: solve music theory challenges to deal damage to each Bardo's guardian enemies.

## Package name
`com.info85.aironmusic`

## Story

Airon grew up surrounded by music and love. His father listened to favourite songs every evening; his mother gave him musical toys — a toy keyboard, a melodica (*escaleta*), and a simple guitar. One night he fell asleep to his father's music and woke up in the **Kingdom of Music**, a world where everything exists as sound, rhythm and harmony. He must cross six regions, defeat the Bardos and reunite the fragments of an ancient song to open the path home.

## Gameplay overview
- The first time you play you see a narrative intro screen that sets the story.
- Regions unlock sequentially — defeat each Bardo to unlock the next.
- Tap piano keys to answer challenges before the timer runs out.
- Correct answers deal damage to the enemy; wrong answers and timeouts damage Airon.
- Defeat the required number of guardian enemies to face the region Bardo.
- After clearing a region, Airon receives a **Song Fragment** and the Bardo delivers a story farewell.
- Collect all six fragments to trigger the **Melodia do Retorno** ending.

### Challenge types
| Mode | Description |
|------|-------------|
| **Single note** | Tap the displayed note |
| **Sequence** | Tap a sequence of 3–4 notes in order |
| **Scale** | Play a complete major or harmonic-minor scale |
| **Chord** | Tap all notes of a triad (any order) |

### The Six Bardos (regions)
| # | Icon | Region | Bardo | Title | Musical focus |
|---|------|--------|-------|-------|---------------|
| 1 | 🌲 | Floresta das Primeiras Melodias | Alson | Guardião da Primeira Melodia | Natural notes (C–B), single & sequence |
| 2 | 🌋 | Terras do Compasso Ardente | Angara | Senhora do Compasso Ardente | Sharps + naturals, tight timer |
| 3 | 🌊 | Lago dos Ecos Profundos | Raintein | Senhor dos Ecos Profundos | Major scales |
| 4 | 🏰 | Castelo da Harmonia Velada | Lord Senford | Nobre da Harmonia Velada | Harmonic-minor scales |
| 5 | ⚡ | Torre das Cordas Eternas | Theron | Mestre das Cordas Eternas | Triads / chords |
| 6 | 🌀 | Domínio do Fim e do Recomeço | Slikpot | Bardo do Fim e do Recomeço | All types, hardest timer |

## Project structure
```
app/src/main/
├── AndroidManifest.xml
├── java/com/info85/aironmusic/
│   ├── MainActivity.kt          ← Home screen
│   ├── StoryIntroActivity.kt    ← Narrative intro (shown once on first play)
│   ├── MapSelectActivity.kt     ← Region selection with sequential unlock
│   ├── GameActivity.kt          ← Game loop + bardo dialogs + fragment reveal
│   ├── model/
│   │   ├── ChallengeType.kt
│   │   ├── Challenge.kt
│   │   ├── Scale.kt
│   │   ├── ChordDef.kt
│   │   ├── Region.kt            ← Includes story / Bardo fields
│   │   ├── GamePhase.kt
│   │   ├── GameState.kt
│   │   └── GameData.kt          ← 6 Bardo regions + challenge generator
│   ├── ui/
│   │   ├── PianoKeyboardView.kt ← Custom View (white+black keys, touch)
│   │   └── GameViewModel.kt     ← Game engine (timer, HP, score, combo)
│   └── util/
│       ├── SoundManager.kt      ← AudioTrack sine-wave tone generator
│       └── StoryPrefs.kt        ← SharedPreferences story-progress helper
└── res/
    ├── layout/   activity_main, activity_story_intro, activity_map_select, activity_game, item_region
    ├── values/   strings, colors, themes
    └── drawable/ rpg_dialog_bg, ic_launcher_foreground, …
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
