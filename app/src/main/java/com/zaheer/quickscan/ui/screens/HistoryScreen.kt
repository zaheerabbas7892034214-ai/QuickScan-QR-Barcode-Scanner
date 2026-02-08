package com.zaheer.quickscan.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zaheer.quickscan.ui.components.EmptyState
import com.zaheer.quickscan.ui.components.ScanHistoryItem
import com.zaheer.quickscan.ui.components.UpgradeBanner
import com.zaheer.quickscan.viewmodel.HistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onNavigateToPaywall: () -> Unit,
    onScanItemClick: (String) -> Unit
) {
    val scans by viewModel.scans.collectAsState()
    val isPro by viewModel.isProActive.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    
    var showClearDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") },
                actions = {
                    if (scans.isNotEmpty()) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(Icons.Default.DeleteForever, "Clear All")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (!isPro) {
                UpgradeBanner(onUpgradeClick = onNavigateToPaywall)
            }
            
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search history...") },
                leadingIcon = { Icon(Icons.Default.Search, "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Clear, "Clear")
                        }
                    }
                },
                singleLine = true
            )
            
            if (scans.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.History,
                    title = if (searchQuery.isEmpty()) "No History" else "No Results",
                    message = if (searchQuery.isEmpty()) 
                        "Your scan history will appear here"
                    else 
                        "No scans match your search"
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(scans, key = { it.id }) { scan ->
                        ScanHistoryItem(
                            scan = scan,
                            onItemClick = { onScanItemClick(scan.content) },
                            onFavoriteClick = { viewModel.toggleFavorite(scan) },
                            onDeleteClick = { viewModel.deleteScan(scan) }
                        )
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
    
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear History") },
            text = { Text("Are you sure you want to delete all scan history? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearHistory()
                        showClearDialog = false
                    }
                ) {
                    Text("Clear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
