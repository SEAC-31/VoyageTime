package com.example.voyagetime.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TermsAcceptanceScreen(
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    val scrollState = rememberScrollState()

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
                        text = "By tapping \"Accept\", you confirm you have read and agree to our Terms & Conditions and Privacy Policy.",
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
                            text = "Accept & Continue",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Decline",
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

            // Header
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
                    text = "Terms & Conditions",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Please read our Terms and Conditions carefully before using VoyageTime.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Text(
                    text = "Last updated: March 1, 2025",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            }

            HorizontalDivider()

            TermsAcceptanceSection("1. Acceptance of Terms",
                "By using VoyageTime, you confirm that you are at least 16 years of age and agree to comply with these Terms. If you do not agree, you must not use the application."
            )

            TermsAcceptanceSection("2. Use of the Application",
                "VoyageTime grants you a limited, non-exclusive license to use the app for personal, non-commercial purposes. You agree not to use the app for any unlawful purpose or attempt to gain unauthorized access to any part of the service."
            )

            TermsAcceptanceSection("3. User Content",
                "You retain ownership of content you upload. By uploading content, you grant VoyageTime a limited license to store and display it solely to provide the service. You are responsible for the legality and accuracy of your content."
            )

            TermsAcceptanceSection("4. Privacy & Data",
                "We collect only the data necessary to provide the service. We do not sell your personal data to third parties. You may request deletion of your account and data at any time by contacting contact@voyagetime.app."
            )

            TermsAcceptanceSection("5. Disclaimer of Warranties",
                "VoyageTime is provided \"as is\" without warranties of any kind. Travel information provided is for guidance only and should always be verified with official sources before travel."
            )

            TermsAcceptanceSection("6. Limitation of Liability",
                "VoyageTime shall not be liable for any indirect, incidental, or consequential damages resulting from your use of the application or any travel decisions made based on information provided."
            )

            TermsAcceptanceSection("7. Governing Law",
                "These Terms are governed by the laws of the European Union and the Kingdom of Spain. Any disputes shall be subject to the jurisdiction of the courts of Barcelona, Spain."
            )

            TermsAcceptanceSection("8. Contact",
                "For questions about these Terms, contact us at:\nlegal@voyagetime.app"
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TermsAcceptanceSection(title: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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