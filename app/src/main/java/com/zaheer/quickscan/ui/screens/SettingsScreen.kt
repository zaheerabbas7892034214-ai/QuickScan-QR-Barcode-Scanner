package com.zaheer.quickscan.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zaheer.quickscan.ui.components.ProBadge
import com.zaheer.quickscan.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToPaywall: () -> Unit,
    onNavigateToAppLock: () -> Unit
) {
    val isPro by viewModel.isProActive.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isAppLockEnabled by viewModel.isAppLockEnabled.collectAsState()
    val vibrateOnScan by viewModel.vibrateOnScan.collectAsState()
    val soundOnScan by viewModel.soundOnScan.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            if (!isPro) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Upgrade to PRO",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Unlock all premium features",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Button(onClick = onNavigateToPaywall) {
                            Text("Upgrade")
                        }
                    }
                }
            }
            
            SettingSection(title = "PRO Features")
            
            SettingItem(
                icon = Icons.Default.Lock,
                title = "App Lock",
                description = "Secure app with PIN or biometric",
                trailing = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isPro) {
                            Switch(
                                checked = isAppLockEnabled,
                                onCheckedChange = { enabled ->
                                    if (enabled) {
                                        onNavigateToAppLock()
                                    } else {
                                        viewModel.setAppLock(false)
                                    }
                                }
                            )
                        } else {
                            ProBadge()
                        }
                    }
                },
                enabled = isPro,
                onClick = if (isPro) {
                    { if (isAppLockEnabled) onNavigateToAppLock() }
                } else {
                    onNavigateToPaywall
                }
            )
            
            Divider()
            
            SettingSection(title = "Appearance")
            
            SettingItem(
                icon = Icons.Default.DarkMode,
                title = "Dark Mode",
                description = "Enable dark theme",
                trailing = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isPro) {
                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = { viewModel.setDarkMode(it) }
                            )
                        } else {
                            ProBadge()
                        }
                    }
                },
                enabled = isPro,
                onClick = if (isPro) null else onNavigateToPaywall
            )
            
            Divider()
            
            SettingSection(title = "Scanning")
            
            SettingItem(
                icon = Icons.Default.Vibration,
                title = "Vibrate on Scan",
                description = "Vibrate when scan is successful",
                trailing = {
                    Switch(
                        checked = vibrateOnScan,
                        onCheckedChange = { viewModel.setVibrateOnScan(it) }
                    )
                }
            )
            
            Divider()
            
            SettingItem(
                icon = Icons.Default.VolumeUp,
                title = "Sound on Scan",
                description = "Play sound when scan is successful",
                trailing = {
                    Switch(
                        checked = soundOnScan,
                        onCheckedChange = { viewModel.setSoundOnScan(it) }
                    )
                }
            )
            
            Divider()
            
            SettingSection(title = "About")
            
            SettingItem(
                icon = Icons.Default.Info,
                title = "Version",
                description = "1.0.0"
            )
            
            Divider()
            
            SettingItem(
                icon = Icons.Default.Description,
                title = "Privacy Policy",
                description = "View our privacy policy",
                onClick = {}
            )
            
            Divider()
            
            SettingItem(
                icon = Icons.Default.Policy,
                title = "Terms of Service",
                description = "View our terms of service",
                onClick = {}
            )
        }
    }
}

@Composable
private fun SettingSection(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    Surface(
        onClick = onClick ?: {},
        enabled = onClick != null && enabled,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                if (description != null) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
            if (trailing != null) {
                Spacer(modifier = Modifier.width(8.dp))
                trailing()
            }
        }
    }
}
