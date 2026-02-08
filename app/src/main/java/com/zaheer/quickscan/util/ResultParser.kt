package com.zaheer.quickscan.util

import com.zaheer.quickscan.data.model.ScanType

object ResultParser {
    
    fun parseType(content: String): ScanType {
        return when {
            content.startsWith("http://") || content.startsWith("https://") || 
            content.startsWith("www.") -> ScanType.URL
            
            content.startsWith("mailto:") || isEmail(content) -> ScanType.EMAIL
            
            content.startsWith("tel:") || isPhone(content) -> ScanType.PHONE
            
            content.startsWith("smsto:") || content.startsWith("SMSTO:") -> ScanType.SMS
            
            content.startsWith("WIFI:") -> ScanType.WIFI
            
            content.startsWith("geo:") || content.startsWith("GEO:") -> ScanType.GEO
            
            content.startsWith("BEGIN:VCARD") || content.startsWith("MECARD:") -> ScanType.CONTACT
            
            content.startsWith("upi://") || content.contains("pa=") && content.contains("pn=") -> ScanType.UPI
            
            else -> ScanType.TEXT
        }
    }
    
    private fun isEmail(text: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
        return emailPattern.matches(text)
    }
    
    private fun isPhone(text: String): Boolean {
        val phonePattern = "^[+]?[0-9]{10,13}$".toRegex()
        return phonePattern.matches(text.replace("\\s".toRegex(), ""))
    }
    
    fun getActionableData(content: String, type: ScanType): String {
        return when (type) {
            ScanType.URL -> {
                if (!content.startsWith("http://") && !content.startsWith("https://")) {
                    "https://$content"
                } else {
                    content
                }
            }
            ScanType.EMAIL -> {
                if (content.startsWith("mailto:")) content else "mailto:$content"
            }
            ScanType.PHONE -> {
                if (content.startsWith("tel:")) content else "tel:$content"
            }
            ScanType.SMS -> content
            ScanType.UPI -> content
            else -> content
        }
    }
    
    fun extractWifiInfo(content: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        if (!content.startsWith("WIFI:")) return map
        
        val parts = content.substring(5).split(";")
        for (part in parts) {
            val keyValue = part.split(":")
            if (keyValue.size == 2) {
                map[keyValue[0]] = keyValue[1]
            }
        }
        return map
    }
}
