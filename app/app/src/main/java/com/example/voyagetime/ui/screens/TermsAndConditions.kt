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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditions(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Terms & Conditions", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Go back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Header
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Terms & Conditions",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Last updated: March 1, 2025",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f)
                )
            }

            Text(
                text = "Please read these Terms and Conditions carefully before using the VoyageTime application. By accessing or using our service, you agree to be bound by these terms.",
                fontSize = 14.sp,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
            )

            TermsSection(
                number = "1",
                title = "Acceptance of Terms",
                body = "By downloading, installing, or using VoyageTime, you confirm that you are at least 16 years of age and agree to comply with and be bound by these Terms and Conditions. If you do not agree with any part of these terms, you must not use our application.\n\nWe reserve the right to update or modify these terms at any time. Continued use of the application after changes are published constitutes your acceptance of the revised terms."
            )

            TermsSection(
                number = "2",
                title = "Use of the Application",
                body = "VoyageTime grants you a limited, non-exclusive, non-transferable, revocable license to use the application for personal, non-commercial purposes.\n\nYou agree not to:\n• Use the application for any unlawful purpose or in violation of any local, national, or international regulations.\n• Reproduce, duplicate, copy, sell, or exploit any portion of the service without express written permission.\n• Attempt to gain unauthorized access to any part of the application or its related systems.\n• Upload or transmit any malicious code, viruses, or harmful content."
            )

            TermsSection(
                number = "3",
                title = "User Content",
                body = "You retain full ownership of any content you upload to VoyageTime, including photos, videos, trip notes, and itineraries ('User Content').\n\nBy uploading User Content, you grant VoyageTime a limited, worldwide, royalty-free license to store and display that content solely for the purpose of providing the service to you.\n\nYou are solely responsible for the accuracy, legality, and appropriateness of any content you upload. VoyageTime does not monitor User Content but reserves the right to remove content that violates these Terms."
            )

            TermsSection(
                number = "4",
                title = "Privacy and Data",
                body = "Your privacy is important to us. Our Privacy Policy, which is incorporated into these Terms by reference, explains how we collect, use, and share information about you when you use VoyageTime.\n\nWe collect only the data necessary to provide the service, including account information, trip data, and usage analytics. We do not sell your personal data to third parties.\n\nYou may request deletion of your account and associated data at any time by contacting us at contact@voyagetime.app."
            )

            TermsSection(
                number = "5",
                title = "Intellectual Property",
                body = "All rights, title, and interest in and to VoyageTime — including but not limited to the software, design, graphics, logos, and trademarks — are and will remain the exclusive property of VoyageTime and its licensors.\n\nYou may not use our trademarks, logos, or brand assets without prior written consent."
            )

            TermsSection(
                number = "6",
                title = "Third-Party Services",
                body = "VoyageTime may integrate with third-party services such as mapping providers, weather APIs, and cloud storage solutions. These services are governed by their own terms and privacy policies.\n\nWe are not responsible for the content, accuracy, or practices of any third-party services. Links to third-party websites within the application do not constitute an endorsement."
            )

            TermsSection(
                number = "7",
                title = "Disclaimer of Warranties",
                body = "VoyageTime is provided on an \"as is\" and \"as available\" basis without warranties of any kind, either express or implied, including but not limited to implied warranties of merchantability, fitness for a particular purpose, or non-infringement.\n\nWe do not warrant that the application will be uninterrupted, error-free, or free of viruses or other harmful components. Travel information provided within the app is for guidance only and should always be verified with official sources before travel."
            )

            TermsSection(
                number = "8",
                title = "Limitation of Liability",
                body = "To the fullest extent permitted by applicable law, VoyageTime shall not be liable for any indirect, incidental, special, consequential, or punitive damages, including but not limited to loss of profits, data, or goodwill, resulting from:\n• Your use or inability to use the application.\n• Unauthorized access to your data.\n• Any errors or inaccuracies in travel content.\n• Any other matter relating to the service."
            )

            TermsSection(
                number = "9",
                title = "Account Termination",
                body = "We reserve the right to suspend or terminate your account at our sole discretion, without prior notice, if we believe you have violated these Terms or engaged in conduct harmful to other users or the integrity of the service.\n\nYou may delete your account at any time through the account settings. Upon deletion, your data will be permanently removed within 30 days, except where retention is required by law."
            )

            TermsSection(
                number = "10",
                title = "Governing Law",
                body = "These Terms shall be governed and construed in accordance with the laws of the European Union and the Kingdom of Spain, without regard to its conflict of law provisions.\n\nAny disputes arising under these Terms shall be subject to the exclusive jurisdiction of the courts located in Barcelona, Spain."
            )

            TermsSection(
                number = "11",
                title = "Contact Us",
                body = "If you have any questions about these Terms and Conditions, please contact us:\n\nEmail: legal@voyagetime.app\nAddress: VoyageTime, Carrer de la Tecnologia 42, 08000 Barcelona, Spain"
            )

            // Footer
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Text(
                    text = "By using VoyageTime, you acknowledge that you have read, understood, and agree to be bound by these Terms and Conditions.",
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun TermsSection(number: String, title: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "$number. $title",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = body,
            fontSize = 14.sp,
            lineHeight = 22.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
        )
    }
}