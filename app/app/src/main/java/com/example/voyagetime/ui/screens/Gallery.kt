package com.example.voyagetime.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voyagetime.R

data class GalleryItem(
    val id: Int, val name: String, val location: String, val dateAdded: String,
    val type: String, val isFavorite: Boolean, val size: String, val color: Color,
    @DrawableRes val imageRes: Int? = null
)

val sampleGalleryItems = listOf(
    GalleryItem(1, "Barcelona", "Barcelona, Spain", "March 2, 2025", "photo", true, "3.2 MB", Color(0xFFFF7043), R.drawable.barcelona),
    GalleryItem(2, "New York", "New York, USA", "January 15, 2025", "photo", false, "2.8 MB", Color(0xFF42A5F5), R.drawable.newyork),
    GalleryItem(3, "Paris", "Paris, France", "December 10, 2024", "photo", true, "2.1 MB", Color(0xFFAB47BC), R.drawable.paris),
    GalleryItem(4, "Tokyo", "Tokyo, Japan", "November 5, 2024", "photo", false, "4.1 MB", Color(0xFF66BB6A), R.drawable.tokyo),
)

@Composable
fun Gallery(modifier: Modifier = Modifier) {
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
        "favorites" -> sampleGalleryItems.filter { it.isFavorite }
        "recent" -> sampleGalleryItems.sortedByDescending { it.dateAdded }.take(6)
        else -> sampleGalleryItems
    }

    if (selectedItem != null) {
        GalleryDetailScreen(item = selectedItem!!, onBack = { selectedItem = null })
    } else {
        Box(modifier = modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(text = stringResource(R.string.gallery_title), fontSize = 28.sp, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))

                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChipItem(stringResource(R.string.gallery_filter_all), selectedFilter == "all", { selectedFilter = "all" })
                    FilterChipItem(label = stringResource(R.string.gallery_filter_favorites), selected = selectedFilter == "favorites",
                        onClick = { selectedFilter = "favorites" },
                        leadingIcon = { Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp)) })
                    FilterChipItem(stringResource(R.string.gallery_filter_recent), selectedFilter == "recent", { selectedFilter = "recent" })
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = stringResource(R.string.gallery_items_count, filteredItems.size), fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f), fontWeight = FontWeight.Medium)
                    Box {
                        Row(modifier = Modifier.clickable { sortExpanded = true }.clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)).padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(stringResource(R.string.gallery_sort_prefix) + selectedSort, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurface)
                        }
                        DropdownMenu(expanded = sortExpanded, onDismissRequest = { sortExpanded = false }) {
                            sortOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = { selectedSort = option; sortExpanded = false },
                                    leadingIcon = if (selectedSort == option) { { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) } } else null
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(filteredItems) { item -> GalleryGridItem(item = item, onClick = { selectedItem = item }) }
                }
            }

            FloatingActionButton(onClick = {}, modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
                containerColor = MaterialTheme.colorScheme.primary, shape = CircleShape) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.gallery_add), tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
fun GalleryGridItem(item: GalleryItem, onClick: () -> Unit) {
    Box(modifier = Modifier.aspectRatio(1f).clip(RoundedCornerShape(6.dp)).clickable { onClick() }) {
        if (item.imageRes != null) {
            Image(painter = painterResource(id = item.imageRes), contentDescription = item.name,
                contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        } else {
            Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(item.color.copy(alpha = 0.6f), item.color))))
        }
        Box(modifier = Modifier.fillMaxWidth().align(Alignment.BottomStart)
            .background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f)))).padding(4.dp)) {
            Text(text = item.location.split(",")[0], fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        if (item.type == "video") {
            Box(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(18.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
            }
        }
        if (item.isFavorite) {
            Box(modifier = Modifier.align(Alignment.TopStart).padding(4.dp).size(18.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFCA28), modifier = Modifier.size(11.dp))
            }
        }
    }
}

@Composable
fun FilterChipItem(label: String, selected: Boolean, onClick: () -> Unit, leadingIcon: (@Composable () -> Unit)? = null) {
    val bgColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    val textColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    Row(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(bgColor).clickable { onClick() }.padding(horizontal = 14.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        if (leadingIcon != null) CompositionLocalProvider(LocalContentColor provides textColor) { leadingIcon() }
        Text(text = label, fontSize = 13.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal, color = textColor)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryDetailScreen(item: GalleryItem, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.about_back)) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(innerPadding)) {
            Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                if (item.imageRes != null) {
                    Image(painter = painterResource(id = item.imageRes), contentDescription = item.name,
                        contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(item.color.copy(alpha = 0.7f), item.color))), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(64.dp))
                    }
                }
                if (item.isFavorite) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFCA28), modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).size(28.dp))
                }
            }

            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(item.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
                    var favorite by remember { mutableStateOf(item.isFavorite) }
                    IconButton(onClick = { favorite = !favorite }) {
                        Icon(imageVector = if (favorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (favorite) Color(0xFFFFCA28) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier.size(28.dp))
                    }
                }
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(0.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailInfoRow(Icons.Default.LocationOn, stringResource(R.string.gallery_detail_location), item.location)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        DetailInfoRow(Icons.Default.DateRange, stringResource(R.string.gallery_detail_date), item.dateAdded)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        DetailInfoRow(Icons.Default.PhotoLibrary, stringResource(R.string.gallery_detail_type), item.type.replaceFirstChar { it.uppercase() })
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        DetailInfoRow(Icons.Default.Info, stringResource(R.string.gallery_detail_size), item.size)
                    }
                }
                Button(onClick = {}, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.gallery_btn_delete), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun DetailInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), modifier = Modifier.width(80.dp))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
    }
}