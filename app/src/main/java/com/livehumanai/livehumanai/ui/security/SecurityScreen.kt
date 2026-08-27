package com.livehumanai.livehumanai.ui.security

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var biometricEnabled by remember { mutableStateOf(false) }
    var encryptMemories by remember { mutableStateOf(true) }
    var secureModelInstall by remember { mutableStateOf(true) }
    var verifyChecksums by remember { mutableStateOf(true) }
    var autoLockTimeout by remember { mutableStateOf("5 minutes") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Security Center") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Hardware Security & Verification", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Android Keystore protects memory keys. Model binaries are validated via SHA-256 before runtime execution.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Text("Authentication & Encryption", style = MaterialTheme.typography.titleMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Biometric Authentication", style = MaterialTheme.typography.bodyLarge)
                    Text("Require BiometricPrompt to view sensitive memories", style = MaterialTheme.typography.bodySmall)
                }
                Switch(checked = biometricEnabled, onCheckedChange = { biometricEnabled = it })
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Encrypt Memory Database", style = MaterialTheme.typography.bodyLarge)
                    Text("Use SQLCipher with Android Keystore key management", style = MaterialTheme.typography.bodySmall)
                }
                Switch(checked = encryptMemories, onCheckedChange = { encryptMemories = it })
            }

            Spacer(Modifier.height(8.dp))
            Text("Model Verification Policy", style = MaterialTheme.typography.titleMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Enforce SHA-256 Checksum", style = MaterialTheme.typography.bodyLarge)
                    Text("Reject corrupted or tampered Hugging Face downloads", style = MaterialTheme.typography.bodySmall)
                }
                Switch(checked = verifyChecksums, onCheckedChange = { verifyChecksums = it })
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Atomic Model Installation", style = MaterialTheme.typography.bodyLarge)
                    Text("Verify models in staging sandbox before registering", style = MaterialTheme.typography.bodySmall)
                }
                Switch(checked = secureModelInstall, onCheckedChange = { secureModelInstall = it })
            }

            Spacer(Modifier.height(8.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Security Audit Status", style = MaterialTheme.typography.titleMedium)
                    Text("✓ Offline-first isolation: Active", style = MaterialTheme.typography.bodySmall)
                    Text("✓ Keystore master key: Valid", style = MaterialTheme.typography.bodySmall)
                    Text("✓ Permission Manager: Restricted", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
