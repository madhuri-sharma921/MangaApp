package com.example.mangaverseapp.utils



import android.content.Context
import android.util.Log
import java.io.IOException


object ModelValidator {
    private const val TAG = "ModelValidator"

   
    fun validateFaceDetectionModel(context: Context): Boolean {
        val possiblePaths = listOf(
            "blaze_face_short_range.tflite",
            "models/blaze_face_short_range.tflite",
            "mediapipe/blaze_face_short_range.tflite"
        )

        for (path in possiblePaths) {
            try {
                val stream = context.assets.open(path)
                stream.close()
                Log.d(TAG, "Face detection model found at: $path")
                return true
            } catch (e: IOException) {
                Log.d(TAG, "Model not found at: $path")
            }
        }

        Log.e(TAG, "❌ Face detection model not found in any of the expected locations")
        return false
    }

   
    fun listAvailableAssets(context: Context): String {
        return try {
            val assetsList = context.assets.list("") ?: emptyArray()
            val assetsString = assetsList.joinToString(", ")

            // Check for subdirectories
            val result = StringBuilder("Root: $assetsString\n")

            // Check if 'models' directory exists
            if (assetsList.contains("models")) {
                val modelsDir = context.assets.list("models") ?: emptyArray()
                result.append("models/: ${modelsDir.joinToString(", ")}\n")
            }

            result.toString()
        } catch (e: IOException) {
            "Error listing assets: ${e.message}"
        }
    }
}
