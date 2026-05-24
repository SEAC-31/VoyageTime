package com.example.voyagetime.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.voyagetime.R
import com.example.voyagetime.ui.viewmodels.TripGalleryViewModel
import com.example.voyagetime.utils.TripImageStorage
import java.time.LocalDate
import kotlinx.coroutines.launch

/**
 * T3.1/T3.2/T3.3 — Galería específica de cada viaje.
 *
 * Permite adjuntar varias imágenes en una sola operación, guarda sus URI persistentes
 * en Room mediante TripImageEntity y muestra únicamente las imágenes vinculadas a este tripId.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripGallery(
    tripId: String,
    tripName: String,
    onBack: () -> Unit,
    viewModel: TripGalleryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val savedImages by viewModel.tripImages.collectAsStateWithLifecycle()

    LaunchedEffect(tripId) {
        viewModel.loadTrip(tripId)
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            coroutineScope.launch {
                val copiedUris = uris.mapIndexedNotNull { index, uri ->
                    TripImageStorage.copyImageToTripFolder(
                        context = context,
                        sourceUri = uri,
                        tripId = tripId,
                        tripName = tripName,
                        prefix = "gallery_${index + 1}"
                    )
                }
                viewModel.attachImages(tripId, copiedUris)
            }
        }
    }

    val galleryItems = savedImages.mapIndexed { index, image ->
        GalleryItem(
            id = image.id.toInt(),
            name = stringResource(R.string.gallery_local_image_name, index + 1),
            location = tripName,
            dateAdded = LocalDate.now(),
            type = "photo",
            isFavorite = false,
            size = stringResource(R.string.gallery_user_uploaded),
            color = Color(0xFF7E57C2),
            imageUri = image.imageUri
        )
    }

    var selectedItem by remember { mutableStateOf<GalleryItem?>(null) }

    if (selectedItem != null) {
        GalleryDetailScreen(
            item = selectedItem!!,
            onBack = { selectedItem = null },
            onDelete = { item ->
                val entity = savedImages.firstOrNull { it.imageUri == item.imageUri }
                if (entity != null) {
                    runCatching { context.contentResolver.delete(Uri.parse(entity.imageUri), null, null) }
                    viewModel.deleteImage(entity)
                }
                selectedItem = null
            }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.trip_gallery_title, tripName),
                            maxLines = 1
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = stringResource(R.string.about_back)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { imagePickerLauncher.launch(arrayOf("image/*")) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.gallery_add),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            ) {
                if (galleryItems.isEmpty()) {
                    TripGalleryEmptyState(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(galleryItems, key = { it.id }) { item ->
                            GalleryGridItem(
                                item = item,
                                onClick = { selectedItem = item }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TripGalleryEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.PhotoLibrary,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f),
            modifier = Modifier.size(72.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.trip_gallery_empty_title),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(R.string.trip_gallery_empty_subtitle),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            textAlign = TextAlign.Center
        )
    }
}
