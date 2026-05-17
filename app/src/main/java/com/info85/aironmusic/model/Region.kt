package com.info85.aironmusic.model

data class Region(
    val id: Int,
    val name: String,
    val icon: String,
    val colorHex: String,
    val baseTimeMs: Long,
    val modes: List<String>,
    val notePool: List<String> = emptyList(),
    val scales: List<Scale> = emptyList(),
    val chords: List<ChordDef> = emptyList(),
    val description: String = "",
    val enemyName: String = "Guardião",
    val enemyIcon: String = "👾",
    val requiredKills: Int = 5
)
