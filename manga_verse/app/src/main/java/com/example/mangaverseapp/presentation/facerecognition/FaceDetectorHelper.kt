package com.example.mangaverseapp.presentation.facerecognition

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.SystemClock
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facedetector.FaceDetector
import com.google.mediapipe.tasks.vision.facedetector.FaceDetectorResult

class FaceDetectorHelper(
    var threshold: Float = THRESHOLD_DEFAULT,
    var currentDelegate: Int = DELEGATE_CPU,
    var runningMode: RunningMode = RunningMode.IMAGE,
    val context: Context,
    // The listener is only used when running in RunningMode.LIVE_STREAM
    var faceDetectorListener: DetectorListener? = null
) {

    // For this example this needs to be a var so it can be reset on changes. If the faceDetector
    // will not change, a lazy val would be preferable.
    private var faceDetector: FaceDetector? = null

    init {
        setupFaceDetector()
    }

    fun clearFaceDetector() {
        faceDetector?.close()
        faceDetector = null
    }

    // Initialize the face detector using current settings on the
    // thread that is using it. CPU can be used with detectors
    // that are created on the main thread and used on a background thread, but
    // the GPU delegate needs to be used on the thread that initialized the detector
    fun setupFaceDetector() {
        Log.d(TAG, "Setting up face detector")

        // Set general detection options, including number of used threads
        val baseOptionsBuilder = BaseOptions.builder()

        // Use the specified hardware for running the model. Default to CPU
        when (currentDelegate) {
            DELEGATE_CPU -> {
                Log.d(TAG, "Using CPU delegate")
                baseOptionsBuilder.setDelegate(Delegate.CPU)
            }
            DELEGATE_GPU -> {
                Log.d(TAG, "Using GPU delegate")
                // Is there a check for GPU being supported?
                baseOptionsBuilder.setDelegate(Delegate.GPU)
            }
        }

        val modelName = "blaze_face_short_range.tflite"

        try {
            Log.d(TAG, "Setting model asset path: $modelName")
            baseOptionsBuilder.setModelAssetPath(modelName)
            //"asset:///$modelName"
            Log.d(TAG, "Model asset path set successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting model path: ${e.message}", e)
            faceDetectorListener?.onError("Failed to set model path: ${e.message}")
            return
        }

        // Check if runningMode is consistent with faceDetectorListener
        when (runningMode) {
            RunningMode.LIVE_STREAM -> {
                if (faceDetectorListener == null) {
                    Log.e(TAG, "faceDetectorListener must be set when runningMode is LIVE_STREAM")
                    throw IllegalStateException(
                        "faceDetectorListener must be set when runningMode is LIVE_STREAM."
                    )
                }
            }
            RunningMode.IMAGE,
            RunningMode.VIDEO -> {
                // no-op
                Log.d(TAG, "Using ${runningMode.name} mode")
            }
        }

        try {
            Log.d(TAG, "Building face detector options")
            val optionsBuilder = FaceDetector.FaceDetectorOptions.builder()
                .setBaseOptions(baseOptionsBuilder.build())
                .setMinDetectionConfidence(threshold)

            // Set running mode properly based on what was passed in
            when (runningMode) {
                RunningMode.IMAGE,
                RunningMode.VIDEO -> optionsBuilder.setRunningMode(runningMode)
                RunningMode.LIVE_STREAM ->
                    optionsBuilder.setRunningMode(runningMode)
                        .setResultListener(this::returnLivestreamResult)
                        .setErrorListener(this::returnLivestreamError)
            }

            val options = optionsBuilder.build()

            Log.d(TAG, "Creating face detector from options")
            faceDetector = FaceDetector.createFromOptions(context, options)
            Log.d(TAG, "Face detector created successfully")
        } catch (e: IllegalStateException) {
            Log.e(TAG, "TFLite failed to load model with error: ${e.message}", e)
            faceDetectorListener?.onError(
                "Face detector failed to initialize. See error logs for details"
            )
        } catch (e: RuntimeException) {
            Log.e(TAG, "Face detector failed to load model with error: ${e.message}", e)
            faceDetectorListener?.onError(
                "Face detector failed to initialize. See error logs for details",
                GPU_ERROR
            )
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error initializing face detector: ${e.message}", e)
            faceDetectorListener?.onError(
                "Unexpected error initializing face detector: ${e.message}"
            )
        }
    }

    // Return running status of recognizer helper
    fun isClosed(): Boolean {
        return faceDetector == null
    }

    // Accepts the URI for a video file loaded from the user's gallery and attempts to run
    // face detection inference on the video. This process will evaluate every frame in
    // the video and attach the results to a bundle that will be returned.
    fun detectVideoFile(
        videoUri: Uri,
        inferenceIntervalMs: Long
    ): ResultBundle? {

        if (runningMode != RunningMode.VIDEO) {
            throw IllegalArgumentException(
                "Attempting to call detectVideoFile" +
                        " while not using RunningMode.VIDEO"
            )
        }

        if (faceDetector == null) return null

        // Inference time is the difference between the system time at the start and finish of the
        // process
        val startTime = SystemClock.uptimeMillis()

        var didErrorOccurred = false

        // Load frames from the video and run the face detection model.
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, videoUri)
        val videoLengthMs =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLong()

        // Note: We need to read width/height from frame instead of getting the width/height
        // of the video directly because MediaRetriever returns frames that are smaller than the
        // actual dimension of the video file.
        val firstFrame = retriever.getFrameAtTime(0)
        val width = firstFrame?.width
        val height = firstFrame?.height

        // If the video is invalid, returns a null detection result
        if ((videoLengthMs == null) || (width == null) || (height == null)) return null

        // Next, we'll get one frame every frameInterval ms, then run detection on these frames.
        val resultList = mutableListOf<FaceDetectorResult>()
        val numberOfFrameToRead = videoLengthMs.div(inferenceIntervalMs)

        for (i in 0..numberOfFrameToRead) {
            val timestampMs = i * inferenceIntervalMs // ms

            retriever
                .getFrameAtTime(
                    timestampMs * 1000, // convert from ms to micro-s
                    MediaMetadataRetriever.OPTION_CLOSEST
                )
                ?.let { frame ->
                    // Convert the video frame to ARGB_8888 which is required by the MediaPipe
                    val argb8888Frame =
                        if (frame.config == Bitmap.Config.ARGB_8888) frame
                        else frame.copy(Bitmap.Config.ARGB_8888, false)

                    // Convert the input Bitmap face to an MPImage face to run inference
                    val mpImage = BitmapImageBuilder(argb8888Frame).build()

                    // Run face detection using MediaPipe Face Detector API
                    faceDetector?.detectForVideo(mpImage, timestampMs)
                        ?.let { detectionResult ->
                            resultList.add(detectionResult)
                        }
                        ?: {
                            didErrorOccurred = true
                            faceDetectorListener?.onError(
                                "ResultBundle could not be returned" +
                                        " in detectVideoFile"
                            )
                        }
                }
                ?: run {
                    didErrorOccurred = true
                    faceDetectorListener?.onError(
                        "Frame at specified time could not be" +
                                " retrieved when detecting in video."
                    )
                }
        }

        retriever.release()

        val inferenceTimePerFrameMs =
            (SystemClock.uptimeMillis() - startTime).div(numberOfFrameToRead)

        return if (didErrorOccurred) {
            null
        } else {
            ResultBundle(resultList, inferenceTimePerFrameMs, height, width)
        }
    }

    // Runs face detection on live streaming cameras frame-by-frame and returns the results
    // asynchronously to the caller.
    fun detectLivestreamFrame(imageProxy: ImageProxy) {
        if (runningMode != RunningMode.LIVE_STREAM) {
            throw IllegalArgumentException(
                "Attempting to call detectLivestreamFrame" +
                        " while not using RunningMode.LIVE_STREAM"
            )
        }

        if (faceDetector == null) {
            Log.e(TAG, "Face detector is null when attempting to process frame")
            imageProxy.close()
            return
        }

        val frameTime = SystemClock.uptimeMillis()

        try {
            // Copy out RGB bits from the frame to a bitmap buffer
            val bitmapBuffer =
                Bitmap.createBitmap(
                    imageProxy.width,
                    imageProxy.height,
                    Bitmap.Config.ARGB_8888
                )
            imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
            imageProxy.close()

            // Rotate the frame received from the camera to be in the same direction as it'll be shown
            val matrix =
                Matrix().apply {
                    postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())

                    // postScale is used here because we're forcing using the front camera lens
                    // This can be set behind a bool if the camera is togglable.
                    // Not using postScale here with the front camera causes the horizontal axis
                    // to be mirrored.
                    postScale(
                        -1f,
                        1f,
                        imageProxy.width.toFloat(),
                        imageProxy.height.toFloat()
                    )
                }

            val rotatedBitmap =
                Bitmap.createBitmap(
                    bitmapBuffer,
                    0,
                    0,
                    bitmapBuffer.width,
                    bitmapBuffer.height,
                    matrix,
                    true
                )

            // Convert the input Bitmap face to an MPImage face to run inference
            val mpImage = BitmapImageBuilder(rotatedBitmap).build()

            detectAsync(mpImage, frameTime)
        } catch (e: Exception) {
            Log.e(TAG, "Error processing camera frame: ${e.message}", e)
            faceDetectorListener?.onError("Error processing camera frame: ${e.message}")
        }
    }

    // Run face detection using MediaPipe Face Detector API
    @VisibleForTesting
    fun detectAsync(mpImage: MPImage, frameTime: Long) {
        try {
            // As we're using running mode LIVE_STREAM, the detection result will be returned in
            // returnLivestreamResult function
            faceDetector?.detectAsync(mpImage, frameTime)
        } catch (e: Exception) {
            Log.e(TAG, "Error during detectAsync: ${e.message}", e)
            faceDetectorListener?.onError("Error during face detection: ${e.message}")
        }
    }

    // Return the detection result to this FaceDetectorHelper's caller
    private fun returnLivestreamResult(
        result: FaceDetectorResult,
        input: MPImage
    ) {
        val finishTimeMs = SystemClock.uptimeMillis()
        val inferenceTime = finishTimeMs - result.timestampMs()

        faceDetectorListener?.onResults(
            ResultBundle(
                listOf(result),
                inferenceTime,
                input.height,
                input.width
            )
        )
    }

    // Return errors thrown during detection to this FaceDetectorHelper's caller
    private fun returnLivestreamError(error: RuntimeException) {
        Log.e(TAG, "Face detection error: ${error.message}", error)
        faceDetectorListener?.onError(
            error.message ?: "An unknown error has occurred"
        )
    }

    // Accepted a Bitmap and runs face detection inference on it to return results back
    // to the caller
    fun detectImage(image: Bitmap): ResultBundle? {
        if (runningMode != RunningMode.IMAGE) {
            Log.e(TAG, "Attempting to call detectImage while not using RunningMode.IMAGE, current mode: $runningMode")
            throw IllegalArgumentException(
                "Attempting to call detectImage" +
                        " while not using RunningMode.IMAGE"
            )
        }

        if (faceDetector == null) {
            Log.e(TAG, "Face detector is null when attempting to detect image")
            return null
        }

        // Inference time is the difference between the system time at the start and finish of the
        // process
        val startTime = SystemClock.uptimeMillis()

        // Convert the input Bitmap face to an MPImage face to run inference
        val mpImage = BitmapImageBuilder(image).build()

        // Run face detection using MediaPipe Face Detector API
        faceDetector?.detect(mpImage)?.also { detectionResult ->
            val inferenceTimeMs = SystemClock.uptimeMillis() - startTime
            Log.d(TAG, "Face detection successful, found ${detectionResult.detections().size} faces")
            return ResultBundle(
                listOf(detectionResult),
                inferenceTimeMs,
                image.height,
                image.width
            )
        }

        // If faceDetector?.detect() returns null, this is likely an error. Returning null
        // to indicate this.
        Log.e(TAG, "Face detection returned null result")
        return null
    }

    // Wraps results from inference, the time it takes for inference to be performed, and
    // the input image and height for properly scaling UI to return back to callers
    data class ResultBundle(
        val results: List<FaceDetectorResult>,
        val inferenceTime: Long,
        val inputImageHeight: Int,
        val inputImageWidth: Int,
    )

    companion object {
        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
        const val THRESHOLD_DEFAULT = 0.5F
        const val OTHER_ERROR = 0
        const val GPU_ERROR = 1

        const val TAG = "FaceDetectorHelper"
    }

    // Used to pass results or errors back to the calling class
    interface DetectorListener {
        fun onError(error: String, errorCode: Int = OTHER_ERROR)
        fun onResults(resultBundle: ResultBundle)
    }
}