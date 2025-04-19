package com.example.mangaverseapp.domain.model

import android.graphics.RectF

sealed class FaceDetectionState {
    object Initializing : FaceDetectionState()
    object NoFaceDetected : FaceDetectionState()
    data class FaceDetected(val boundingBox: RectF, val isWithinReference: Boolean) : FaceDetectionState()
    data class Error(val message: String) : FaceDetectionState()
}