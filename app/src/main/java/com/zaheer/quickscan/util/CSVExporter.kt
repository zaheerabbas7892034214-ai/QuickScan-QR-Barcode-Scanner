package com.zaheer.quickscan.util

import android.content.Context
import android.net.Uri
import com.zaheer.quickscan.data.model.ScanHistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object CSVExporter {
    
    suspend fun exportToCSV(
        context: Context,
        scans: List<ScanHistoryEntity>,
        uri: Uri
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                
                // Write header
                val header = "ID,Content,Type,Format,Timestamp,Is Favorite\n"
                outputStream.write(header.toByteArray())
                
                // Write data
                scans.forEach { scan ->
                    val date = dateFormat.format(Date(scan.timestamp))
                    val line = "${scan.id},\"${escapeCsv(scan.content)}\",${scan.type},${scan.format},$date,${scan.isFavorite}\n"
                    outputStream.write(line.toByteArray())
                }
                
                outputStream.flush()
            } ?: return@withContext Result.failure(IOException("Failed to open output stream"))
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun escapeCsv(value: String): String {
        return value.replace("\"", "\"\"").replace("\n", " ")
    }
    
    fun generateFileName(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        return "QuickScan_Export_${dateFormat.format(Date())}.csv"
    }
}
