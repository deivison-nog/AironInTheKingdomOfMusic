package com.info85.aironmusic.model

/**
 * Central data store and challenge factory for the game.
 *
 * [REGIONS] contains the six canonical Bardo regions that follow the story of
 * *Airon no Reino da Música*, progressing from introductory natural notes all the
 * way to the final boss Slikpot who tests every musical concept at once:
 *
 *  1. **Alson** — natural notes (C–B), single & sequence challenges
 *  2. **Angara** — sharps and natural notes, tighter timer, single & sequence
 *  3. **Raintein** — major scales
 *  4. **Lord Senford** — harmonic-minor scales
 *  5. **Theron** — triads / chords
 *  6. **Slikpot** — all challenge types, hardest timer
 *
 * Each region cleared unlocks the next and awards a *Song Fragment*.  When all
 * six fragments are collected [GameActivity] shows the *Melodia do Retorno* ending.
 *
 * [generateChallenge] picks a challenge type at random from the region's [Region.modes]
 * list and produces a [Challenge] whose [Challenge.answer] the player must reproduce
 * on the virtual piano keyboard.
 */
object GameData {

    val REGIONS = listOf(

        // ── 1. Alson – Floresta das Primeiras Melodias ─────────────────────────
        Region(
            id = 0,
            name = "Floresta das Primeiras Melodias",
            shortName = "Floresta",
            icon = "🌲",
            colorHex = "#1b4332",
            mapX = 0.30f,
            mapY = 0.31f,
            baseTimeMs = 7000,
            modes = listOf("single", "sequence"),
            notePool = listOf("C", "D", "E", "F", "G", "A", "B"),
            description = "As notas naturais habitam aqui, vibrando como cordas dedilhadas nas árvores ancestrais.",
            enemyName = "Espírito da Floresta",
            enemyIcon = "🧝",
            requiredKills = 3,
            bardoName = "Alson",
            bardoTitle = "Guardião da Primeira Melodia",
            introLore = "Airon avança pela floresta onde as árvores vibram como cordas dedilhadas.\n\n" +
                "Das sombras surge ALSON, o Guardião da Primeira Melodia.\n\n" +
                "ALSON: \"Então você é Airon... Para retornar ao mundo real, precisará dos Seis Fragmentos de Canção.\"\n\n" +
                "\"O primeiro está comigo. Mostre-me que sente as notas — a linguagem mais simples da música.\"",
            victoryLore = "ALSON: \"Você demonstrou sensibilidade genuína, Airon.\"\n\n" +
                "Uma luz dourada flutua até Airon.\n\n" +
                "\"Carregue o Primeiro Fragmento em seu coração. A jornada está apenas começando.\"\n\n" +
                "\"Encontre os outros Bardos. Cada um guarda um pedaço da Melodia do Retorno.\"",
            fragmentName = "1º Fragmento de Canção",
            fragmentLore = "Um brilho suave de notas naturais — a essência da música em sua forma mais pura."
        ),

        // ── 2. Angara – Terras do Compasso Ardente ────────────────────────────
        Region(
            id = 1,
            name = "Terras do Compasso Ardente",
            shortName = "Vulcão",
            icon = "🌋",
            colorHex = "#6a040f",
            mapX = 0.39f,
            mapY = 0.53f,
            baseTimeMs = 5500,
            modes = listOf("single", "sequence"),
            notePool = listOf("C#", "D#", "F#", "G#", "A#", "C", "D", "E"),
            description = "A terra treme ao ritmo do fogo. As notas alteradas surgem das chamas como fragmentos incandescentes.",
            enemyName = "Guardião do Fogo",
            enemyIcon = "🔥",
            requiredKills = 4,
            bardoName = "Angara",
            bardoTitle = "Senhora do Compasso Ardente",
            introLore = "O chão treme sob os pés de Airon enquanto lavas borbulham ao redor.\n\n" +
                "Do coração do vulcão surge ANGARA, a Senhora do Compasso Ardente.\n\n" +
                "ANGARA: \"Ritmo, tempo, reflexo! Aqui não há espaço para hesitação!\"\n\n" +
                "\"Se você sobreviver ao ritmo do fogo, talvez mereça o próximo Fragmento.\"",
            victoryLore = "ANGARA: \"Sua resistência e ritmo me surpreenderam, Airon.\"\n\n" +
                "As chamas se acalmam e um fragmento brilhante flutua até Airon.\n\n" +
                "\"Leve o Segundo Fragmento. Você vai precisar de toda essa energia nas próximas batalhas.\"",
            fragmentName = "2º Fragmento de Canção",
            fragmentLore = "Pulsa com ritmo ardente — a força do compasso na música."
        ),

        // ── 3. Raintein – Lago dos Ecos Profundos ─────────────────────────────
        Region(
            id = 2,
            name = "Lago dos Ecos Profundos",
            shortName = "Lago",
            icon = "🌊",
            colorHex = "#0077b6",
            mapX = 0.24f,
            mapY = 0.60f,
            baseTimeMs = 8000,
            modes = listOf("scale"),
            scales = listOf(
                Scale("C Maior", listOf("C", "D", "E", "F", "G", "A", "B", "C")),
                Scale("G Maior", listOf("G", "A", "B", "C", "D", "E", "F#", "G")),
                Scale("D Maior", listOf("D", "E", "F#", "G", "A", "B", "C#", "D")),
                Scale("F Maior", listOf("F", "G", "A", "A#", "C", "D", "E", "F"))
            ),
            description = "As águas espelhadas guardam melodias que reverberam como lembranças. As escalas maiores são seus caminhos.",
            enemyName = "Serpente do Eco",
            enemyIcon = "🐉",
            requiredKills = 4,
            bardoName = "Raintein",
            bardoTitle = "Senhor dos Ecos Profundos",
            introLore = "As águas espelhadas do lago guardam melodias que soam como memórias distantes.\n\n" +
                "RAINTEIN emerge das profundezas lentamente, sua forma translúcida como a água.\n\n" +
                "RAINTEIN: \"As escalas são os caminhos que a música percorre, Airon. São memórias que se repetem...\"\n\n" +
                "\"Percorra esses caminhos e eu lhe darei o que busca.\"",
            victoryLore = "RAINTEIN: \"As escalas maiores revelam clareza e luz interior. Você as percorreu com maestria.\"\n\n" +
                "O lago ilumina-se e um fragmento sobe das profundezas.\n\n" +
                "\"Leve o Terceiro Fragmento. Que essas escalas guiem seus passos.\"",
            fragmentName = "3º Fragmento de Canção",
            fragmentLore = "Ressoa com escalas harmônicas — memórias que percorrem o tempo."
        ),

        // ── 4. Lord Senford – Castelo da Harmonia Velada ──────────────────────
        Region(
            id = 3,
            name = "Castelo da Harmonia Velada",
            shortName = "Castelo",
            icon = "🏰",
            colorHex = "#2d00f7",
            mapX = 0.58f,
            mapY = 0.26f,
            baseTimeMs = 8000,
            modes = listOf("scale"),
            scales = listOf(
                Scale("A Menor Harmônica", listOf("A", "B", "C", "D", "E", "F", "G#", "A")),
                Scale("E Menor Harmônica", listOf("E", "F#", "G", "A", "B", "C", "D#", "E")),
                Scale("D Menor Harmônica", listOf("D", "E", "F", "G", "A", "A#", "C#", "D")),
                Scale("B Menor Harmônica", listOf("B", "C#", "D", "E", "F#", "G", "A#", "B"))
            ),
            description = "O castelo é envolvido em névoa e tensão emocional. Escalas menores harmônicas revelam profundidade.",
            enemyName = "Cavaleiro Sombrio",
            enemyIcon = "🗡️",
            requiredKills = 4,
            bardoName = "Lord Senford",
            bardoTitle = "Nobre da Harmonia Velada",
            introLore = "O Castelo da Harmonia Velada é envolvido em névoa e tensão emocional.\n\n" +
                "LORD SENFORD aguarda no salão principal, tocando notas que parecem suspirar.\n\n" +
                "LORD SENFORD: \"A verdadeira harmonia contém sombra e luz, jovem.\"\n\n" +
                "\"Domine a tensão emocional das escalas menores, e lhe concederei o Quarto Fragmento.\"",
            victoryLore = "LORD SENFORD: \"Impressionante. Você compreendeu que beleza e dor coexistem na música.\"\n\n" +
                "O castelo vibra em ressonância e um fragmento sombrio e brilhante chega até Airon.\n\n" +
                "\"Carregue o Quarto Fragmento. A profundidade harmônica sempre o guiará.\"",
            fragmentName = "4º Fragmento de Canção",
            fragmentLore = "Emana tensão harmônica — a sombra que torna a música completa."
        ),

        // ── 5. Theron – Torre das Cordas Eternas ──────────────────────────────
        Region(
            id = 4,
            name = "Torre das Cordas Eternas",
            shortName = "Torre",
            icon = "⚡",
            colorHex = "#cc8800",
            mapX = 0.50f,
            mapY = 0.74f,
            baseTimeMs = 7000,
            modes = listOf("chord"),
            chords = listOf(
                ChordDef("C Maior", listOf("C", "E", "G")),
                ChordDef("A Menor", listOf("A", "C", "E")),
                ChordDef("G Maior", listOf("G", "B", "D")),
                ChordDef("E Menor", listOf("E", "G", "B")),
                ChordDef("F Maior", listOf("F", "A", "C")),
                ChordDef("D Menor", listOf("D", "F", "A")),
                ChordDef("B Diminuto", listOf("B", "D", "F"))
            ),
            description = "Acordes ressoam por cada corredor. Os pilares harmônicos que sustentam todas as grandes canções.",
            enemyName = "Guardião das Cordas",
            enemyIcon = "🎸",
            requiredKills = 4,
            bardoName = "Theron",
            bardoTitle = "Mestre das Cordas Eternas",
            introLore = "Airon sobe a Torre das Cordas Eternas, onde acordes ressoam por cada corredor.\n\n" +
                "THERON, o Mestre das Cordas Eternas, aguarda no alto, seu violão eterno em mãos.\n\n" +
                "THERON: \"Os acordes são os pilares que sustentam toda grande canção, Airon.\"\n\n" +
                "\"Toque-os com precisão e sentimento, e o Quinto Fragmento será seu.\"",
            victoryLore = "THERON: \"Você domina os acordes com maestria e sensibilidade. A estrutura harmônica reside em você.\"\n\n" +
                "Theron toca uma última melodia no violão e um fragmento brilhante flutua até Airon.\n\n" +
                "\"Leve o Quinto Fragmento. Agora você tem quase tudo. Resta apenas Slikpot...\"",
            fragmentName = "5º Fragmento de Canção",
            fragmentLore = "Vibra com acordes eternos — a estrutura que sustenta todas as canções."
        ),

        // ── 6. Slikpot – Domínio do Fim e do Recomeço ─────────────────────────
        Region(
            id = 5,
            name = "Domínio do Fim e do Recomeço",
            shortName = "Final",
            icon = "🌀",
            colorHex = "#240046",
            mapX = 0.71f,
            mapY = 0.54f,
            baseTimeMs = 5000,
            modes = listOf("single", "sequence", "scale", "chord"),
            notePool = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"),
            scales = listOf(
                Scale("C Maior", listOf("C", "D", "E", "F", "G", "A", "B", "C")),
                Scale("G Maior", listOf("G", "A", "B", "C", "D", "E", "F#", "G")),
                Scale("A Menor Harmônica", listOf("A", "B", "C", "D", "E", "F", "G#", "A")),
                Scale("E Menor Harmônica", listOf("E", "F#", "G", "A", "B", "C", "D#", "E"))
            ),
            chords = listOf(
                ChordDef("C Maior", listOf("C", "E", "G")),
                ChordDef("A Menor", listOf("A", "C", "E")),
                ChordDef("G Maior", listOf("G", "B", "D")),
                ChordDef("E Menor", listOf("E", "G", "B")),
                ChordDef("F Maior", listOf("F", "A", "C")),
                ChordDef("D Menor", listOf("D", "F", "A")),
                ChordDef("B Diminuto", listOf("B", "D", "F"))
            ),
            description = "O coração do Reino da Música. Slikpot reúne em si todas as formas da música. O desafio final.",
            enemyName = "Guardião Sombrio",
            enemyIcon = "👁️",
            requiredKills = 6,
            bardoName = "Slikpot",
            bardoTitle = "Bardo do Fim e do Recomeço",
            introLore = "Airon chega ao coração do Reino da Música com cinco fragmentos brilhando em seu ser.\n\n" +
                "SLIKPOT emerge do silêncio absoluto, reunindo em si todas as formas da música.\n\n" +
                "SLIKPOT: \"Você chegou até aqui, Airon. Impressionante.\"\n\n" +
                "\"Para obter o último Fragmento e tocar a Melodia do Retorno, você precisará superar tudo o que aprendeu — notas, sequências, escalas, acordes. Tudo ao mesmo tempo.\"\n\n" +
                "\"Mostre-me.\"",
            victoryLore = "Slikpot inclina a cabeça, um sorriso lento em seus lábios.\n\n" +
                "SLIKPOT: \"Você reuniu os seis fragmentos, Airon. A Melodia do Retorno está completa.\"\n\n" +
                "O Sexto Fragmento flutua até Airon, pulsando com toda a força do reino.\n\n" +
                "\"Agora toque essa melodia com os instrumentos que sua mãe lhe deu, com o amor que aprendeu de seu pai...\"\n\n" +
                "\"E o caminho se abrirá.\"",
            fragmentName = "6º Fragmento de Canção",
            fragmentLore = "O fragmento final — reúne em si todas as formas da música. A chave para a Melodia do Retorno."
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
