package com.example.healthmate

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

class LineGraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var labels = emptyList<String>()
    private var values = emptyList<Float>()

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(85, 73, 190)
        strokeWidth = dp(3f)
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(85, 73, 190)
        style = Paint.Style.FILL
    }

    private val innerDotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(235, 237, 246)
        strokeWidth = dp(1f)
        style = Paint.Style.STROKE
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(120, 130, 155)
        textSize = dp(10f)
        textAlign = Paint.Align.CENTER
    }

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(25, 85, 73, 190)
        style = Paint.Style.FILL
    }

    private fun dp(value: Float): Float {
        return value * resources.displayMetrics.density
    }

    fun setData(
        newLabels: List<String>,
        newValues: List<Float>
    ) {
        labels = newLabels
        values = newValues
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val count = minOf(labels.size, values.size)

        if (count == 0) return

        val left = dp(18f)
        val right = width - dp(18f)
        val top = dp(18f)
        val bottom = height - dp(32f)

        if (right <= left || bottom <= top) return

        val chartWidth = right - left
        val chartHeight = bottom - top

        // Horizontal grid lines
        for (i in 0..3) {
            val y = top + chartHeight * i / 3f

            canvas.drawLine(
                left,
                y,
                right,
                y,
                gridPaint
            )
        }

        val maxValue = max(
            values.take(count).maxOrNull() ?: 0f,
            1f
        ) * 1.2f

        val points = mutableListOf<Pair<Float, Float>>()

        for (i in 0 until count) {

            val x = if (count == 1) {
                (left + right) / 2f
            } else {
                left + chartWidth * i / (count - 1).toFloat()
            }

            val y = bottom -
                    (values[i].coerceAtLeast(0f) / maxValue) * chartHeight

            points.add(x to y)
        }

        // Smooth line path
        val linePath = Path()

        linePath.moveTo(
            points[0].first,
            points[0].second
        )

        for (i in 1 until points.size) {

            val previous = points[i - 1]
            val current = points[i]

            val middleX = (previous.first + current.first) / 2f

            linePath.cubicTo(
                middleX,
                previous.second,
                middleX,
                current.second,
                current.first,
                current.second
            )
        }

        // Area fill
        val fillPath = Path(linePath)

        fillPath.lineTo(
            points.last().first,
            bottom
        )

        fillPath.lineTo(
            points.first().first,
            bottom
        )

        fillPath.close()

        canvas.drawPath(fillPath, fillPaint)

        // Line
        canvas.drawPath(linePath, linePaint)

        // Data point circles
        points.forEach { point ->

            canvas.drawCircle(
                point.first,
                point.second,
                dp(5f),
                dotPaint
            )

            canvas.drawCircle(
                point.first,
                point.second,
                dp(2.5f),
                innerDotPaint
            )
        }

        labels.take(count).forEachIndexed { index, label ->

            canvas.drawText(
                label,
                points[index].first,
                height - dp(9f),
                labelPaint
            )
        }
    }
}
