package com.example.voyagetime.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voyagetime.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUs(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val orange = MaterialTheme.colorScheme.primary
    val sky = MaterialTheme.colorScheme.secondary
    val surface = MaterialTheme.colorScheme.surface
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "About VoyageTime",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back",
                            tint = orange
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(132.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    sky.copy(alpha = 0.18f),
                                    androidx.compose.ui.graphics.Color.Transparent
                                )
                            )
                        )
                )

                Box(
                    modifier = Modifier
                        .size(108.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(surfaceVariant)
                        .border(
                            width = 1.5.dp,
                            color = orange.copy(alpha = 0.55f),
                            shape = RoundedCornerShape(28.dp)
                        )
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "VoyageTime Logo",
                        modifier = Modifier.size(76.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "VoyageTime",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Version 1.0.0",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
                )
            }

            AboutCard {
                Text(
                    text = "What is VoyageTime?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = orange
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "VoyageTime is your personal travel companion. Plan trips, organize itineraries, track ideas, and keep your travel memories in one place with a clean and visual experience.",
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.82f)
                )
            }

            AboutCard {
                Text(
                    text = "Key Features",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = orange
                )
                Spacer(modifier = Modifier.height(14.dp))

                AboutFeatureRow(Icons.Default.Place, "Plan and manage your trips easily")
                AboutFeatureRow(Icons.Default.PhotoLibrary, "Keep a gallery of your travel memories")
                AboutFeatureRow(Icons.Default.DateRange, "Organize detailed day-by-day itineraries")
                AboutFeatureRow(Icons.Default.Notifications, "Get reminders for upcoming trips")
                AboutFeatureRow(Icons.Default.WifiOff, "Access important travel info offline")
            }

            AboutCard {
                Text(
                    text = "Our Mission",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = orange
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "We built VoyageTime to make travel planning feel inspiring, simple, and visually enjoyable. Every trip deserves a place where timing, ideas, memories, and planning come together beautifully.",
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.82f)
                )
            }

            AboutCard {
                Text(
                    text = "Technical Information",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = orange
                )
                Spacer(modifier = Modifier.height(14.dp))

                AboutFeatureRow(Icons.Default.Code, "Built with Kotlin and Jetpack Compose")
                AboutFeatureRow(Icons.Default.Storage, "Local trip organization and editable travel data")
                AboutFeatureRow(Icons.Default.Security, "Structured navigation and scalable screen design")
            }

            Text(
                text = "Designed for travelers who want clarity, style, and control.",
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                color = sky.copy(alpha = 0.9f),
                lineHeight = 20.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )
        }
    }
}

@Composable
private fun AboutCard(
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun AboutFeatureRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(38.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.14f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.size(12.dp))

            Text(
                text = text,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.86f)
            )
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
        )
    }
}