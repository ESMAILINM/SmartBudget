package edu.ucne.smartbudget.presentation.meta.componentes

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ImageSelectionGridWithDefaults(
    selectedImageIndex: Int,
    onImageSelected: (Int, String) -> Unit,
    images: List<String>,
    onImagesChanged: (List<String>) -> Unit
) {
    val selectionColor = MaterialTheme.colorScheme.primary
    val context = LocalContext.current
    val contentResolver = context.contentResolver

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            uri?.let {
                try {
                    val takeFlags = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                    contentResolver.takePersistableUriPermission(it, takeFlags)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                val currentList = images.toMutableList()
                if (currentList.size < 4) {
                    currentList.add(it.toString())
                    onImagesChanged(currentList)
                    onImageSelected(currentList.lastIndex, it.toString())
                }
            }
        }
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.heightIn(max = 400.dp)
        ) {
            itemsIndexed(images) { index, imageUri ->
                val isSelected = index == selectedImageIndex

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .border(
                            width = if (isSelected) 3.dp else 0.dp,
                            color = if (isSelected) selectionColor else Color.Transparent,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(if (isSelected) 3.dp else 0.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onImageSelected(index, imageUri) }
                ) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Imagen de la meta",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f))
                            .clickable {
                                val currentList = images.toMutableList()
                                currentList.removeAt(index)
                                onImagesChanged(currentList)

                                if (isSelected && currentList.isNotEmpty()) {
                                    onImageSelected(0, currentList[0])
                                } else if (currentList.isEmpty()) {
                                    onImageSelected(-1, "")
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Eliminar imagen",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (images.size < 4) {
                item {
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .border(
                                border = BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                singlePhotoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar imagen",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
        }
    }
}
