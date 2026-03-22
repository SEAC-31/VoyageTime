package com.example.voyagetime.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voyagetime.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUs(modifier: Modifier = Modifier, onBack: () -> Unit = {}) {
    val scrollState = rememberScrollState()
    val orange = MaterialTheme.colorScheme.primary
    val sky = MaterialTheme.colorScheme.secondary
    val surface = MaterialTheme.colorScheme.surface
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.about_title), color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.about_back), tint = orange)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = surface)
            )
        }
    ) { innerPadding ->
        Column(modifier = modifier.fillMaxSize().verticalScroll(scrollState).padding(innerPadding).padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {

            Box(contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.size(132.dp).clip(CircleShape).background(Brush.radialGradient(colors = listOf(sky.copy(alpha = 0.18f), androidx.compose.ui.graphics.Color.Transparent))))
                Box(modifier = Modifier.size(108.dp).clip(RoundedCornerShape(28.dp)).background(surfaceVariant)
                    .border(width = 1.5.dp, color = orange.copy(alpha = 0.55f), shape = RoundedCornerShape(28.dp)).padding(14.dp),
                    contentAlignment = Alignment.Center) {
                    Image(painter = painterResource(id = R.drawable.logo), contentDescription = stringResource(R.string.app_name),
                        modifier = Modifier.size(76.dp), contentScale = ContentScale.Fit)
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = stringResource(R.string.app_name), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = stringResource(R.string.about_version), fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f))
            }

            AboutCard {
                Text(text = stringResource(R.string.about_what_title), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = orange)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = stringResource(R.string.about_what_body), fontSize = 14.sp, lineHeight = 22.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.82f))
            }

            AboutCard {
                Text(text = stringResource(R.string.about_features_title), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = orange)
                Spacer(modifier = Modifier.height(14.dp))
                AboutFeatureRow(Icons.Default.Place, stringResource(R.string.about_feature_trips))
                AboutFeatureRow(Icons.Default.PhotoLibrary, stringResource(R.string.about_feature_gallery))
                AboutFeatureRow(Icons.Default.DateRange, stringResource(R.string.about_feature_itinerary))
                AboutFeatureRow(Icons.Default.Notifications, stringResource(R.string.about_feature_notifications))
                AboutFeatureRow(Icons.Default.WifiOff, stringResource(R.string.about_feature_offline))
            }

            AboutCard {
                Text(text = stringResource(R.string.about_mission_title), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = orange)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = stringResource(R.string.about_mission_body), fontSize = 14.sp, lineHeight = 22.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.82f))
            }

            AboutCard {
                Text(text = stringResource(R.string.about_tech_title), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = orange)
                Spacer(modifier = Modifier.height(14.dp))
                AboutFeatureRow(Icons.Default.Code, stringResource(R.string.about_tech_kotlin))
                AboutFeatureRow(Icons.Default.Storage, stringResource(R.string.about_tech_storage))
                AboutFeatureRow(Icons.Default.Security, stringResource(R.string.about_tech_nav))
            }

            Text(text = stringResource(R.string.about_footer_tagline), fontSize = 13.sp, textAlign = TextAlign.Center,
                color = sky.copy(alpha = 0.9f), lineHeight = 20.sp, modifier = Modifier.padding(top = 4.dp, bottom = 8.dp))
        }
    }
}

@Composable
private fun AboutCard(content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) { content() }
    }
}

@Composable
private fun AboutFeatureRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Column {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(38.dp), shape = CircleShape, color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.14f)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.size(12.dp))
            Text(text = text, fontSize = 14.sp, lineHeight = 20.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.86f))
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f))
    }
}