package com.lernki.app.ui.components

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.lernki.app.util.ImageUtils
import java.io.File

/**
 * Erlaubt das Hochladen eines oder mehrerer Bilder ueber Kamera oder Galerie
 * (Abschnitt 1: "ein Bild hochladen" / "mehrere Bilder gleichzeitig hochladen").
 */
@Composable
fun ImagePickerRow(
    images: List<Bitmap>,
    onImagesAdded: (List<Bitmap>) -> Unit,
    onImageRemoved: (Bitmap) -> Unit
) {
    val context = LocalContext.current
    var pendingCameraFile by remember { mutableStateOf<File?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        val bitmaps = uris.mapNotNull { ImageUtils.loadBitmap(context, it) }
        if (bitmaps.isNotEmpty()) onImagesAdded(bitmaps)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val file = pendingCameraFile
        if (success && file != null) {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            ImageUtils.loadBitmap(context, uri)?.let { onImagesAdded(listOf(it)) }
        }
    }

    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { galleryLauncher.launch("image/*") }) {
                Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null)
                Text(" Bild(er) auswählen", modifier = Modifier.padding(start = 4.dp))
            }
            OutlinedButton(onClick = {
                val imagesDir = File(context.cacheDir, "images").apply { mkdirs() }
                val file = File.createTempFile("capture_", ".jpg", imagesDir)
                pendingCameraFile = file
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                cameraLauncher.launch(uri)
            }) {
                Icon(Icons.Filled.CameraAlt, contentDescription = null)
                Text(" Foto", modifier = Modifier.padding(start = 4.dp))
            }
        }

        if (images.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(images) { bmp ->
                    Box(modifier = Modifier.size(90.dp)) {
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        )
                        IconButton(
                            onClick = { onImageRemoved(bmp) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(24.dp)
                                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Entfernen", tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}
