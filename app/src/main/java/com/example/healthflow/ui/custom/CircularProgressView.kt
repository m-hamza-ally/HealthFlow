package com.example.healthflow.ui.custom

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import com.example.healthflow.R

/**
 * Custom view for circular progress indicator
 * Progress bar is blue in light mode and black in dark mode
 */
class CircularProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var progress = 0f
    private val maxProgress = 100f

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 20f
        strokeCap = Paint.Cap.ROUND
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 20f
        strokeCap = Paint.Cap.ROUND
    }

    private val rectF = RectF()
    private val startAngle = -90f // Start from top

    init {
        updateColors()
    }

    /**
     * Update both background and progress colors based on theme
     */
    private fun updateColors() {
        // Check if dark mode is enabled
        val isDarkMode = (context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        // Background color (gray)
        backgroundPaint.color = if (isDarkMode) {
            ContextCompat.getColor(context, R.color.gray_medium) // Visible in dark mode
        } else {
            ContextCompat.getColor(context, R.color.gray_light) // Light mode
        }

        // Progress color - BLUE in light mode, BLACK in dark mode
        progressPaint.color = if (isDarkMode) {
            ContextCompat.getColor(context, android.R.color.black) // Black in dark mode
        } else {
            ContextCompat.getColor(context, R.color.primary_blue) // Blue in light mode
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        updateColors()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        // Update colors when theme changes
        updateColors()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val padding = backgroundPaint.strokeWidth / 2
        rectF.set(
            padding,
            padding,
            width - padding,
            height - padding
        )

        // Draw background circle
        canvas.drawArc(rectF, 0f, 360f, false, backgroundPaint)

        // Draw progress arc
        val sweepAngle = (progress / maxProgress) * 360f
        canvas.drawArc(rectF, startAngle, sweepAngle, false, progressPaint)
    }

    /**
     * Set progress with animation
     */
    fun setProgress(targetProgress: Float, animate: Boolean = true) {
        if (animate) {
            val animator = ValueAnimator.ofFloat(progress, targetProgress)
            animator.duration = 1000
            animator.interpolator = DecelerateInterpolator()
            animator.addUpdateListener { animation ->
                progress = animation.animatedValue as Float
                invalidate()
            }
            animator.start()
        } else {
            progress = targetProgress
            invalidate()
        }
    }

    /**
     * Get current progress
     */
    fun getProgress(): Float = progress

    /**
     * Set progress color manually (optional)
     */
    fun setProgressColor(color: Int) {
        progressPaint.color = color
        invalidate()
    }

    /**
     * Set background color manually (optional)
     */
    fun setBackgroundCircleColor(color: Int) {
        backgroundPaint.color = color
        invalidate()
    }
}