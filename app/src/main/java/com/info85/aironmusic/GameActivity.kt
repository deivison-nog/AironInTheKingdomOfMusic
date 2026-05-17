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
import com.info85.aironmusic.util.StoryPrefs

class GameActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_REGION_INDEX = "region_index"
        private const val TOTAL_REGIONS = 6
    }

    private lateinit var binding: ActivityGameBinding
    private val viewModel: GameViewModel by viewModels()

    private var regionIndex = 0
    private var dialogShown = false
    private var introDismissed = false

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

        try {
            val base = Color.parseColor(region.colorHex)
            binding.root.setBackgroundColor(darken(base, 0.55f))
        } catch (_: Exception) { }

        binding.tvRegionName.text = "${region.icon} ${region.name}"
        binding.tvEnemyName.text  = "${region.enemyIcon} ${region.bardoName}"

        binding.pianoKeyboard.onNotePlayedListener = object : PianoKeyboardView.OnNotePlayedListener {
            override fun onNotePlayed(note: String) {
                if (!introDismissed) return
                SoundManager.playNote(note)
                viewModel.handleNoteInput(note)
            }
        }

        viewModel.state.observe(this) { s ->
            if (s == null) return@observe

            binding.progressPlayerHp.progress = s.playerHp
            binding.tvPlayerHp.text = "❤️ HP: ${s.playerHp}/${s.maxPlayerHp}"

            val enemyPct = if (s.maxEnemyHp > 0) s.enemyHp * 100 / s.maxEnemyHp else 0
            binding.progressEnemyHp.progress = enemyPct
            binding.tvEnemyHp.text = "💀 ${s.enemyHp}/${s.maxEnemyHp}"

            binding.tvKills.text = "⚔️ ${s.kills}/${s.requiredKills}"

            val timerPct = if (s.maxTimeMs > 0L)
                (s.timeRemainingMs * 100L / s.maxTimeMs).toInt().coerceIn(0, 100)
            else 0
            binding.progressTimer.progress = timerPct

            val timerColor = when {
                timerPct > 60 -> Color.parseColor("#4ade80")
                timerPct > 30 -> Color.parseColor("#facc15")
                else          -> Color.parseColor("#f87171")
            }
            binding.progressTimer.progressTintList =
                android.content.res.ColorStateList.valueOf(timerColor)

            binding.tvScore.text = "🏆 ${s.score}"
            binding.tvCombo.text = if (s.combo > 1) "🔥 ×${s.combo}" else ""

            val ch = s.challenge
            if (ch != null) {
                binding.tvChallengePrompt.text = ch.prompt
                binding.tvChallengeProgress.text =
                    if (ch.total > 1) "${ch.progress} / ${ch.total}" else ""

                binding.pianoKeyboard.activeNotes = when (ch.type) {
                    ChallengeType.CHORD -> (ch.answer.toSet() - ch.input.toSet())
                    else -> if (ch.progress < ch.total) setOf(ch.answer[ch.progress]) else emptySet()
                }

                binding.pianoKeyboard.correctNotes = ch.input.toSet()
            }

            when (s.phase) {
                GamePhase.ENEMY_DEFEATED -> {
                    SoundManager.playSuccess()
                    binding.tvEnemyName.text = "💀 Derrotado!"
                }
                GamePhase.PLAYING -> {
                    binding.tvEnemyName.text = "${region.enemyIcon} ${region.bardoName}"
                    binding.pianoKeyboard.errorNotes = emptySet()
                }
                GamePhase.GAME_OVER -> showGameOver(s.score)
                GamePhase.REGION_CLEAR -> showRegionClear(s.score)
                else -> { }
            }
        }

        viewModel.message.observe(this) { msg ->
            binding.tvMessage.text = msg
            binding.tvMessage.visibility = View.VISIBLE
            binding.tvMessage.removeCallbacks(hideMessage)
            binding.tvMessage.postDelayed(hideMessage, 1800)
        }

        // Show bardo intro dialog before the game starts
        if (region.introLore.isNotBlank()) {
            showBardoIntro(region.introLore)
        } else {
            startGameNow()
        }
    }

    // ── Bardo intro ───────────────────────────────────────────────────────────

    private fun showBardoIntro(lore: String) {
        val region = GameData.REGIONS[regionIndex]
        AlertDialog.Builder(this)
            .setTitle("${region.icon}  ${region.bardoName}")
            .setMessage(lore)
            .setPositiveButton("BATALHAR!") { _, _ -> startGameNow() }
            .setCancelable(false)
            .show()
            .apply { styleRpgDialog(this) }
    }

    private fun startGameNow() {
        introDismissed = true
        viewModel.startGame(regionIndex)
    }

    // ── End-game dialogs ──────────────────────────────────────────────────────

    private fun showGameOver(score: Int) {
        if (dialogShown) return
        dialogShown = true
        SoundManager.playError()
        AlertDialog.Builder(this)
            .setTitle("💀  GAME OVER")
            .setMessage("Airon foi derrotado…\n\nPontuação final: $score")
            .setPositiveButton("TENTAR NOVAMENTE") { _, _ ->
                dialogShown = false
                introDismissed = true
                viewModel.startGame(regionIndex)
            }
            .setNegativeButton("MENU PRINCIPAL") { _, _ -> finish() }
            .setCancelable(false)
            .show()
            .apply { styleRpgDialog(this) }
    }

    private fun showRegionClear(score: Int) {
        if (dialogShown) return
        dialogShown = true
        SoundManager.playVictory()

        val region = GameData.REGIONS[regionIndex]

        // Persist cleared progress
        StoryPrefs.markRegionCleared(this, regionIndex)

        val clearedCount = StoryPrefs.getRegionsCleared(this)
        val allCleared   = clearedCount >= TOTAL_REGIONS

        val message = buildString {
            append(region.victoryLore)
            append("\n\n━━━━━━━━━━━━━━━━━━\n")
            append("🎵  ${region.fragmentName} obtido!\n")
            append("\"${region.fragmentLore}\"")
            if (!allCleared) {
                append("\n\nFragmentos: $clearedCount / $TOTAL_REGIONS")
            }
        }

        if (allCleared) {
            showMelodiaDoRetorno(message)
        } else {
            AlertDialog.Builder(this)
                .setTitle("🎉  REGIÃO CONQUISTADA!")
                .setMessage(message)
                .setPositiveButton("ESCOLHER MAPA") { _, _ ->
                    startActivity(Intent(this, MapSelectActivity::class.java))
                    finish()
                }
                .setNegativeButton("MENU PRINCIPAL") { _, _ -> finish() }
                .setCancelable(false)
                .show()
                .apply { styleRpgDialog(this) }
        }
    }

    private fun showMelodiaDoRetorno(fragmentMessage: String) {
        val ending = """
$fragmentMessage

━━━━━━━━━━━━━━━━━━

✨  A MELODIA DO RETORNO  ✨

Airon toca a Melodia do Retorno com os instrumentos que sua mãe lhe deu.

O teclado infantil ressoa com precisão.
A escaleta canaliza cada melodia e respiração.
O violão vibra com força harmônica e memória afetiva.

Todo o Reino da Música ressoa em uma única harmonia.

As paisagens vibram, o céu se abre em luz sonora, e o caminho entre os mundos é restaurado.

Quando Airon desperta em seu quarto, as músicas favoritas de seu pai ainda ecoam suavemente...

Seus brinquedos musicais continuam ali, silenciosos, mas carregados de um novo significado.

Airon voltou diferente: mais forte, mais sensível, e mais próximo da música que sempre esteve presente em sua vida.

FIM
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("🌟  MELODIA DO RETORNO")
            .setMessage(ending)
            .setPositiveButton("JOGAR NOVAMENTE") { _, _ ->
                StoryPrefs.resetProgress(this)
                startActivity(Intent(this, MapSelectActivity::class.java))
                finish()
            }
            .setNegativeButton("MENU PRINCIPAL") { _, _ -> finish() }
            .setCancelable(false)
            .show()
            .apply { styleRpgDialog(this) }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun darken(color: Int, factor: Float): Int {
        val r = (Color.red(color)   * factor).toInt().coerceIn(0, 255)
        val g = (Color.green(color) * factor).toInt().coerceIn(0, 255)
        val b = (Color.blue(color)  * factor).toInt().coerceIn(0, 255)
        return Color.rgb(r, g, b)
    }

    /**
     * Applies a dark RPG-style look to an AlertDialog:
     * dark background, monospace message font, white title.
     */
    private fun styleRpgDialog(dialog: AlertDialog) {
        try {
            dialog.window?.setBackgroundDrawableResource(android.R.color.black)
            // Tint message text to match 8-bit RPG palette
            dialog.findViewById<android.widget.TextView>(android.R.id.message)?.apply {
                setTextColor(Color.parseColor("#DDDDDD"))
                typeface = android.graphics.Typeface.MONOSPACE
                textSize = 13f
                setLineSpacing(0f, 1.4f)
            }
        } catch (_: Exception) { }
    }
}
