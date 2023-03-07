package com.feelsoftware.feelfine.ui.statistic

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer

internal class RoundedBarChartRenderer(
    chart: BarChart,
) : BarChartRenderer(chart, chart.animator, chart.viewPortHandler) {

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        val trans = mChart.getTransformer(dataSet.axisDependency)
        mShadowPaint.color = dataSet.barShadowColor
        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY
        if (mBarBuffers != null) {
            // initialize the buffer
            val buffer = mBarBuffers[index]
            buffer.setPhases(phaseX, phaseY)
            buffer.setDataSet(index)
            buffer.setBarWidth(mChart.barData.barWidth)
            buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
            buffer.feed(dataSet)
            trans.pointValuesToPixel(buffer.buffer)

            var j = 0
            while (j < buffer.size()) {
                if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                    j += 4
                    continue
                }
                if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) break
                if (mChart.isDrawBarShadowEnabled) {
                    val mRadius = 0f
                    c.drawRoundRect(
                        RectF(
                            buffer.buffer[j], mViewPortHandler.contentTop(),
                            buffer.buffer[j + 2], mViewPortHandler.contentBottom()
                        ), mRadius, mRadius, mShadowPaint
                    )
                }

                mRenderPaint.color = dataSet.getColor(j / 4)
                val path = roundedRect(
                    left = buffer.buffer[j],
                    top = buffer.buffer[j + 1],
                    right = buffer.buffer[j + 2],
                    bottom = buffer.buffer[j + 3],
                )
                c.drawPath(path, mRenderPaint)
                j += 4
            }
        }
    }

    private fun roundedRect(
        left: Float, top: Float, right: Float, bottom: Float,
        tl: Boolean = true, tr: Boolean = true, br: Boolean = true, bl: Boolean = true,
    ): Path {
        val path = Path()
        val width = right - left
        val height = bottom - top
        val rx = width / 2
        val ry = (width / 2)
            .coerceAtMost(height / 2)
        val widthMinusCorners = width - 2 * rx
        val heightMinusCorners = height - 2 * ry
        path.moveTo(right, top + ry)
        if (tr) path.rQuadTo(0f, -ry, -rx, -ry) //top-right corner
        else {
            path.rLineTo(0f, -ry)
            path.rLineTo(-rx, 0f)
        }
        path.rLineTo(-widthMinusCorners, 0f)
        if (tl) path.rQuadTo(-rx, 0f, -rx, ry) //top-left corner
        else {
            path.rLineTo(-rx, 0f)
            path.rLineTo(0f, ry)
        }
        path.rLineTo(0f, heightMinusCorners)
        if (bl) path.rQuadTo(0f, ry, rx, ry) //bottom-left corner
        else {
            path.rLineTo(0f, ry)
            path.rLineTo(rx, 0f)
        }
        path.rLineTo(widthMinusCorners, 0f)
        if (br) path.rQuadTo(rx, 0f, rx, -ry) //bottom-right corner
        else {
            path.rLineTo(rx, 0f)
            path.rLineTo(0f, -ry)
        }
        path.rLineTo(0f, -heightMinusCorners)
        path.close() //Given close, last lineTo can be removed.
        return path
    }
}