package com.example.voyagetime.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── DATA MODEL ───────────────────────────────────────────────

data class GalleryItem(
    val id: Int,
    val name: String,
    val location: String,
    val dateAdded: String,
    val type: String,
    val isFavorite: Boolean,
    val size: String,
    val color: Color
)

val sampleGalleryItems = listOf(
    GalleryItem(1, "Sunset at Santorini", "Santorini, Greece", "March 2, 2025", "photo", true, "3.2 MB", Color(0xFFFF7043)),
    GalleryItem(2, "Tokyo Streets", "Tokyo, Japan", "January 15, 2025", "photo", false, "2.8 MB", Color(0xFF42A5F5)),
    GalleryItem(3, "Colosseum Visit", "Rome, Italy", "December 10, 2024", "video", true, "45.1 MB", Color(0xFF66BB6A)),
    GalleryItem(4, "Safari Morning", "Nairobi, Kenya", "November 5, 2024", "photo", false, "4.1 MB", Color(0xFFFFCA28)),
    GalleryItem(5, "Eiffel Tower", "Paris, France", "October 20, 2024", "photo", true, "2.1 MB", Color(0xFFAB47BC)),
    GalleryItem(6, "Bali Rice Fields", "Ubud, Bali", "September 3, 2024", "video", false, "38.7 MB", Color(0xFF26A69A)),
    GalleryItem(7, "Northern Lights", "Tromsø, Norway", "February 14, 2025", "photo", true, "5.5 MB", Color(0xFF5C6BC0)),
    GalleryItem(8, "Machu Picchu", "Cusco, Peru", "August 22, 2024", "photo", false, "3.9 MB", Color(0xFFEF5350)),
    GalleryItem(9, "Grand Canyon", "Arizona, USA", "July 4, 2024", "photo", false, "4.4 MB", Color(0xFFFF7043)),
    GalleryItem(10, "Amsterdam Canals", "Amsterdam, Netherlands", "June 18, 2024", "video", true, "52.3 MB", Color(0xFF29B6F6)),
    GalleryItem(11, "Sahara Desert", "Morocco", "May 30, 2024", "photo", false, "3.7 MB", Color(0xFFFFB300)),
    GalleryItem(12, "Great Wall", "Beijing, China", "April 12, 2024", "photo", true, "2.9 MB", Color(0xFF8D6E63)),
    GalleryItem(13, "Amalfi Coast", "Amalfi, Italy", "March 18, 2025", "photo", true, "4.8 MB", Color(0xFF00ACC1)),
    GalleryItem(14, "Kyoto Temple", "Kyoto, Japan", "February 28, 2025", "photo", false, "3.1 MB", Color(0xFFEC407A)),
    GalleryItem(15, "Patagonia Trek", "El Chaltén, Argentina", "January 5, 2025", "video", true, "61.2 MB", Color(0xFF78909C)),
    GalleryItem(16, "Maldives Beach", "Malé, Maldives", "December 25, 2024", "photo", true, "2.6 MB", Color(0xFF26C6DA)),
    GalleryItem(17, "Budapest Night", "Budapest, Hungary", "November 30, 2024", "photo", false, "3.4 MB", Color(0xFFFF7043)),
    GalleryItem(18, "Havana Streets", "Havana, Cuba", "October 14, 2024", "video", false, "44.5 MB", Color(0xFFFFB74D)),
    GalleryItem(19, "Petra at Dawn", "Petra, Jordan", "September 22, 2024", "photo", true, "5.1 MB", Color(0xFFA1887F)),
    GalleryItem(20, "Sydney Opera", "Sydney, Australia", "August 8, 2024", "photo", false, "3.8 MB", Color(0xFF42A5F5)),
    GalleryItem(21, "Cinque Terre", "Liguria, Italy", "July 19, 2024", "photo", false, "4.2 MB", Color(0xFF9CCC65)),
    GalleryItem(22, "Angkor Wat", "Siem Reap, Cambodia", "June 3, 2024", "video", true, "57.8 MB", Color(0xFF8D6E63)),
    GalleryItem(23, "Iceland Waterfall", "Skógafoss, Iceland", "May 11, 2024", "photo", false, "6.1 MB", Color(0xFF4DB6AC)),
    GalleryItem(24, "Marrakech Souk", "Marrakech, Morocco", "April 27, 2024", "photo", true, "2.3 MB", Color(0xFFFF8A65)),
)

// ── MAIN GALLERY SCREEN ──────────────────────────────────────

@Composable
fun Gallery(modifier: Modifier = Modifier) {
    var selectedItem by remember { mutableStateOf<GalleryItem?>(null) }
    var selectedFilter by remember { mutableStateOf("all") }
    var sortExpanded by remember { mutableStateOf(false) }
    var selectedSort by remember { mutableStateOf("Date Added") }

    val sortOptions = listOf("Date Added", "Name", "Size", "Location")

    val filteredItems = when (selectedFilter) {
        "favorites" -> sampleGalleryItems.filter { it.isFavorite }
        "recent" -> sampleGalleryItems.sortedByDescending { it.dateAdded }.take(6)
        else -> sampleGalleryItems
    }

    if (selectedItem != null) {
        GalleryDetailScreen(
            item = selectedItem!!,
            onBack = { selectedItem = null }
        )
    } else {
        Box(modifier = modifier.fillMaxSize()) {

            Column(modifier = Modifier.fillMaxSize()) {

                Text(
                    text = "Gallery",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChipItem(
                        label = "All",
                        selected = selectedFilter == "all",
                        onClick = { selectedFilter = "all" }
                    )
                    FilterChipItem(
                        label = "Favorites",
                        selected = selectedFilter == "favorites",
                        onClick = { selectedFilter = "favorites" },
                        leadingIcon = {
                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    )
                    FilterChipItem(
                        label = "Recent",
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
                        text = "${filteredItems.size} items",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
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
                                text = "Sort by: $selectedSort",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                Icons.Default.ArrowDropDown,
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
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                    } else null
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
                    verticalArrangement = Arrangement.spacedBy(4.dp),
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
                onClick = {},
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add photo or video",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

// ── GRID ITEM ────────────────────────────────────────────────

@Composable
fun GalleryGridItem(item: GalleryItem, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(item.color.copy(alpha = 0.7f))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(item.color.copy(alpha = 0.6f), item.color)
                    )
                )
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
                Icon(Icons.Default.PlayArrow, contentDescription = "Video", tint = Color.White, modifier = Modifier.size(12.dp))
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
                Icon(Icons.Default.Star, contentDescription = "Favorite", tint = Color(0xFFFFCA28), modifier = Modifier.size(11.dp))
            }
        }
    }
}

// ── FILTER CHIP ──────────────────────────────────────────────

@Composable
fun FilterChipItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val bgColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    val textColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

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

// ── DETAIL SCREEN ────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryDetailScreen(item: GalleryItem, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Go back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
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
                    .height(300.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(item.color.copy(alpha = 0.7f), item.color)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (item.type == "video") {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                } else {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(64.dp))
                }

                if (item.isFavorite) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFCA28),
                        modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).size(28.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(item.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailInfoRow(Icons.Default.LocationOn, "Location", item.location)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        DetailInfoRow(Icons.Default.DateRange, "Date Added", item.dateAdded)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        DetailInfoRow(
                            if (item.type == "video") Icons.Default.PlayArrow else Icons.Default.PhotoLibrary,
                            "Type", item.type.replaceFirstChar { it.uppercase() }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        DetailInfoRow(Icons.Default.Info, "File Size", item.size)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        DetailInfoRow(Icons.Default.Star, "Favorite", if (item.isFavorite) "Yes" else "No")
                    }
                }

                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ── DETAIL INFO ROW ──────────────────────────────────────────

@Composable
fun DetailInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), modifier = Modifier.width(80.dp))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
    }
}