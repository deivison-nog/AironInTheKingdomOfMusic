package com.info85.aironmusic.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * Renders a simplified treble-clef staff and places note heads for the
 * current challenge. Lets the player read standard musical notation instead
 * of relying on highlighted piano keys.
 *
 * Set [notes] with one note name (SINGLE / SEQUENCE / SCALE — current expected
 * note) or multiple names simultaneously (CHORD — all chord tones stacked).
 *
 * Supported note names: C C# D D# E F F# G G# A A# B
 */
class StaffView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /** Note names to display. Empty list = draw empty staff. */
    var notes: List<String> = emptyList()
        set(value) { field = value; invalidate() }

    // ── Paints ────────────────────────────────────────────────────────────────

    private val staffPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#CCCCCC")
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }
    private val noteFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#F5C518")
        style = Paint.Style.FILL
    }
    private val noteStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#222222")
        strokeWidth = 1.5f
        style = Paint.Style.STROKE
    }
    private val stemPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#CCCCCC")
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }
    private val ledgerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#CCCCCC")
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }
    private val accidentalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#F5C518")
        textAlign = Paint.Align.RIGHT
    }
    private val clefPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#AAAAAA")
        textAlign = Paint.Align.LEFT
    }

    // ── Note → diatonic-step position mapping ─────────────────────────────────
    // Position 0 = bottom staff line (E4 in treble clef).
    // Each integer step = one diatonic step upward (= half a line-spacing).
    // Sharps share the same position as their natural base (accidental drawn separately).
    private val noteSteps = mapOf(
        "C"  to -2, "C#" to -2,
        "D"  to -1, "D#" to -1,
        "E"  to  0,
        "F"  to  1, "F#" to  1,
        "G"  to  2, "G#" to  2,
        "A"  to  3, "A#" to  3,
        "B"  to  4
    )

    // ── Drawing ───────────────────────────────────────────────────────────────

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (width == 0 || height == 0) return

        val w = width.toFloat()
        val h = height.toFloat()

        // Staff geometry
        val lineSpacing = h * 0.14f                           // gap between lines
        val staffBottom = h * 0.74f
        val clefWidth   = w * 0.15f
        val staffLeft   = clefWidth + w * 0.02f
        val staffRight  = w * 0.92f

        // 5 staff lines
        for (i in 0..4) {
            val y = staffBottom - i * lineSpacing
            canvas.drawLine(staffLeft, y, staffRight, y, staffPaint)
        }

        // Treble clef (U+1D11E — supported via Noto Music on API 26+; safely skipped on older fonts)
        clefPaint.textSize = lineSpacing * 5.4f
        val clefChar = "\uD834\uDD1E"
        if (clefPaint.measureText(clefChar) > 1f) {
            canvas.drawText(clefChar, w * 0.01f, staffBottom + lineSpacing * 0.9f, clefPaint)
        }

        if (notes.isEmpty()) return

        val halfStep = lineSpacing / 2f
        val noteRx   = halfStep * 1.40f   // oval half-width
        val noteRy   = halfStep * 0.90f   // oval half-height

        // X position for the note(s) — centred in the staff drawing area
        val noteX = staffLeft + (staffRight - staffLeft) * 0.45f

        notes.forEachIndexed { idx, rawNote ->
            val step    = noteSteps[rawNote] ?: return@forEachIndexed
            val isSharp = '#' in rawNote

            // Offset horizontally if this note is a 2nd (step difference < 2) relative
            // to the previous one, to avoid overlapping heads (standard chord notation).
            val xOff = if (idx > 0) {
                val prevStep = noteSteps[notes[idx - 1]] ?: step
                if (kotlin.math.abs(step - prevStep) < 2) noteRx * 2.2f else 0f
            } else 0f
            val nx = noteX + xOff

            val ny = staffBottom - step * halfStep

            // Ledger line for C4 (step -2)
            if (step == -2) {
                canvas.drawLine(nx - noteRx * 1.6f, ny, nx + noteRx * 1.6f, ny, ledgerPaint)
            }

            // Sharp accidental
            if (isSharp) {
                accidentalPaint.textSize = lineSpacing * 0.88f
                canvas.drawText("♯", nx - noteRx * 0.7f, ny + accidentalPaint.textSize * 0.38f, accidentalPaint)
            }

            // Note head (filled oval)
            canvas.drawOval(nx - noteRx, ny - noteRy, nx + noteRx, ny + noteRy, noteFillPaint)
            canvas.drawOval(nx - noteRx, ny - noteRy, nx + noteRx, ny + noteRy, noteStrokePaint)

            // Stem: upward when note is below the middle line (step < 2), downward otherwise
            val stemX    = if (step < 2) nx + noteRx - 1f else nx - noteRx + 1f
            val stemEndY = if (step < 2) ny - lineSpacing * 3.2f else ny + lineSpacing * 3.2f
            canvas.drawLine(stemX, ny, stemX, stemEndY, stemPaint)
        }
    }
}
