package com.info85.aironmusic

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.info85.aironmusic.databinding.ActivityStoryIntroBinding
import com.info85.aironmusic.util.StoryPrefs

class StoryIntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvIntroStory.text = INTRO_TEXT

        binding.tvIntroContinue.setOnClickListener { goToMap() }
        binding.scrollIntro.setOnClickListener { goToMap() }
    }

    private fun goToMap() {
        StoryPrefs.markIntroShown(this)
        startActivity(Intent(this, MapSelectActivity::class.java))
        finish()
    }

    companion object {
        private val INTRO_TEXT = """
ERA UMA VEZ...

Airon era um garoto sensível e curioso que cresceu cercado de música e amor.

Seu pai tinha o costume de ouvir suas canções favoritas ao anoitecer, enchendo a casa com melodias antigas cheias de emoção e memória.

Sua mãe lhe presenteou com brinquedos musicais ao longo dos anos — um teclado infantil, uma escaleta e um violão simples.

Para Airon, esses objetos eram mais do que brinquedos: eram lembranças vivas do carinho de sua família.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Numa noite aparentemente comum, Airon adormeceu ouvindo as músicas favoritas de seu pai ecoando pela casa...

Mas ao despertar, descobriu que já não estava em seu quarto.

Ele estava no REINO DA MÚSICA — um mundo místico onde tudo existe em forma de som, ritmo e harmonia.

As árvores vibram como cordas dedilhadas. Os rios correm em cadência. O vento sopra em melodias que sussurram segredos antigos.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

SUA MISSÃO:

Para retornar ao mundo real, Airon precisará atravessar o reino e enfrentar os SEIS GRANDES BARDOS — seres lendários que guardam os Fragmentos da Melodia do Retorno.

Cada Bardo domina uma parte fundamental da música:

🌲  ALSON — Guardião da Primeira Melodia
🌋  ANGARA — Senhora do Compasso Ardente
🌊  RAINTEIN — Senhor dos Ecos Profundos
🏰  LORD SENFORD — Nobre da Harmonia Velada
⚡  THERON — Mestre das Cordas Eternas
🌀  SLIKPOT — Bardo do Fim e do Recomeço

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Ao vencer cada Bardo, Airon recebe um FRAGMENTO DE CANÇÃO.

Reunidos os seis fragmentos, ele poderá tocar a MELODIA DO RETORNO — a única canção capaz de abrir o caminho entre os mundos.

Os instrumentos que sua mãe lhe deu guiarão seus passos.
A música que aprendeu de seu pai dará força ao seu coração.

SUA AVENTURA COMEÇA AGORA!
        """.trimIndent()
    }
}
