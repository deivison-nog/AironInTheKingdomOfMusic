package com.info85.aironmusic

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.info85.aironmusic.databinding.ActivityGameBinding
import com.info85.aironmusic.model.ChallengeType
import com.info85.aironmusic.model.GameData
import com.info85.aironmusic.model.GamePhase
import com.info85.aironmusic.ui.GameViewModel
import com.info85.aironmusic.ui.PianoKeyboardView
import com.info85.aironmusic.util.SoundManager

class GameActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_REGION_INDEX = "region_index"
    }

    private lateinit var binding: ActivityGameBinding
    private val viewModel: GameViewModel by viewModels()

    private var regionIndex = 0
    private var dialogShown = false

    // Runnable to hide the floating message after a short delay
    private val hideMessage = Runnable {
        binding.tvMessage.visibility = View.INVISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        regionIndex = intent.getIntExtra(EXTRA_REGION_INDEX, 0)
            .coerceIn(0, GameData.REGIONS.lastIndex)
        val region = GameData.REGIONS[regionIndex]

        // Tint background with region colour (darkened so text stays readable)
        try {
            val base = Color.parseColor(region.colorHex)
            binding.root.setBackgroundColor(darken(base, 0.55f))
        } catch (_: Exception) { }

        binding.tvRegionName.text = "${region.icon} ${region.name}"
        binding.tvEnemyName.text  = "${region.enemyIcon} ${region.enemyName}"

        // Piano keyboard touch
        binding.pianoKeyboard.onNotePlayedListener = object : PianoKeyboardView.OnNotePlayedListener {
            override fun onNotePlayed(note: String) {
                SoundManager.playNote(note)
                viewModel.handleNoteInput(note)
            }
        }

        // Observe game state
        viewModel.state.observe(this) { s ->
            if (s == null) return@observe

            // HP bars
            binding.progressPlayerHp.progress = s.playerHp
            binding.tvPlayerHp.text = "❤️ HP: ${s.playerHp}/${s.maxPlayerHp}"

            val enemyPct = if (s.maxEnemyHp > 0) s.enemyHp * 100 / s.maxEnemyHp else 0
            binding.progressEnemyHp.progress = enemyPct
            binding.tvEnemyHp.text = "💀 ${s.enemyHp}/${s.maxEnemyHp}"

            // Kill counter
            binding.tvKills.text = "⚔️ ${s.kills}/${s.requiredKills}"

            // Timer bar
            val timerPct = if (s.maxTimeMs > 0L)
                (s.timeRemainingMs * 100L / s.maxTimeMs).toInt().coerceIn(0, 100)
            else 0
            binding.progressTimer.progress = timerPct

            // Timer colour: green → yellow → red
            val timerColor = when {
                timerPct > 60 -> Color.parseColor("#4ade80")
                timerPct > 30 -> Color.parseColor("#facc15")
                else          -> Color.parseColor("#f87171")
            }
            binding.progressTimer.progressTintList =
                android.content.res.ColorStateList.valueOf(timerColor)

            // Score & combo
            binding.tvScore.text = "🏆 ${s.score}"
            binding.tvCombo.text = if (s.combo > 1) "🔥 ×${s.combo}" else ""

            // Challenge prompt
            val ch = s.challenge
            if (ch != null) {
                binding.tvChallengePrompt.text = ch.prompt
                binding.tvChallengeProgress.text =
                    if (ch.total > 1) "${ch.progress} / ${ch.total}" else ""

                // Highlight expected key(s) on the keyboard
                binding.pianoKeyboard.activeNotes = when (ch.type) {
                    ChallengeType.CHORD -> (ch.answer.toSet() - ch.input.toSet())
                    else -> if (ch.progress < ch.total) setOf(ch.answer[ch.progress]) else emptySet()
                }

                binding.pianoKeyboard.correctNotes = ch.input.toSet()
            }

            // Phase transitions
            when (s.phase) {
                GamePhase.ENEMY_DEFEATED -> {
                    SoundManager.playSuccess()
                    binding.tvEnemyName.text = "💀 Derrotado!"
                }
                GamePhase.PLAYING -> {
                    binding.tvEnemyName.text = "${region.enemyIcon} ${region.enemyName}"
                    binding.pianoKeyboard.errorNotes = emptySet()
                }
                GamePhase.GAME_OVER -> showGameOver(s.score)
                GamePhase.REGION_CLEAR -> showRegionClear(s.score)
                else -> { }
            }
        }

        // Observe one-shot messages
        viewModel.message.observe(this) { msg ->
            binding.tvMessage.text = msg
            binding.tvMessage.visibility = View.VISIBLE
            binding.tvMessage.removeCallbacks(hideMessage)
            binding.tvMessage.postDelayed(hideMessage, 1800)
        }

        viewModel.startGame(regionIndex)
    }

    // ── End-game dialogs ──────────────────────────────────────────────────────

    private fun showGameOver(score: Int) {
        if (dialogShown) return
        dialogShown = true
        SoundManager.playError()
        AlertDialog.Builder(this)
            .setTitle("💀 Game Over!")
            .setMessage("Airon foi derrotado…\n\nPontuação final: $score")
            .setPositiveButton("Tentar Novamente") { _, _ ->
                dialogShown = false
                recreate()
            }
            .setNegativeButton("Menu Principal") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun showRegionClear(score: Int) {
        if (dialogShown) return
        dialogShown = true
        SoundManager.playVictory()
        AlertDialog.Builder(this)
            .setTitle("🎉 Região Conquistada!")
            .setMessage("Airon venceu todos os inimigos!\n\nPontuação: $score")
            .setPositiveButton("Escolher Outro Mapa") { _, _ ->
                startActivity(Intent(this, MapSelectActivity::class.java))
                finish()
            }
            .setNegativeButton("Menu Principal") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Returns a darkened version of [color] by multiplying each channel by [factor]. */
    private fun darken(color: Int, factor: Float): Int {
        val r = (Color.red(color)   * factor).toInt().coerceIn(0, 255)
        val g = (Color.green(color) * factor).toInt().coerceIn(0, 255)
        val b = (Color.blue(color)  * factor).toInt().coerceIn(0, 255)
        return Color.rgb(r, g, b)
    }
}
