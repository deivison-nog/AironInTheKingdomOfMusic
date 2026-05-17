package com.info85.aironmusic.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.sin

/**
 * Generates musical tones using AudioTrack with sine-wave synthesis.
 * Each note is played on a background thread to avoid blocking the UI.
 */
object SoundManager {

    private val NOTE_FREQUENCIES = mapOf(
        "C"  to 261.63f,
        "C#" to 277.18f,
        "D"  to 293.66f,
        "D#" to 311.13f,
        "E"  to 329.63f,
        "F"  to 349.23f,
        "F#" to 369.99f,
        "G"  to 392.00f,
        "G#" to 415.30f,
        "A"  to 440.00f,
        "A#" to 466.16f,
        "B"  to 493.88f
    )

    fun playNote(note: String, durationMs: Int = 350) {
        val frequency = NOTE_FREQUENCIES[note] ?: return
        Thread { playTone(frequency, durationMs) }.start()
    }

    fun playSuccess() {
        Thread {
            playTone(523.25f, 100)
            Thread.sleep(110)
            playTone(659.25f, 100)
            Thread.sleep(110)
            playTone(783.99f, 200)
        }.start()
    }

    fun playError() {
        Thread {
            playTone(180f, 120)
            Thread.sleep(50)
            playTone(140f, 250)
        }.start()
    }

    fun playVictory() {
        Thread {
            val melody = listOf(
                523.25f to 120, 523.25f to 120, 523.25f to 120,
                523.25f to 350, 415.30f to 350, 466.16f to 350,
                523.25f to 550
            )
            for ((freq, dur) in melody) {
                playTone(freq, dur)
                Thread.sleep((dur + 20).toLong())
            }
        }.start()
    }

    fun playTimeout() {
        Thread { playTone(220f, 400) }.start()
    }

    private fun playTone(frequency: Float, durationMs: Int) {
        val sampleRate = 44100
        val numSamples = (sampleRate * durationMs / 1000.0).toInt()
        if (numSamples <= 0) return

        val buffer = ShortArray(numSamples)
        val attackSamples = (sampleRate * 0.01).toInt().coerceAtLeast(1)
        val releaseSamples = (sampleRate * 0.08).toInt().coerceAtLeast(1)

        for (i in 0 until numSamples) {
            val angle = 2.0 * PI * frequency * i / sampleRate
            var amplitude = 0.55

            if (i < attackSamples) {
                amplitude *= i.toDouble() / attackSamples
            }
            if (i > numSamples - releaseSamples) {
                amplitude *= (numSamples - i).toDouble() / releaseSamples
            }

            buffer[i] = (amplitude * Short.MAX_VALUE * sin(angle)).toInt().toShort()
        }

        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(buffer.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        try {
            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            Thread.sleep(durationMs.toLong())
        } finally {
            audioTrack.stop()
            audioTrack.release()
        }
    }
}
