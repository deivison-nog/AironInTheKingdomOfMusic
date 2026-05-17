package com.info85.aironmusic.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.info85.aironmusic.model.Region
import kotlin.math.hypot
import kotlin.math.min

class KingdomMapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    data class MapNode(
        val region: Region,
        val xFraction: Float,
        val yFraction: Float
    )

    var onRegionTap: ((Region, Boolean) -> Unit)? = null

    var selectedRegionId: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    private var clearedCount: Int = 0
    private var nodes: List<MapNode> = emptyList()

    private val frameOuterPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#bb3434")
        style = Paint.Style.STROKE
        strokeWidth = dp(5f)
    }
    private val frameInnerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#213f8f")
        style = Paint.Style.STROKE
        strokeWidth = dp(3f)
    }
    private val oceanPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#80add7")
        style = Paint.Style.FILL
    }
    private val shallowWaterPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#9ec6e2")
        style = Paint.Style.FILL
    }
    private val islandShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#6a1518")
        style = Paint.Style.FILL
    }
    private val islandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#d6e6b0")
        style = Paint.Style.FILL
    }
    private val grassPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#9fc46a")
        style = Paint.Style.FILL
    }
    private val forestPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#49633d")
        style = Paint.Style.FILL
    }
    private val markerStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#271410")
        style = Paint.Style.STROKE
        strokeWidth = dp(2f)
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#f8f1de")
        textAlign = Paint.Align.CENTER
    }
    private val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }
    private val statusPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }
    private val compassPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#ef4c3a")
        style = Paint.Style.STROKE
        strokeWidth = dp(1.6f)
        textAlign = Paint.Align.CENTER
    }
    private val compassFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#ef4c3a")
        style = Paint.Style.FILL
    }

    fun setRegions(regions: List<Region>, clearedCount: Int) {
        val points = listOf(
            0.30f to 0.31f,
            0.39f to 0.53f,
            0.24f to 0.60f,
            0.58f to 0.26f,
            0.50f to 0.74f,
            0.71f to 0.54f
        )
        this.nodes = regions.mapIndexed { index, region ->
            val point = points.getOrElse(index) { 0.5f to 0.5f }
            MapNode(region, point.first, point.second)
        }
        this.clearedCount = clearedCount
        invalidate()
    }

    fun updateClearedCount(newCount: Int) {
        clearedCount = newCount
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (width == 0 || height == 0) return

        val w = width.toFloat()
        val h = height.toFloat()
        val mapRect = RectF(dp(12f), dp(12f), w - dp(12f), h - dp(12f))
        val oceanRect = RectF(
            mapRect.left + dp(8f),
            mapRect.top + dp(8f),
            mapRect.right - dp(8f),
            mapRect.bottom - dp(8f)
        )

        canvas.drawColor(Color.parseColor("#f0d6ca"))
        canvas.drawRect(oceanRect, oceanPaint)
        drawPixelWater(canvas, oceanRect)
        drawShallowWater(canvas, oceanRect)
        drawIsland(canvas, oceanRect)
        drawLandDetails(canvas, oceanRect)
        drawCompass(canvas, oceanRect)
        drawMarkers(canvas, oceanRect)
        canvas.drawRect(mapRect, frameOuterPaint)
        canvas.drawRect(
            mapRect.left + dp(6f),
            mapRect.top + dp(6f),
            mapRect.right - dp(6f),
            mapRect.bottom - dp(6f),
            frameInnerPaint
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_UP) return true
        val hitNode = findHitNode(event.x, event.y) ?: return true
        val isUnlocked = hitNode.region.id <= clearedCount
        selectedRegionId = hitNode.region.id
        onRegionTap?.invoke(hitNode.region, isUnlocked)
        return true
    }

    private fun findHitNode(x: Float, y: Float): MapNode? {
        val oceanRect = RectF(dp(20f), dp(20f), width - dp(20f), height - dp(20f))
        val markerRadius = min(oceanRect.width(), oceanRect.height()) * 0.05f
        return nodes.firstOrNull { node ->
            val cx = oceanRect.left + oceanRect.width() * node.xFraction
            val cy = oceanRect.top + oceanRect.height() * node.yFraction
            hypot((x - cx).toDouble(), (y - cy).toDouble()) <= markerRadius * 1.35f
        }
    }

    private fun drawPixelWater(canvas: Canvas, rect: RectF) {
        val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
        }
        val step = min(rect.width(), rect.height()) / 18f
        val size = step * 0.18f
        var row = 0
        var y = rect.top + step * 0.4f
        while (y < rect.bottom - step * 0.3f) {
            var x = rect.left + if (row % 2 == 0) step * 0.25f else step * 0.7f
            while (x < rect.right - step * 0.3f) {
                dotPaint.color = if ((row + (x / step).toInt()) % 4 == 0) {
                    Color.parseColor("#b1d6d6")
                } else {
                    Color.parseColor("#f5e2df")
                }
                canvas.drawRect(x, y, x + size, y + size, dotPaint)
                x += step
            }
            row++
            y += step * 0.74f
        }
    }

    private fun drawShallowWater(canvas: Canvas, rect: RectF) {
        val path = Path().apply {
            moveTo(rect.left + rect.width() * 0.12f, rect.top + rect.height() * 0.14f)
            lineTo(rect.left + rect.width() * 0.22f, rect.top + rect.height() * 0.37f)
            lineTo(rect.left + rect.width() * 0.35f, rect.top + rect.height() * 0.52f)
            lineTo(rect.left + rect.width() * 0.22f, rect.top + rect.height() * 0.70f)
            lineTo(rect.left + rect.width() * 0.40f, rect.top + rect.height() * 0.88f)
            lineTo(rect.left + rect.width() * 0.66f, rect.top + rect.height() * 0.88f)
            lineTo(rect.left + rect.width() * 0.86f, rect.top + rect.height() * 0.74f)
            lineTo(rect.left + rect.width() * 0.80f, rect.top + rect.height() * 0.56f)
            lineTo(rect.left + rect.width() * 0.74f, rect.top + rect.height() * 0.44f)
            lineTo(rect.left + rect.width() * 0.65f, rect.top + rect.height() * 0.28f)
            lineTo(rect.left + rect.width() * 0.50f, rect.top + rect.height() * 0.18f)
            close()
        }
        canvas.drawPath(path, shallowWaterPaint)
    }

    private fun drawIsland(canvas: Canvas, rect: RectF) {
        val islandPath = islandPath(rect, 0f)
        val shadowPath = islandPath(rect, dp(5f))
        canvas.drawPath(shadowPath, islandShadowPaint)
        canvas.drawPath(islandPath, islandPaint)

        val chunk = min(rect.width(), rect.height()) * 0.014f
        var y = rect.top + rect.height() * 0.20f
        var row = 0
        while (y < rect.bottom - rect.height() * 0.12f) {
            var x = rect.left + rect.width() * 0.16f + if (row % 2 == 0) 0f else chunk
            while (x < rect.right - rect.width() * 0.16f) {
                if (isPointInsideIsland(x, y, rect)) {
                    canvas.drawRect(x, y, x + chunk * 0.55f, y + chunk * 0.55f, grassPaint)
                }
                x += chunk * 2.1f
            }
            row++
            y += chunk * 1.7f
        }
    }

    private fun drawLandDetails(canvas: Canvas, rect: RectF) {
        drawVolcano(canvas, rect, rect.left + rect.width() * 0.29f, rect.top + rect.height() * 0.37f)
        drawCastle(canvas, rect, rect.left + rect.width() * 0.58f, rect.top + rect.height() * 0.26f)
        drawLake(canvas, rect, rect.left + rect.width() * 0.23f, rect.top + rect.height() * 0.60f)
        drawTower(canvas, rect, rect.left + rect.width() * 0.50f, rect.top + rect.height() * 0.74f)
        drawVortex(canvas, rect, rect.left + rect.width() * 0.71f, rect.top + rect.height() * 0.54f)

        val treeSpots = listOf(
            0.33f to 0.48f, 0.40f to 0.48f, 0.47f to 0.48f,
            0.36f to 0.60f, 0.44f to 0.60f, 0.52f to 0.60f,
            0.45f to 0.39f, 0.54f to 0.39f
        )
        treeSpots.forEach { (fx, fy) ->
            drawTree(canvas, rect.left + rect.width() * fx, rect.top + rect.height() * fy)
        }
    }

    private fun drawMarkers(canvas: Canvas, rect: RectF) {
        val radius = min(rect.width(), rect.height()) * 0.05f
        labelPaint.textSize = radius * 0.46f
        iconPaint.textSize = radius * 0.82f
        statusPaint.textSize = radius * 0.48f

        nodes.forEach { node ->
            val cx = rect.left + rect.width() * node.xFraction
            val cy = rect.top + rect.height() * node.yFraction
            val isUnlocked = node.region.id <= clearedCount
            val isCleared = node.region.id < clearedCount
            val isSelected = node.region.id == selectedRegionId
            val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = when {
                    !isUnlocked -> Color.parseColor("#6c7086")
                    isSelected -> Color.parseColor(node.region.colorHex)
                    else -> Color.parseColor("#f2dfc8")
                }
                alpha = if (isUnlocked) 255 else 175
                style = Paint.Style.FILL
            }

            if (isSelected) {
                val haloPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.parseColor("#fff0a8")
                    style = Paint.Style.STROKE
                    strokeWidth = dp(3f)
                }
                canvas.drawCircle(cx, cy, radius * 1.34f, haloPaint)
            }

            canvas.drawCircle(cx, cy, radius, fillPaint)
            canvas.drawCircle(cx, cy, radius, markerStrokePaint)
            iconPaint.color = if (isUnlocked) Color.parseColor("#161625") else Color.parseColor("#ececf8")
            canvas.drawText(
                if (isUnlocked) node.region.icon else "🔒",
                cx,
                cy + radius * 0.28f,
                iconPaint
            )

            if (isCleared) {
                statusPaint.color = Color.parseColor("#ffd44d")
                canvas.drawText("★", cx + radius * 0.88f, cy - radius * 0.62f, statusPaint)
            } else if (!isUnlocked) {
                statusPaint.color = Color.parseColor("#ececf8")
                canvas.drawText("?", cx + radius * 0.80f, cy - radius * 0.62f, statusPaint)
            }

            labelPaint.color = Color.parseColor("#1f2b55")
            canvas.drawText(
                "${node.region.id + 1}. ${shortLabel(node.region.name)}",
                cx,
                cy + radius * 1.55f,
                labelPaint
            )
        }
    }

    private fun drawCompass(canvas: Canvas, rect: RectF) {
        val cx = rect.right - rect.width() * 0.12f
        val cy = rect.bottom - rect.height() * 0.12f
        val radius = min(rect.width(), rect.height()) * 0.07f
        canvas.drawCircle(cx, cy, radius, compassPaint)
        canvas.drawCircle(cx, cy, radius * 0.75f, compassPaint)
        canvas.drawLine(cx - radius, cy, cx + radius, cy, compassPaint)
        canvas.drawLine(cx, cy - radius, cx, cy + radius, compassPaint)
        val rose = Path().apply {
            moveTo(cx, cy - radius)
            lineTo(cx + radius * 0.18f, cy - radius * 0.18f)
            lineTo(cx, cy - radius * 0.40f)
            lineTo(cx - radius * 0.18f, cy - radius * 0.18f)
            close()
            moveTo(cx + radius, cy)
            lineTo(cx + radius * 0.18f, cy + radius * 0.18f)
            lineTo(cx + radius * 0.40f, cy)
            lineTo(cx + radius * 0.18f, cy - radius * 0.18f)
            close()
            moveTo(cx, cy + radius)
            lineTo(cx + radius * 0.18f, cy + radius * 0.18f)
            lineTo(cx, cy + radius * 0.40f)
            lineTo(cx - radius * 0.18f, cy + radius * 0.18f)
            close()
            moveTo(cx - radius, cy)
            lineTo(cx - radius * 0.18f, cy + radius * 0.18f)
            lineTo(cx - radius * 0.40f, cy)
            lineTo(cx - radius * 0.18f, cy - radius * 0.18f)
            close()
        }
        canvas.drawPath(rose, compassFillPaint)
        compassPaint.textSize = radius * 0.42f
        canvas.drawText("N", cx, cy - radius * 1.2f, compassPaint)
        canvas.drawText("S", cx, cy + radius * 1.35f, compassPaint)
        canvas.drawText("O", cx - radius * 1.25f, cy + radius * 0.12f, compassPaint)
        canvas.drawText("L", cx + radius * 1.25f, cy + radius * 0.12f, compassPaint)
    }

    private fun drawTree(canvas: Canvas, x: Float, y: Float) {
        val size = dp(10f)
        val trunkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#6a4a2b")
            style = Paint.Style.FILL
        }
        canvas.drawRect(x - size * 0.08f, y + size * 0.35f, x + size * 0.08f, y + size, trunkPaint)
        canvas.drawRect(x - size * 0.45f, y, x + size * 0.45f, y + size * 0.22f, forestPaint)
        canvas.drawRect(x - size * 0.30f, y + size * 0.18f, x + size * 0.30f, y + size * 0.38f, forestPaint)
    }

    private fun drawVolcano(canvas: Canvas, rect: RectF, x: Float, y: Float) {
        val base = min(rect.width(), rect.height()) * 0.07f
        val mountainPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#f8dfc5")
            style = Paint.Style.FILL
        }
        val lavaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#df6f4a")
            style = Paint.Style.FILL
        }
        val mountain = Path().apply {
            moveTo(x - base, y + base * 0.9f)
            lineTo(x, y - base)
            lineTo(x + base, y + base * 0.9f)
            close()
        }
        val lava = Path().apply {
            moveTo(x, y - base * 0.75f)
            lineTo(x + base * 0.65f, y + base * 0.9f)
            lineTo(x - base * 0.02f, y + base * 0.9f)
            close()
        }
        canvas.drawPath(mountain, mountainPaint)
        canvas.drawPath(lava, lavaPaint)
        canvas.drawPath(mountain, markerStrokePaint)
    }

    private fun drawCastle(canvas: Canvas, rect: RectF, x: Float, y: Float) {
        val size = min(rect.width(), rect.height()) * 0.08f
        val castlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#908ac7")
            style = Paint.Style.FILL
        }
        val towerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#d9d4f3")
            style = Paint.Style.FILL
        }
        canvas.drawRect(x - size * 0.5f, y - size * 0.15f, x + size * 0.5f, y + size * 0.35f, castlePaint)
        canvas.drawRect(x - size * 0.45f, y - size * 0.45f, x - size * 0.2f, y + size * 0.1f, towerPaint)
        canvas.drawRect(x + size * 0.2f, y - size * 0.45f, x + size * 0.45f, y + size * 0.1f, towerPaint)
        canvas.drawRect(x - size * 0.12f, y - size * 0.52f, x + size * 0.12f, y + size * 0.08f, towerPaint)
    }

    private fun drawLake(canvas: Canvas, rect: RectF, x: Float, y: Float) {
        val radius = min(rect.width(), rect.height()) * 0.05f
        val lakePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#5ab2df")
            style = Paint.Style.FILL
        }
        canvas.drawCircle(x, y, radius, lakePaint)
        canvas.drawCircle(x, y, radius, markerStrokePaint)
    }

    private fun drawTower(canvas: Canvas, rect: RectF, x: Float, y: Float) {
        val size = min(rect.width(), rect.height()) * 0.06f
        val towerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#eac971")
            style = Paint.Style.FILL
        }
        canvas.drawRect(x - size * 0.18f, y - size * 0.58f, x + size * 0.18f, y + size * 0.4f, towerPaint)
        canvas.drawRect(x - size * 0.34f, y - size * 0.72f, x + size * 0.34f, y - size * 0.42f, towerPaint)
        canvas.drawRect(x - size * 0.46f, y + size * 0.35f, x + size * 0.46f, y + size * 0.55f, towerPaint)
    }

    private fun drawVortex(canvas: Canvas, rect: RectF, x: Float, y: Float) {
        val radius = min(rect.width(), rect.height()) * 0.05f
        val vortexPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#7147d8")
            style = Paint.Style.STROKE
            strokeWidth = dp(3f)
        }
        canvas.drawCircle(x, y, radius, vortexPaint)
        canvas.drawCircle(x, y, radius * 0.65f, vortexPaint)
        canvas.drawCircle(x, y, radius * 0.30f, vortexPaint)
    }

    private fun islandPath(rect: RectF, offset: Float): Path {
        fun px(fx: Float) = rect.left + rect.width() * fx + offset
        fun py(fy: Float) = rect.top + rect.height() * fy + offset
        return Path().apply {
            moveTo(px(0.24f), py(0.30f))
            lineTo(px(0.28f), py(0.24f))
            lineTo(px(0.43f), py(0.24f))
            lineTo(px(0.43f), py(0.30f))
            lineTo(px(0.58f), py(0.30f))
            lineTo(px(0.58f), py(0.36f))
            lineTo(px(0.68f), py(0.36f))
            lineTo(px(0.68f), py(0.48f))
            lineTo(px(0.76f), py(0.48f))
            lineTo(px(0.76f), py(0.60f))
            lineTo(px(0.84f), py(0.60f))
            lineTo(px(0.84f), py(0.76f))
            lineTo(px(0.70f), py(0.76f))
            lineTo(px(0.70f), py(0.70f))
            lineTo(px(0.40f), py(0.70f))
            lineTo(px(0.40f), py(0.64f))
            lineTo(px(0.46f), py(0.64f))
            lineTo(px(0.46f), py(0.56f))
            lineTo(px(0.34f), py(0.56f))
            lineTo(px(0.34f), py(0.50f))
            lineTo(px(0.26f), py(0.50f))
            lineTo(px(0.26f), py(0.42f))
            lineTo(px(0.18f), py(0.42f))
            lineTo(px(0.18f), py(0.30f))
            close()
        }
    }

    private fun isPointInsideIsland(x: Float, y: Float, rect: RectF): Boolean {
        val region = android.graphics.Region().apply {
            val clip = android.graphics.Region(
                rect.left.toInt(),
                rect.top.toInt(),
                rect.right.toInt(),
                rect.bottom.toInt()
            )
            setPath(islandPath(rect, 0f), clip)
        }
        return region.contains(x.toInt(), y.toInt())
    }

    private fun shortLabel(name: String): String {
        return when {
            name.contains("Floresta", ignoreCase = true) -> "Floresta"
            name.contains("Terras", ignoreCase = true) -> "Vulcão"
            name.contains("Lago", ignoreCase = true) -> "Lago"
            name.contains("Castelo", ignoreCase = true) -> "Castelo"
            name.contains("Torre", ignoreCase = true) -> "Torre"
            name.contains("Domínio", ignoreCase = true) -> "Final"
            else -> name.take(8)
        }
    }

    private fun dp(value: Float): Float = value * resources.displayMetrics.density
}
