package com.example.voyagetime.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.voyagetime.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

data class GalleryItem(
    val id: Int,
    val name: String,
    val location: String,
    val dateAdded: LocalDate,
    val type: String,
    val isFavorite: Boolean,
    val size: String,
    val color: Color,
    @DrawableRes val imageRes: Int? = null,
    val imageUri: String? = null
)

val sampleGalleryItems = listOf(
    GalleryItem(1, "Barcelona", "Barcelona, Spain", LocalDate.of(2025, 3, 2), "photo", true, "3.2 MB", Color(0xFFFF7043), R.drawable.barcelona),
    GalleryItem(2, "New York", "New York, USA", LocalDate.of(2025, 1, 15), "photo", false, "2.8 MB", Color(0xFF42A5F5), R.drawable.newyork),
    GalleryItem(3, "Paris", "Paris, France", LocalDate.of(2024, 12, 10), "photo", true, "2.1 MB", Color(0xFFAB47BC), R.drawable.paris),
    GalleryItem(4, "Tokyo", "Tokyo, Japan", LocalDate.of(2024, 11, 5), "photo", false, "4.1 MB", Color(0xFF66BB6A), R.drawable.tokyo)
)

private fun localizedGalleryDate(date: LocalDate): String {
    return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
}

@Composable
private fun localizedGalleryType(type: String): String {
    return if (type.equals("video", ignoreCase = true)) {
        stringResource(R.string.gallery_type_video)
    } else {
        stringResource(R.string.gallery_type_photo)
    }
}

@Composable
fun Gallery(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var uploadedUris by remember {
        mutableStateOf(PreferencesManager.getGalleryImageUris(context))
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            PreferencesManager.addGalleryImageUri(context, uri.toString())
            uploadedUris = PreferencesManager.getGalleryImageUris(context)
        }
    }

    val uploadedItems = uploadedUris.mapIndexed { index, uri ->
        GalleryItem(
            id = 10_000 + index,
            name = stringResource(R.string.gallery_local_image_name, index + 1),
            location = stringResource(R.string.gallery_local_location),
            dateAdded = LocalDate.now(),
            type = "photo",
            isFavorite = false,
            size = stringResource(R.string.gallery_user_uploaded),
            color = Color(0xFF7E57C2),
            imageUri = uri
        )
    }

    val allItems = sampleGalleryItems + uploadedItems

    var selectedItem by remember { mutableStateOf<GalleryItem?>(null) }
    var selectedFilter by remember { mutableStateOf("all") }
    var sortExpanded by remember { mutableStateOf(false) }

    val sortDateLabel = stringResource(R.string.gallery_sort_date)
    val sortNameLabel = stringResource(R.string.gallery_sort_name)
    val sortSizeLabel = stringResource(R.string.gallery_sort_size)
    val sortLocationLabel = stringResource(R.string.gallery_sort_location)
    var selectedSort by remember { mutableStateOf(sortDateLabel) }
    val sortOptions = listOf(sortDateLabel, sortNameLabel, sortSizeLabel, sortLocationLabel)

    val filteredItems = when (selectedFilter) {
        "favorites" -> allItems.filter { it.isFavorite }
        "recent" -> allItems.sortedByDescending { it.dateAdded }.take(6)
        else -> allItems
    }

    if (selectedItem != null) {
        GalleryDetailScreen(
            item = selectedItem!!,
            onBack = { selectedItem = null },
            onDelete = { item ->
                item.imageUri?.let { uri ->
                    PreferencesManager.removeGalleryImageUri(context, uri)
                    uploadedUris = PreferencesManager.getGalleryImageUris(context)
                }
                selectedItem = null
            }
        )
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = stringResource(R.string.gallery_title),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (MaterialTheme.colorScheme.background.red < 0.5f) {
                        Color.White
                    } else {
                        Color(0xFF111111)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChipItem(
                        label = stringResource(R.string.gallery_filter_all),
                        selected = selectedFilter == "all",
                        onClick = { selectedFilter = "all" }
                    )

                    FilterChipItem(
                        label = stringResource(R.string.gallery_filter_favorites),
                        selected = selectedFilter == "favorites",
                        onClick = { selectedFilter = "favorites" },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    )

                    FilterChipItem(
                        label = stringResource(R.string.gallery_filter_recent),
                        selected = selectedFilter == "recent",
                        onClick = { selectedFilter = "recent" }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.gallery_items_count, filteredItems.size),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
                        fontWeight = FontWeight.Medium
                    )

                    Box {
                        Row(
                            modifier = Modifier
                                .clickable { sortExpanded = true }
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.gallery_sort_prefix) + selectedSort,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        DropdownMenu(
                            expanded = sortExpanded,
                            onDismissRequest = { sortExpanded = false }
                        ) {
                            sortOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedSort = option
                                        sortExpanded = false
                                    },
                                    leadingIcon = if (selectedSort == option) {
                                        {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    } else {
                                        null
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredItems) { item ->
                        GalleryGridItem(
                            item = item,
                            onClick = { selectedItem = item }
                        )
                    }
                }
            }

            FloatingActionButton(
                onClick = {
                    imagePickerLauncher.launch(arrayOf("image/*"))
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
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
    }
}

@Composable
fun GalleryGridItem(
    item: GalleryItem,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() }
    ) {
        GalleryImage(
            item = item,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
                    )
                )
                .padding(4.dp)
        ) {
            Text(
                text = item.location.split(",")[0],
                fontSize = 9.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (item.type == "video") {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        if (item.isFavorite) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFCA28),
                    modifier = Modifier.size(11.dp)
                )
            }
        }
    }
}

@Composable
private fun GalleryImage(
    item: GalleryItem,
    modifier: Modifier = Modifier
) {
    when {
        item.imageUri != null -> {
            AsyncImage(
                model = item.imageUri,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = modifier
            )
        }

        item.imageRes != null -> {
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = modifier
            )
        }

        else -> {
            Box(
                modifier = modifier.background(
                    Brush.verticalGradient(
                        colors = listOf(item.color.copy(alpha = 0.6f), item.color)
                    )
                )
            )
        }
    }
}

@Composable
fun FilterChipItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val bgColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    }

    val textColor = if (selected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (leadingIcon != null) {
            CompositionLocalProvider(LocalContentColor provides textColor) {
                leadingIcon()
            }
        }

        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = textColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryDetailScreen(
    item: GalleryItem,
    onBack: () -> Unit,
    onDelete: (GalleryItem) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = item.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                GalleryImage(
                    item = item,
                    modifier = Modifier.fillMaxSize()
                )

                if (item.isFavorite) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFCA28),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .size(28.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = item.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )

                    var favorite by remember { mutableStateOf(item.isFavorite) }

                    IconButton(onClick = { favorite = !favorite }) {
                        Icon(
                            imageVector = if (favorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (favorite) {
                                Color(0xFFFFCA28)
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            },
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailInfoRow(
                            icon = Icons.Default.LocationOn,
                            label = stringResource(R.string.gallery_detail_location),
                            value = item.location
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        DetailInfoRow(
                            icon = Icons.Default.DateRange,
                            label = stringResource(R.string.gallery_detail_date),
                            value = localizedGalleryDate(item.dateAdded)
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        DetailInfoRow(
                            icon = Icons.Default.PhotoLibrary,
                            label = stringResource(R.string.gallery_detail_type),
                            value = localizedGalleryType(item.type)
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        DetailInfoRow(
                            icon = Icons.Default.Info,
                            label = stringResource(R.string.gallery_detail_size),
                            value = item.size
                        )
                    }
                }

                if (item.imageUri != null) {
                    Button(
                        onClick = { onDelete(item) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = stringResource(R.string.gallery_btn_delete),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.width(80.dp)
        )

        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}