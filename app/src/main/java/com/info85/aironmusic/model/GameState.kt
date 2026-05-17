package com.info85.aironmusic.model

data class GameState(
    var playerHp: Int = 100,
    var maxPlayerHp: Int = 100,
    var enemyHp: Int = 100,
    var maxEnemyHp: Int = 100,
    var maxTimeMs: Long = 5000L,
    var timeRemainingMs: Long = 5000L,
    var canInput: Boolean = true,
    var score: Int = 0,
    var combo: Int = 0,
    var maxCombo: Int = 0,
    var hits: Int = 0,
    var totalAnswers: Int = 0,
    var kills: Int = 0,
    var requiredKills: Int = 5,
    var currentRegionIndex: Int = 0,
    var challenge: Challenge? = null,
    var phase: GamePhase = GamePhase.PLAYING,
    var lastWasCorrect: Boolean = false
)
