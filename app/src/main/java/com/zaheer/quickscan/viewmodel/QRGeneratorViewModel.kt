package com.zaheer.quickscan.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaheer.quickscan.util.QRCodeGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QRGeneratorViewModel : ViewModel() {
    
    private val _qrText = MutableStateFlow("")
    val qrText: StateFlow<String> = _qrText.asStateFlow()
    
    private val _generatedQR = MutableStateFlow<Bitmap?>(null)
    val generatedQR: StateFlow<Bitmap?> = _generatedQR.asStateFlow()
    
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()
    
    fun onTextChanged(text: String) {
        _qrText.value = text
    }
    
    fun generateQRCode() {
        if (_qrText.value.isEmpty()) return
        
        viewModelScope.launch(Dispatchers.Default) {
            _isGenerating.value = true
            val bitmap = QRCodeGenerator.generateQRCode(_qrText.value)
            _generatedQR.value = bitmap
            _isGenerating.value = false
        }
    }
    
    fun clearQRCode() {
        _generatedQR.value = null
        _qrText.value = ""
    }
}
