package com.lernki.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.lernki.app.data.remote.ImagePayload
import java.io.ByteArrayOutputStream

object ImageUtils {

    private const val MAX_DIMENSION = 1600

    fun loadBitmap(context: Context, uri: Uri): Bitmap? {
        context.contentResolver.openInputStream(uri)?.use { input ->
            val original = BitmapFactory.decodeStream(input) ?: return null
            return downscale(original)
        }
        return null
    }

    private fun downscale(bitmap: Bitmap): Bitmap {
        val maxSide = maxOf(bitmap.width, bitmap.height)
        if (maxSide <= MAX_DIMENSION) return bitmap
        val scale = MAX_DIMENSION.toFloat() / maxSide
        val newWidth = (bitmap.width * scale).toInt()
        val newHeight = (bitmap.height * scale).toInt()
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    fun bitmapToPayload(bitmap: Bitmap, quality: Int = 85): ImagePayload {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        val base64 = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
        return ImagePayload(base64 = base64, mimeType = "image/jpeg")
    }
}
