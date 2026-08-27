package com.livehumanai.ui.security

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
                title = { Text("Security") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                // Security Status
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Shield,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Security Status: Good",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Your data is protected with Android Keystore encryption. Biometric authentication is available but not enabled.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
                // Authentication Section
                SecuritySection(title = "Authentication") {
                    SecurityToggleItem(
                        title = "Biometric Lock",
                        description = "Use fingerprint or face unlock to access the app",
                        checked = biometricEnabled,
                        onCheckedChange = { biometricEnabled = it }
                    )
                    
                    SecurityDropdownItem(
                        title = "Auto-Lock Timeout",
                        value = autoLockTimeout,
                        options = listOf("1 minute", "5 minutes", "15 minutes", "Never"),
                        onValueSelected = { autoLockTimeout = it }
                    )
                }
                
                // Encryption Section
                SecuritySection(title = "Encryption") {
                    SecurityToggleItem(
                        title = "Encrypt Memories",
                        description = "Encrypt sensitive memories using Android Keystore",
                        checked = encryptMemories,
                        onCheckedChange = { encryptMemories = it }
                    )
                    
                    SecurityInfoItem(
                        title = "Encryption Method",
                        description = "AES-256-GCM with hardware-backed keys"
                    )
                }
                
                // Model Security Section
                SecuritySection(title = "Model Security") {
                    SecurityToggleItem(
                        title = "Secure Model Installation",
                        description = "Verify model signatures before installation",
                        checked = secureModelInstall,
                        onCheckedChange = { secureModelInstall = it }
                    )
                    
                    SecurityToggleItem(
                        title = "Checksum Verification",
                        description = "Verify SHA-256 checksums for all downloads",
                        checked = verifyChecksums,
                        onCheckedChange = { verifyChecksums = it }
                    )
                    
                    SecurityInfoItem(
                        title = "Model Sources",
                        description = "Only models from verified Hugging Face repositories"
                    )
                }
                
                // Data Protection Section
                SecuritySection(title = "Data Protection") {
                    SecurityInfoItem(
                        title = "Storage Location",
                        description = "App-private storage (encrypted on Android 10+)"
                    )
                    
                    SecurityInfoItem(
                        title = "Backup Encryption",
                        description = "Exports are encrypted with your device credentials"
                    )
                    
                    Button(
                        onClick = { /* Clear secure data */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Icon(
                            Icons.Default.DeleteForever,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Clear All Secure Data")
                    }
                }
                
                // Security Audit Section
                SecuritySection(title = "Security Audit") {
                    SecurityInfoItem(
                        title = "Last Security Scan",
                        description = "Today at 9:41 AM - No issues found"
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { /* Run security scan */ },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Run Security Scan")
                        }
                        
                        OutlinedButton(
                            onClick = { /* View audit log */ },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("View Audit Log")
                        }
                    }
                }
                
                // Advanced Section
                SecuritySection(title = "Advanced") {
                    SecurityInfoItem(
                        title = "Android Keystore",
                        description = "Hardware-backed key storage available"
                    )
                    
                    SecurityInfoItem(
                        title = "SELinux Status",
                        description = "Enforcing"
                    )
                    
                    OutlinedButton(
                        onClick = { /* View security details */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View Detailed Security Information")
                    }
                }
            }
        }
    }
}

@Composable
private fun SecuritySection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun SecurityToggleItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SecurityInfoItem(
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SecurityDropdownItem(
    title: String,
    value: String,
    options: List<String>,
    onValueSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
        
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = { },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .width(150.dp)
                    .menuAnchor()
            )
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
