package com.example.voyagetime.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voyagetime.R

@Composable
fun TermsAcceptanceScreen(
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    val scrollState = rememberScrollState()
    var showRejectDialog by rememberSaveable { mutableStateOf(false) }

    BackHandler(enabled = true) {
        showRejectDialog = true
    }

    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = {
                showRejectDialog = false
            },
            title = {
                Text(text = stringResource(R.string.terms_title))
            },
            text = {
                Text(
                    text = "You need to accept the Terms & Conditions to continue using VoyageTime."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRejectDialog = false
                    }
                ) {
                    Text(text = "Continue reading")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showRejectDialog = false
                        onReject()
                    }
                ) {
                    Text(text = stringResource(R.string.terms_decline_btn))
                }
            }
        )
    }

    Scaffold(
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.terms_accept_disclaimer),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = onAccept,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.terms_accept_btn),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            showRejectDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.terms_decline_btn),
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp)
                    )
                }

                Text(
                    text = stringResource(R.string.terms_title),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = stringResource(R.string.terms_accept_subtitle),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Text(
                    text = stringResource(R.string.terms_last_updated),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            }

            HorizontalDivider()

            TermsAcceptanceSection(
                title = stringResource(R.string.terms_accept_s1_title),
                body = stringResource(R.string.terms_accept_s1_body)
            )

            TermsAcceptanceSection(
                title = stringResource(R.string.terms_accept_s2_title),
                body = stringResource(R.string.terms_accept_s2_body)
            )

            TermsAcceptanceSection(
                title = stringResource(R.string.terms_accept_s3_title),
                body = stringResource(R.string.terms_accept_s3_body)
            )

            TermsAcceptanceSection(
                title = stringResource(R.string.terms_accept_s4_title),
                body = stringResource(R.string.terms_accept_s4_body)
            )

            TermsAcceptanceSection(
                title = stringResource(R.string.terms_accept_s5_title),
                body = stringResource(R.string.terms_accept_s5_body)
            )

            TermsAcceptanceSection(
                title = stringResource(R.string.terms_accept_s6_title),
                body = stringResource(R.string.terms_accept_s6_body)
            )

            TermsAcceptanceSection(
                title = stringResource(R.string.terms_accept_s7_title),
                body = stringResource(R.string.terms_accept_s7_body)
            )

            TermsAcceptanceSection(
                title = stringResource(R.string.terms_accept_s8_title),
                body = stringResource(R.string.terms_accept_s8_body)
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TermsAcceptanceSection(
    title: String,
    body: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = body,
            fontSize = 13.sp,
            lineHeight = 21.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}