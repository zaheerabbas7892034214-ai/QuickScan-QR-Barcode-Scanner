package com.zaheer.quickscan.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.zaheer.quickscan.util.BiometricHelper
import com.zaheer.quickscan.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLockScreen(
    viewModel: SettingsViewModel,
    onUnlockSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    isSetup: Boolean = false
) {
    val context = LocalContext.current as? FragmentActivity
    val scope = rememberCoroutineScope()
    
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    val useBiometric by viewModel.useBiometric.collectAsState()
    val savedPin = produceState<String?>(initialValue = null) {
        value = viewModel.pinCode.first()
    }
    
    LaunchedEffect(Unit) {
        if (!isSetup && context != null && useBiometric && BiometricHelper.isBiometricAvailable(context)) {
            BiometricHelper.showBiometricPrompt(
                activity = context,
                onSuccess = { onUnlockSuccess() },
                onError = { },
                onFailed = { }
            )
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isSetup) "Set up App Lock" else "Unlock QuickScan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = if (isSetup) "Create a 4-digit PIN" else "Enter your PIN",
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (isSetup) {
                Text(
                    text = "This PIN will be used to unlock the app",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            OutlinedTextField(
                value = pin,
                onValueChange = { if (it.length <= 4) pin = it },
                label = { Text("PIN") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                isError = showError,
                modifier = Modifier.fillMaxWidth()
            )
            
            if (isSetup) {
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = confirmPin,
                    onValueChange = { if (it.length <= 4) confirmPin = it },
                    label = { Text("Confirm PIN") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    isError = showError,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            if (showError) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    if (isSetup) {
                        when {
                            pin.length != 4 -> {
                                showError = true
                                errorMessage = "PIN must be 4 digits"
                            }
                            pin != confirmPin -> {
                                showError = true
                                errorMessage = "PINs do not match"
                            }
                            else -> {
                                scope.launch {
                                    viewModel.setPinCode(pin)
                                    viewModel.setAppLock(true)
                                    onUnlockSuccess()
                                }
                            }
                        }
                    } else {
                        if (pin == savedPin.value) {
                            onUnlockSuccess()
                        } else {
                            showError = true
                            errorMessage = "Incorrect PIN"
                            pin = ""
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = if (isSetup) pin.length == 4 && confirmPin.length == 4 else pin.length == 4
            ) {
                Text(if (isSetup) "Set PIN" else "Unlock")
            }
            
            if (!isSetup && context != null && useBiometric && BiometricHelper.isBiometricAvailable(context)) {
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedButton(
                    onClick = {
                        BiometricHelper.showBiometricPrompt(
                            activity = context,
                            onSuccess = { onUnlockSuccess() },
                            onError = {
                                showError = true
                                errorMessage = it
                            },
                            onFailed = {
                                showError = true
                                errorMessage = "Authentication failed"
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Fingerprint, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Use Biometric")
                }
            }
            
            if (isSetup) {
                Spacer(modifier = Modifier.height(24.dp))
                
                if (context != null && BiometricHelper.isBiometricAvailable(context)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = useBiometric,
                            onCheckedChange = { viewModel.setUseBiometric(it) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Use biometric authentication")
                    }
                }
            }
        }
    }
}
