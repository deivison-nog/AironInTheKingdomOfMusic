package com.info85.aironmusic.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Custom View that renders a one-octave piano keyboard (C to B) with
 * white and black keys. Highlights notes in correct/error/active states
 * and reports touch events through [OnNotePlayedListener].
 */
class PianoKeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    interface OnNotePlayedListener {
        fun onNotePlayed(note: String)
    }

    var onNotePlayedListener: OnNotePlayedListener? = null

    /** Notes to highlight as the currently expected input (blue). */
    var activeNotes: Set<String> = emptySet()
        set(value) { field = value; invalidate() }

    /** Notes to highlight as correctly entered (green). */
    var correctNotes: Set<String> = emptySet()
        set(value) { field = value; invalidate() }

    /** Notes to highlight as incorrectly entered (red). */
    var errorNotes: Set<String> = emptySet()
        set(value) { field = value; invalidate() }

    private val WHITE_NOTES = listOf("C", "D", "E", "F", "G", "A", "B")

    // Maps black-note name → index of the white key to its left
    private val BLACK_NOTE_POSITIONS = mapOf(
        "C#" to 0, "D#" to 1, "F#" to 3, "G#" to 4, "A#" to 5
    )

    // Paints
    private val whitePaint  = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; style = Paint.Style.FILL }
    private val blackPaint  = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1a1a2e"); style = Paint.Style.FILL }
    private val activePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#4cc9f0"); style = Paint.Style.FILL }
    private val correctPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#4ade80"); style = Paint.Style.FILL }
    private val errorPaint  = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#f87171"); style = Paint.Style.FILL }
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#555555"); style = Paint.Style.STROKE; strokeWidth = 1.5f
    }
    private val labelPaintDark = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#222222"); textAlign = Paint.Align.CENTER; textSize = 26f
    }
    private val labelPaintLight = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE; textAlign = Paint.Align.CENTER; textSize = 22f
    }

    private val whiteKeyRects = mutableMapOf<String, RectF>()
    private val blackKeyRects = mutableMapOf<String, RectF>()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        buildKeyRects(w.toFloat(), h.toFloat())
    }

    private fun buildKeyRects(w: Float, h: Float) {
        whiteKeyRects.clear()
        blackKeyRects.clear()

        val keyW = w / WHITE_NOTES.size
        val blackH = h * 0.60f
        val blackW = keyW * 0.58f

        WHITE_NOTES.forEachIndexed { i, note ->
            whiteKeyRects[note] = RectF(i * keyW + 1f, 0f, (i + 1) * keyW - 1f, h)
        }

        BLACK_NOTE_POSITIONS.forEach { (note, leftWhiteIndex) ->
            val x = (leftWhiteIndex + 1) * keyW - blackW / 2f
            blackKeyRects[note] = RectF(x, 0f, x + blackW, blackH)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw white keys first
        WHITE_NOTES.forEach { note ->
            val rect = whiteKeyRects[note] ?: return@forEach
            val paint = when (note) {
                in correctNotes -> correctPaint
                in errorNotes   -> errorPaint
                in activeNotes  -> activePaint
                else            -> whitePaint
            }
            canvas.drawRoundRect(rect, 8f, 8f, paint)
            canvas.drawRoundRect(rect, 8f, 8f, borderPaint)

            val lp = if (note in correctNotes || note in errorNotes || note in activeNotes)
                labelPaintLight else labelPaintDark
            canvas.drawText(note, rect.centerX(), rect.bottom - 18f, lp)
        }

        // Draw black keys on top
        BLACK_NOTE_POSITIONS.keys.forEach { note ->
            val rect = blackKeyRects[note] ?: return@forEach
            val paint = when (note) {
                in correctNotes -> correctPaint
                in errorNotes   -> errorPaint
                in activeNotes  -> activePaint
                else            -> blackPaint
            }
            canvas.drawRoundRect(rect, 6f, 6f, paint)
            canvas.drawText(note, rect.centerX(), rect.bottom - 10f, labelPaintLight)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN ||
            event.action == MotionEvent.ACTION_POINTER_DOWN
        ) {
            val x = event.x
            val y = event.y

            // Black keys are drawn on top → check them first
            for ((note, rect) in blackKeyRects) {
                if (rect.contains(x, y)) {
                    onNotePlayedListener?.onNotePlayed(note)
                    return true
                }
            }
            for ((note, rect) in whiteKeyRects) {
                if (rect.contains(x, y)) {
                    onNotePlayedListener?.onNotePlayed(note)
                    return true
                }
            }
        }
        return true
    }
}
