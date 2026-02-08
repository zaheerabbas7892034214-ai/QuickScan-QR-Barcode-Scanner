package com.zaheer.quickscan.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.zaheer.quickscan.data.database.DatabaseProvider
import com.zaheer.quickscan.data.repository.EntitlementRepository
import com.zaheer.quickscan.data.repository.ScanHistoryRepository
import com.zaheer.quickscan.data.repository.SettingsRepository
import com.zaheer.quickscan.ui.screens.*
import com.zaheer.quickscan.util.Constants
import com.zaheer.quickscan.viewmodel.*
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph() {
    val context = LocalContext.current
    val navController = rememberNavController()
    
    val database = remember { DatabaseProvider.getDatabase(context) }
    val scanHistoryRepository = remember { ScanHistoryRepository(database.scanHistoryDao()) }
    val entitlementRepository = remember { EntitlementRepository(database.entitlementDao()) }
    val settingsRepository = remember { SettingsRepository(context) }
    
    val scannerViewModel: ScannerViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ScannerViewModel(scanHistoryRepository, entitlementRepository) as T
            }
        }
    )
    
    val historyViewModel: HistoryViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HistoryViewModel(scanHistoryRepository, entitlementRepository) as T
            }
        }
    )
    
    val qrGeneratorViewModel: QRGeneratorViewModel = viewModel()
    
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SettingsViewModel(settingsRepository, entitlementRepository) as T
            }
        }
    )
    
    val paywallViewModel: PaywallViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PaywallViewModel(context.applicationContext as android.app.Application, entitlementRepository) as T
            }
        }
    )
    
    val isPro by scannerViewModel.isProActive.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val showBottomBar = currentRoute in listOf(
        Constants.Routes.SCANNER,
        Constants.Routes.HISTORY,
        Constants.Routes.SETTINGS
    )
    
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.QrCodeScanner, "Scanner") },
                        label = { Text("Scanner") },
                        selected = currentRoute == Constants.Routes.SCANNER,
                        onClick = {
                            navController.navigate(Constants.Routes.SCANNER) {
                                popUpTo(Constants.Routes.SCANNER) { inclusive = true }
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.History, "History") },
                        label = { Text("History") },
                        selected = currentRoute == Constants.Routes.HISTORY,
                        onClick = {
                            navController.navigate(Constants.Routes.HISTORY) {
                                popUpTo(Constants.Routes.SCANNER)
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, "Settings") },
                        label = { Text("Settings") },
                        selected = currentRoute == Constants.Routes.SETTINGS,
                        onClick = {
                            navController.navigate(Constants.Routes.SETTINGS) {
                                popUpTo(Constants.Routes.SCANNER)
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentRoute == Constants.Routes.SCANNER && isPro) {
                FloatingActionButton(
                    onClick = { navController.navigate(Constants.Routes.QR_GENERATOR) }
                ) {
                    Icon(Icons.Default.Add, "Generate QR")
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Constants.Routes.SPLASH,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Constants.Routes.SPLASH) {
                SplashScreen(
                    onNavigateToScanner = {
                        navController.navigate(Constants.Routes.SCANNER) {
                            popUpTo(Constants.Routes.SPLASH) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(Constants.Routes.SCANNER) {
                ScannerScreen(
                    viewModel = scannerViewModel,
                    onNavigateToResult = {
                        navController.navigate(Constants.Routes.RESULT)
                    },
                    isPro = isPro
                )
            }
            
            composable(Constants.Routes.RESULT) {
                ResultScreen(
                    viewModel = scannerViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(Constants.Routes.HISTORY) {
                HistoryScreen(
                    viewModel = historyViewModel,
                    onNavigateToPaywall = {
                        navController.navigate(Constants.Routes.PAYWALL)
                    },
                    onScanItemClick = { content ->
                        // Handle scan item click - could show details
                    }
                )
            }
            
            composable(Constants.Routes.QR_GENERATOR) {
                if (isPro) {
                    QRGeneratorScreen(
                        viewModel = qrGeneratorViewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                } else {
                    LaunchedEffect(Unit) {
                        navController.navigate(Constants.Routes.PAYWALL)
                    }
                }
            }
            
            composable(Constants.Routes.EXPORT) {
                if (isPro) {
                    ExportScreen(
                        historyViewModel = historyViewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                } else {
                    LaunchedEffect(Unit) {
                        navController.navigate(Constants.Routes.PAYWALL)
                    }
                }
            }
            
            composable(Constants.Routes.PAYWALL) {
                PaywallScreen(
                    viewModel = paywallViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onPurchaseSuccess = { navController.popBackStack() }
                )
            }
            
            composable(Constants.Routes.SETTINGS) {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateToPaywall = {
                        navController.navigate(Constants.Routes.PAYWALL)
                    },
                    onNavigateToAppLock = {
                        navController.navigate(Constants.Routes.APP_LOCK)
                    }
                )
            }
            
            composable(Constants.Routes.APP_LOCK) {
                val isAppLockEnabled by settingsViewModel.isAppLockEnabled.collectAsState()
                AppLockScreen(
                    viewModel = settingsViewModel,
                    onUnlockSuccess = { navController.popBackStack() },
                    onNavigateBack = { navController.popBackStack() },
                    isSetup = !isAppLockEnabled
                )
            }
        }
    }
}
