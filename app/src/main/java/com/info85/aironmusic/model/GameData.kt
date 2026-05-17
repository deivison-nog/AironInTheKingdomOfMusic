package com.info85.aironmusic.model

object GameData {

    val REGIONS = listOf(
        Region(
            id = 0,
            name = "Floresta das Notas",
            icon = "🌲",
            colorHex = "#1b4332",
            baseTimeMs = 6000,
            modes = listOf("single"),
            notePool = listOf("C", "D", "E", "F", "G", "A", "B"),
            description = "As notas naturais habitam aqui. Aprenda a identificar as notas básicas.",
            enemyName = "Duende da Floresta",
            enemyIcon = "🧝",
            requiredKills = 5
        ),
        Region(
            id = 1,
            name = "Vulcão das Alterações",
            icon = "🌋",
            colorHex = "#6a040f",
            baseTimeMs = 5000,
            modes = listOf("single", "sequence"),
            notePool = listOf("C#", "D#", "F#", "G#", "A#"),
            description = "As notas alteradas surgem do fogo. Domine os sustenidos.",
            enemyName = "Golem de Lava",
            enemyIcon = "🔥",
            requiredKills = 5
        ),
        Region(
            id = 2,
            name = "Castelo Cromático",
            icon = "🏰",
            colorHex = "#2d00f7",
            baseTimeMs = 4500,
            modes = listOf("single", "sequence"),
            notePool = listOf("C","C#","D","D#","E","F","F#","G","G#","A","A#","B"),
            description = "Todas as 12 notas coexistem neste castelo. Esteja preparado.",
            enemyName = "Cavaleiro Sombrio",
            enemyIcon = "🗡️",
            requiredKills = 5
        ),
        Region(
            id = 3,
            name = "Oceano das Escalas Maiores",
            icon = "🌊",
            colorHex = "#0077b6",
            baseTimeMs = 7000,
            modes = listOf("scale"),
            scales = listOf(
                Scale("C Maior", listOf("C","D","E","F","G","A","B","C")),
                Scale("G Maior", listOf("G","A","B","C","D","E","F#","G")),
                Scale("D Maior", listOf("D","E","F#","G","A","B","C#","D")),
                Scale("F Maior", listOf("F","G","A","A#","C","D","E","F"))
            ),
            description = "Navegue pelas escalas maiores das profundezas.",
            enemyName = "Serpente Marinha",
            enemyIcon = "🐉",
            requiredKills = 4
        ),
        Region(
            id = 4,
            name = "Templo Lunar",
            icon = "🌙",
            colorHex = "#5a189a",
            baseTimeMs = 7000,
            modes = listOf("scale"),
            scales = listOf(
                Scale("A Menor Natural", listOf("A","B","C","D","E","F","G","A")),
                Scale("E Menor Natural", listOf("E","F#","G","A","B","C","D","E")),
                Scale("A Menor Harmônica", listOf("A","B","C","D","E","F","G#","A")),
                Scale("E Menor Harmônica", listOf("E","F#","G","A","B","C","D#","E"))
            ),
            description = "Escalas menores naturais e harmônicas sob a luz da lua.",
            enemyName = "Espectro Lunar",
            enemyIcon = "👻",
            requiredKills = 4
        ),
        Region(
            id = 5,
            name = "Torre da Harmonia",
            icon = "⚡",
            colorHex = "#cc8800",
            baseTimeMs = 6000,
            modes = listOf("chord"),
            chords = listOf(
                ChordDef("C Maior", listOf("C","E","G")),
                ChordDef("A Menor", listOf("A","C","E")),
                ChordDef("G Maior", listOf("G","B","D")),
                ChordDef("E Menor", listOf("E","G","B")),
                ChordDef("F Maior", listOf("F","A","C")),
                ChordDef("D Menor", listOf("D","F","A")),
                ChordDef("B Diminuto", listOf("B","D","F"))
            ),
            description = "Acordes ressoam pelos corredores desta torre mística.",
            enemyName = "Mago Arcano",
            enemyIcon = "🧙",
            requiredKills = 4
        ),
        Region(
            id = 6,
            name = "Portal Arcano",
            icon = "🌀",
            colorHex = "#240046",
            baseTimeMs = 5000,
            modes = listOf("single", "sequence", "scale", "chord"),
            notePool = listOf("C","D","E","F","G","A","B","C#","F#","G#"),
            scales = listOf(
                Scale("C Maior", listOf("C","D","E","F","G","A","B","C")),
                Scale("A Menor Harmônica", listOf("A","B","C","D","E","F","G#","A"))
            ),
            chords = listOf(
                ChordDef("C Maior", listOf("C","E","G")),
                ChordDef("A Menor", listOf("A","C","E")),
                ChordDef("G Maior", listOf("G","B","D"))
            ),
            description = "Uma mistura de todos os desafios musicais. Somente os mestres chegam aqui.",
            enemyName = "Guardião do Portal",
            enemyIcon = "🌀",
            requiredKills = 5
        ),
        Region(
            id = 7,
            name = "Palácio Real",
            icon = "👑",
            colorHex = "#7b0e6e",
            baseTimeMs = 4000,
            modes = listOf("single", "sequence", "scale", "chord"),
            notePool = listOf("C","C#","D","D#","E","F","F#","G","G#","A","A#","B"),
            scales = listOf(
                Scale("C Maior", listOf("C","D","E","F","G","A","B","C")),
                Scale("G Maior", listOf("G","A","B","C","D","E","F#","G")),
                Scale("A Menor Harmônica", listOf("A","B","C","D","E","F","G#","A")),
                Scale("E Menor Harmônica", listOf("E","F#","G","A","B","C","D#","E"))
            ),
            chords = listOf(
                ChordDef("C Maior", listOf("C","E","G")),
                ChordDef("A Menor", listOf("A","C","E")),
                ChordDef("G Maior", listOf("G","B","D")),
                ChordDef("D Menor", listOf("D","F","A")),
                ChordDef("B Diminuto", listOf("B","D","F"))
            ),
            description = "O Palácio do Rei da Música. Airon deve provar que é o Mestre da Harmonia.",
            enemyName = "Rei da Discórdia",
            enemyIcon = "👑",
            requiredKills = 7
        )
    )

    fun generateChallenge(region: Region): Challenge {
        val mode = region.modes.random()

        return when (mode) {
            "single" -> {
                val note = region.notePool.random()
                Challenge(
                    type = ChallengeType.SINGLE,
                    prompt = "Toque a nota  $note",
                    answer = listOf(note)
                )
            }
            "sequence" -> {
                val size = (3..4).random()
                val seq = List(size) { region.notePool.random() }
                Challenge(
                    type = ChallengeType.SEQUENCE,
                    prompt = "Toque a sequência:\n${seq.joinToString(" → ")}",
                    answer = seq
                )
            }
            "scale" -> {
                val scale = region.scales.random()
                Challenge(
                    type = ChallengeType.SCALE,
                    prompt = "Toque a escala\n${scale.name}",
                    answer = scale.notes
                )
            }
            "chord" -> {
                val chord = region.chords.random()
                Challenge(
                    type = ChallengeType.CHORD,
                    prompt = "Toque o acorde\n${chord.name}",
                    answer = chord.notes
                )
            }
            else -> {
                Challenge(
                    type = ChallengeType.SINGLE,
                    prompt = "Toque a nota  C",
                    answer = listOf("C")
                )
            }
        }
    }
}
