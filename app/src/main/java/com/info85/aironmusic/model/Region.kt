package com.info85.aironmusic.model

data class Region(
    val id: Int,
    val name: String,
    val shortName: String,
    val icon: String,
    val colorHex: String,
    val mapX: Float,
    val mapY: Float,
    val baseTimeMs: Long,
    val modes: List<String>,
    val notePool: List<String> = emptyList(),
    val scales: List<Scale> = emptyList(),
    val chords: List<ChordDef> = emptyList(),
    val description: String = "",
    val enemyName: String = "Guardião",
    val enemyIcon: String = "👾",
    val requiredKills: Int = 5,
    // Story / Bardo fields
    val bardoName: String = enemyName,
    val bardoTitle: String = "",
    val introLore: String = "",
    val victoryLore: String = "",
    val fragmentName: String = "Fragmento de Canção",
    val fragmentLore: String = ""
)
