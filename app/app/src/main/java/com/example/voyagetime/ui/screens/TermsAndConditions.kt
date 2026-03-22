package com.example.voyagetime.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voyagetime.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditions(modifier: Modifier = Modifier, onBack: () -> Unit = {}) {
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.terms_title), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.about_back)) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(modifier = modifier.fillMaxSize().verticalScroll(scrollState).padding(innerPadding).padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)) {

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = stringResource(R.string.terms_title), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text(text = stringResource(R.string.terms_last_updated), fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f))
            }
            Text(text = stringResource(R.string.terms_intro), fontSize = 14.sp, lineHeight = 22.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f))

            TermsSection("1", stringResource(R.string.terms_s1_title), stringResource(R.string.terms_s1_body))
            TermsSection("2", stringResource(R.string.terms_s2_title), stringResource(R.string.terms_s2_body))
            TermsSection("3", stringResource(R.string.terms_s3_title), stringResource(R.string.terms_s3_body))
            TermsSection("4", stringResource(R.string.terms_s4_title), stringResource(R.string.terms_s4_body))
            TermsSection("5", stringResource(R.string.terms_s5_title), stringResource(R.string.terms_s5_body))
            TermsSection("6", stringResource(R.string.terms_s6_title), stringResource(R.string.terms_s6_body))
            TermsSection("7", stringResource(R.string.terms_s7_title), stringResource(R.string.terms_s7_body))
            TermsSection("8", stringResource(R.string.terms_s8_title), stringResource(R.string.terms_s8_body))
            TermsSection("9", stringResource(R.string.terms_s9_title), stringResource(R.string.terms_s9_body))
            TermsSection("10", stringResource(R.string.terms_s10_title), stringResource(R.string.terms_s10_body))
            TermsSection("11", stringResource(R.string.terms_s11_title), stringResource(R.string.terms_s11_body))

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                elevation = CardDefaults.cardElevation(0.dp)) {
                Text(text = stringResource(R.string.terms_footer), fontSize = 13.sp, lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(16.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun TermsSection(number: String, title: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "$number. $title", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
        Text(text = body, fontSize = 14.sp, lineHeight = 22.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f))
    }
}