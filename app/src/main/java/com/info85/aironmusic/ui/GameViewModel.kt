package com.info85.aironmusic.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.info85.aironmusic.model.Challenge
import com.info85.aironmusic.model.ChallengeType
import com.info85.aironmusic.model.GameData
import com.info85.aironmusic.model.GamePhase
import com.info85.aironmusic.model.GameState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameViewModel : ViewModel() {

    private val _state = MutableLiveData(GameState())
    val state: LiveData<GameState> = _state

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private var timerJob: Job? = null

    // ── Public API ────────────────────────────────────────────────────────────

    fun startGame(regionIndex: Int) {
        val region = GameData.REGIONS[regionIndex]
        val initial = GameState(
            currentRegionIndex = regionIndex,
            maxTimeMs = region.baseTimeMs,
            timeRemainingMs = region.baseTimeMs,
            requiredKills = region.requiredKills,
            maxEnemyHp = 100,
            enemyHp = 100
        )
        _state.value = initial
        startNextChallenge(initial)
    }

    fun handleNoteInput(note: String) {
        val s = _state.value ?: return
        if (!s.canInput) return
        val challenge = s.challenge ?: return

        if (challenge.type == ChallengeType.CHORD) {
            handleChordInput(s, challenge, note)
        } else {
            handleOrderedInput(s, challenge, note)
        }
    }

    // ── Input handlers ────────────────────────────────────────────────────────

    private fun handleChordInput(s: GameState, challenge: Challenge, note: String) {
        when {
            note in challenge.answer && note !in challenge.input -> {
                challenge.input.add(note)
                _state.value = s
                if (challenge.isComplete) finishChallenge(s, success = true)
            }
            note !in challenge.answer -> finishChallenge(s, success = false)
            // Duplicate tap of a correct note: ignore
        }
    }

    private fun handleOrderedInput(s: GameState, challenge: Challenge, note: String) {
        val expected = challenge.answer.getOrNull(challenge.progress) ?: return
        if (note == expected) {
            challenge.input.add(note)
            _state.value = s
            if (challenge.isComplete) finishChallenge(s, success = true)
        } else {
            finishChallenge(s, success = false)
        }
    }

    // ── Challenge resolution ──────────────────────────────────────────────────

    private fun finishChallenge(s: GameState, success: Boolean) {
        timerJob?.cancel()
        s.canInput = false
        s.totalAnswers++
        s.lastWasCorrect = success

        if (success) {
            s.hits++
            s.combo++
            if (s.combo > s.maxCombo) s.maxCombo = s.combo
            val bonus = (50 + s.combo * 10).coerceAtMost(200)
            s.score += bonus
            val dmg = (20 + s.combo * 5).coerceAtMost(45)
            s.enemyHp = (s.enemyHp - dmg).coerceAtLeast(0)
            _message.value = "✅ Correto! +$bonus pts  🔥 Combo ×${s.combo}"
        } else {
            s.combo = 0
            s.playerHp = (s.playerHp - 20).coerceAtLeast(0)
            _message.value = "❌ Errou! −20 HP"
        }

        _state.value = s
        scheduleNextAction(s)
    }

    private fun onTimeout() {
        val s = _state.value ?: return
        if (!s.canInput) return
        timerJob?.cancel()
        s.canInput = false
        s.combo = 0
        s.totalAnswers++
        s.playerHp = (s.playerHp - 15).coerceAtLeast(0)
        _message.value = "⏱️ Tempo esgotado! −15 HP"
        _state.value = s
        scheduleNextAction(s)
    }

    private fun scheduleNextAction(s: GameState) {
        viewModelScope.launch {
            delay(900)

            if (s.playerHp <= 0) {
                s.phase = GamePhase.GAME_OVER
                _state.postValue(s)
                return@launch
            }

            if (s.enemyHp <= 0) {
                s.kills++
                if (s.kills >= s.requiredKills) {
                    s.phase = GamePhase.REGION_CLEAR
                    _state.postValue(s)
                } else {
                    s.phase = GamePhase.ENEMY_DEFEATED
                    _state.postValue(s)
                    delay(1200)
                    s.enemyHp = s.maxEnemyHp
                    s.phase = GamePhase.PLAYING
                    _state.postValue(s)
                    startNextChallenge(s)
                }
            } else {
                startNextChallenge(s)
            }
        }
    }

    // ── Timer ─────────────────────────────────────────────────────────────────

    private fun startNextChallenge(s: GameState) {
        val region = GameData.REGIONS[s.currentRegionIndex]
        s.challenge = GameData.generateChallenge(region)
        s.canInput = true
        s.timeRemainingMs = s.maxTimeMs
        s.phase = GamePhase.PLAYING
        _state.postValue(s)
        startTimer(s)
    }

    private fun startTimer(s: GameState) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            val start = System.currentTimeMillis()
            val max = s.maxTimeMs
            while (isActive) {
                val elapsed = System.currentTimeMillis() - start
                val remaining = (max - elapsed).coerceAtLeast(0L)
                s.timeRemainingMs = remaining
                _state.postValue(s)

                if (remaining <= 0L) {
                    withContext(Dispatchers.Main) { onTimeout() }
                    break
                }
                delay(50)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
