package com.example.mangaverseapp.presentation.facerecognition

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.mangaverseapp.R
import com.google.mediapipe.tasks.vision.facedetector.FaceDetectorResult
import kotlin.math.min

class OverlayView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {

    private var results: FaceDetectorResult? = null
    private var boxPaint = Paint()
    private var textBackgroundPaint = Paint()
    private var textPaint = Paint()

    private var scaleFactor: Float = 1f
    private var bounds = Rect()

    // Add a flag to track if a face is detected
    private var isFaceDetected = false

    // Define colors for when face is detected and not detected
    private val faceDetectedColor = Color.GREEN
    private val noFaceDetectedColor = Color.RED

    init {
        initPaints()
    }

    fun clear() {
        results = null
        textPaint.reset()
        textBackgroundPaint.reset()
        boxPaint.reset()
        invalidate()
        initPaints()
    }

    private fun initPaints() {
        textBackgroundPaint.color = Color.BLACK
        textBackgroundPaint.style = Paint.Style.FILL
        textBackgroundPaint.textSize = 50f

        textPaint.color = Color.WHITE
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 50f

        // Initialize with no face detected color (red)
        boxPaint.color = noFaceDetectedColor
        boxPaint.strokeWidth = 8F
        boxPaint.style = Paint.Style.STROKE
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        // Check if we have detection results
        if (results != null) {
            val detections = results!!.detections()

            // Update face detection status
            isFaceDetected = detections.isNotEmpty()

            // Update rectangle color based on face detection status
            boxPaint.color = if (isFaceDetected) faceDetectedColor else noFaceDetectedColor

            // If faces are detected, draw bounding boxes around them
            if (isFaceDetected) {
                for (detection in detections) {
                    val boundingBox = detection.boundingBox()

                    val top = boundingBox.top * scaleFactor
                    val bottom = boundingBox.bottom * scaleFactor
                    val left = boundingBox.left * scaleFactor
                    val right = boundingBox.right * scaleFactor

                    // Draw bounding box around detected faces
                    val drawableRect = RectF(left, top, right, bottom)
                    canvas.drawRect(drawableRect, boxPaint)

                    // Create text to display alongside detected faces
                    val drawableText =
                        detection.categories()[0].categoryName() +
                                " " +
                                String.format(
                                    "%.2f",
                                    detection.categories()[0].score()
                                )

                    // Draw rect behind display text
                    textBackgroundPaint.getTextBounds(
                        drawableText,
                        0,
                        drawableText.length,
                        bounds
                    )
                    val textWidth = bounds.width()
                    val textHeight = bounds.height()
                    canvas.drawRect(
                        left,
                        top,
                        left + textWidth + Companion.BOUNDING_RECT_TEXT_PADDING,
                        top + textHeight + Companion.BOUNDING_RECT_TEXT_PADDING,
                        textBackgroundPaint
                    )

                    // Draw text for detected face
                    canvas.drawText(
                        drawableText,
                        left,
                        top + bounds.height(),
                        textPaint
                    )
                }
            } else {
                // No face detected - draw a red rectangle in the center
                val centerX = width / 2f
                val centerY = height / 2f
                val rectWidth = width * 0.5f
                val rectHeight = height * 0.5f

                val drawableRect = RectF(
                    centerX - rectWidth / 2,
                    centerY - rectHeight / 2,
                    centerX + rectWidth / 2,
                    centerY + rectHeight / 2
                )
                canvas.drawRect(drawableRect, boxPaint)
            }
        } else {
            // No results yet - draw a default red rectangle
            boxPaint.color = noFaceDetectedColor
            val centerX = width / 2f
            val centerY = height / 2f
            val rectWidth = width * 0.5f
            val rectHeight = height * 0.5f

            val drawableRect = RectF(
                centerX - rectWidth / 2,
                centerY - rectHeight / 2,
                centerX + rectWidth / 2,
                centerY + rectHeight / 2
            )
            canvas.drawRect(drawableRect, boxPaint)
        }
    }

    fun setResults(
        detectionResults: FaceDetectorResult,
        imageHeight: Int,
        imageWidth: Int,
    ) {
        results = detectionResults

        // Images, videos and camera live streams are displayed in FIT_START mode. So we need to scale
        // up the bounding box to match with the size that the images/videos/live streams being
        // displayed.
        scaleFactor = min(width * 1f / imageWidth, height * 1f / imageHeight)

        invalidate()
    }

    // Function to check if a face is currently detected (can be used externally)
    fun isFaceDetected(): Boolean {
        return isFaceDetected
    }

    companion object {
        private const val BOUNDING_RECT_TEXT_PADDING = 8
    }
}