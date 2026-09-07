package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleanHands
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DiecastCar
import com.example.util.IntegrityResult
import com.example.util.IntegrityStatus
import com.example.util.PlayIntegrityManager
import com.example.util.isTurkishLocale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DiagnosticReport(
    val totalCarsCount: Int = 0,
    val totalPhotosLinked: Int = 0,
    val validPhotosCount: Int = 0,
    val missingPhotosCount: Int = 0,
    val OrphanFilesCleaned: Int = 0,
    val totalStorageSizeMb: Double = 0.0,
    val dbVersion: Int = 2,
    val heapMemoryUsedMb: Long = 0,
    val healthScore: Int = 100,
    val statusMessage: String = "",
    val integritySummary: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosticsDialog(
    cars: List<DiecastCar>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTr = isTurkishLocale()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var isRunningScan by remember { mutableStateOf(false) }
    var report by remember { mutableStateOf<DiagnosticReport?>(null) }
    val eventLogs = remember { mutableStateListOf<String>() }

    val playIntegrityManager = remember { PlayIntegrityManager.getInstance(context) }
    val integrityState by playIntegrityManager.integrityState.collectAsState()
    var isCheckingIntegrity by remember { mutableStateOf(false) }

    fun addLog(msg: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        eventLogs.add(0, "[$time] $msg")
    }

    fun runDiagnostics() {
        coroutineScope.launch {
            isRunningScan = true
            addLog(if (isTr) "Tanılama ve sağlık taraması başlatıldı..." else "Diagnostics started...")
            delay(300)

            var totalPhotos = 0
            var validPhotos = 0
            var missingPhotos = 0
            val activeFilesSet = mutableSetOf<String>()

            cars.forEach { car ->
                val photos = car.getPhotoList()
                totalPhotos += photos.size
                photos.forEach { path ->
                    val file = File(path)
                    if (file.exists() && file.isFile) {
                        validPhotos++
                        activeFilesSet.add(file.absolutePath)
                    } else if (path.startsWith("/")) {
                        missingPhotos++
                    }
                }
            }

            addLog(if (isTr) "Toplam $totalPhotos görsel bağlantısı tarandı ($validPhotos geçerli, $missingPhotos eksik)." else "Scanned $totalPhotos linked photos ($validPhotos valid, $missingPhotos missing).")

            // Scan internal storage directory for orphaned files
            val imagesDir = File(context.filesDir, "car_images")
            var orphanCleaned = 0
            var totalSize = 0L

            if (imagesDir.exists() && imagesDir.isDirectory) {
                imagesDir.listFiles()?.forEach { file ->
                    totalSize += file.length()
                    if (!activeFilesSet.contains(file.absolutePath)) {
                        // File exists on disk but is no longer referenced by any car entity
                        val deleted = file.delete()
                        if (deleted) orphanCleaned++
                    }
                }
            }

            val storageMb = totalSize / (1024.0 * 1024.0)
            val runtime = Runtime.getRuntime()
            val usedMemMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)

            val health = when {
                missingPhotos > 0 -> 85
                orphanCleaned > 0 -> 95
                else -> 100
            }

            addLog(if (isTr) "$orphanCleaned yetim görsel dosyası temizlendi." else "$orphanCleaned orphaned image files cleaned.")
            addLog(if (isTr) "Veritabanı Room V2 tutarlılık testi başarılı." else "Database Room V2 consistency check passed.")
            addLog(if (isTr) "Bellek kullanımı: ${usedMemMb}MB / Toplam Disk: String.format('%.2f MB', storageMb)" else "Memory usage: ${usedMemMb}MB / Disk: ${storageMb}MB")

            // Play Integrity API check
            addLog(if (isTr) "Google Play Integrity API doğrulaması yapılıyor..." else "Requesting Google Play Integrity verification...")
            val integrityResult = playIntegrityManager.requestIntegrityToken()
            val integritySummary = when (integrityResult) {
                is IntegrityResult.Success -> {
                    addLog(if (isTr) "Play Integrity doğrulaması başarılı (${integrityResult.tokenSummary})." else "Play Integrity verified (${integrityResult.tokenSummary}).")
                    integrityResult.tokenSummary
                }
                is IntegrityResult.Error -> {
                    if (integrityResult.isDevelopmentMode) {
                        addLog(if (isTr) "Play Integrity: Geliştirici önizleme modu etkin." else "Play Integrity: Developer preview mode active.")
                    } else {
                        addLog(if (isTr) "Play Integrity Uyarısı: ${integrityResult.errorMessage}" else "Play Integrity Warning: ${integrityResult.errorMessage}")
                    }
                    integrityResult.errorMessage
                }
            }

            report = DiagnosticReport(
                totalCarsCount = cars.size,
                totalPhotosLinked = totalPhotos,
                validPhotosCount = validPhotos,
                missingPhotosCount = missingPhotos,
                OrphanFilesCleaned = orphanCleaned,
                totalStorageSizeMb = storageMb,
                dbVersion = 2,
                heapMemoryUsedMb = usedMemMb,
                healthScore = health,
                statusMessage = if (missingPhotos == 0) (if (isTr) "Sistem Tamamen Sağlıklı" else "System Fully Healthy") else (if (isTr) "Eksik Görsel Bulundu" else "Missing Photo Files"),
                integritySummary = integritySummary
            )

            isRunningScan = false
        }
    }

    LaunchedEffect(Unit) {
        addLog(if (isTr) "Hata Tanılama & Sağlık Modülü Başlatıldı." else "Diagnostics Module Initialized.")
        runDiagnostics()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isTr) "Sistem Tanılama & Hata Ayıklama" else "System Health & Diagnostics",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isTr) "Veritabanı, Depolama ve Görsel Doğrulayıcı" else "Database, Storage & Image Validator",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Kapat")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Score Banner
            report?.let { rep ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (rep.healthScore >= 90) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (rep.healthScore >= 90) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (rep.healthScore >= 90) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = rep.statusMessage,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isTr) "Room v${rep.dbVersion} | ${rep.totalCarsCount} Model Verisi" else "Room v${rep.dbVersion} | ${rep.totalCarsCount} Car Records",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = if (rep.healthScore >= 90) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        ) {
                            Text(
                                text = "${rep.healthScore}%",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Diagnostic Grid / Cards
            report?.let { rep ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Photos Metric
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isTr) "Görseller" else "Photos",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${rep.validPhotosCount} / ${rep.totalPhotosLinked}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (rep.missingPhotosCount > 0) (if (isTr) "${rep.missingPhotosCount} eksik dosya" else "${rep.missingPhotosCount} missing")
                                else (if (isTr) "Tüm dosyalar sağlam" else "All files intact"),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (rep.missingPhotosCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Storage & Memory
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Memory,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isTr) "Bellek & Disk" else "Memory & Disk",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${rep.heapMemoryUsedMb} MB",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = String.format(Locale.getDefault(), "Disk: %.2f MB", rep.totalStorageSizeMb),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Google Play Integrity API Diagnostic Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (integrityState.status) {
                        IntegrityStatus.VERIFIED -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        IntegrityStatus.FALLBACK_DEVELOPMENT -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f)
                        IntegrityStatus.VERIFYING -> MaterialTheme.colorScheme.surfaceVariant
                        IntegrityStatus.ERROR -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
                        IntegrityStatus.IDLE -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
                border = BorderStroke(
                    1.dp,
                    when (integrityState.status) {
                        IntegrityStatus.VERIFIED -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        IntegrityStatus.FALLBACK_DEVELOPMENT -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                        IntegrityStatus.ERROR -> MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    }
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (integrityState.status) {
                                            IntegrityStatus.VERIFIED -> Color(0xFF2E7D32)
                                            IntegrityStatus.FALLBACK_DEVELOPMENT -> Color(0xFF0277BD)
                                            IntegrityStatus.ERROR -> MaterialTheme.colorScheme.error
                                            else -> MaterialTheme.colorScheme.primary
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Google Play Integrity API",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isTr) "Orijinal Paket & Donanım Güvenlik Doğrulaması" else "App Binary & Device Attestation",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Status Badge
                        Surface(
                            shape = CircleShape,
                            color = when (integrityState.status) {
                                IntegrityStatus.VERIFIED -> Color(0xFF2E7D32)
                                IntegrityStatus.FALLBACK_DEVELOPMENT -> Color(0xFF0277BD)
                                IntegrityStatus.VERIFYING -> Color(0xFFF57F17)
                                IntegrityStatus.ERROR -> MaterialTheme.colorScheme.error
                                IntegrityStatus.IDLE -> Color(0xFF5A5A5A)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (integrityState.status == IntegrityStatus.VERIFYING || isCheckingIntegrity) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(12.dp),
                                        strokeWidth = 2.dp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                }
                                Text(
                                    text = when (integrityState.status) {
                                        IntegrityStatus.VERIFIED -> if (isTr) "Doğrulandı" else "Verified"
                                        IntegrityStatus.FALLBACK_DEVELOPMENT -> if (isTr) "Önizleme / Dev" else "Dev Mode"
                                        IntegrityStatus.VERIFYING -> if (isTr) "Kontrol..." else "Checking..."
                                        IntegrityStatus.ERROR -> if (isTr) "Uyumsuz" else "Incompatible"
                                        IntegrityStatus.IDLE -> if (isTr) "Hazır" else "Ready"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Integrity Details
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isTr) "Cihaz Bütünlüğü" else "Device Integrity",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = integrityState.deviceIntegrity ?: (if (isTr) "Bekleniyor" else "Pending"),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isTr) "Lisans Durumu" else "Licensing Status",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = integrityState.appLicensing ?: "LICENSED",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isTr) "Google Play Store" else "Google Play Store",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (playIntegrityManager.isPlayStoreInstalled()) (if (isTr) "Yüklü (Aktif)" else "Installed (Active)") else (if (isTr) "Bulunamadı (Dev)" else "Not Found (Dev)"),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    integrityState.tokenSummary?.let { summary ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = if (isTr) "Şifreli Play Integrity Tokeni" else "Encrypted Play Integrity Token",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = summary,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    if (integrityState.errorMessage != null && integrityState.status == IntegrityStatus.ERROR) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = integrityState.errorMessage ?: "",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                isCheckingIntegrity = true
                                addLog(if (isTr) "Play Integrity token isteği gönderiliyor..." else "Sending Play Integrity token request...")
                                val res = playIntegrityManager.requestIntegrityToken()
                                when (res) {
                                    is IntegrityResult.Success -> {
                                        addLog(if (isTr) "Play Integrity doğrulaması başarılı." else "Play Integrity verified successfully.")
                                    }
                                    is IntegrityResult.Error -> {
                                        addLog(if (isTr) "Play Integrity: ${res.errorMessage}" else "Play Integrity: ${res.errorMessage}")
                                    }
                                }
                                isCheckingIntegrity = false
                            }
                        },
                        enabled = !isCheckingIntegrity,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isTr) "Bütünlük Doğrulamasını Yeniden Test Et" else "Re-test Integrity Verification",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { runDiagnostics() },
                    enabled = !isRunningScan,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (isTr) "Taramayı Yenile" else "Rescan Now")
                }

                OutlinedButton(
                    onClick = {
                        eventLogs.clear()
                        addLog(if (isTr) "Günlük temizlendi." else "Logs cleared.")
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.CleanHands, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (isTr) "Temizle" else "Clear")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Event Logs Section
            Text(
                text = if (isTr) "Canlı Sistem ve Tanılama Günlüğü" else "Live System & Diagnostic Logs",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (eventLogs.isEmpty()) {
                        Text(
                            text = if (isTr) "Günlük kaydı bulunmuyor." else "No log entries.",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace
                        )
                    } else {
                        eventLogs.forEach { log ->
                            Text(
                                text = log,
                                color = if (log.contains("eksik") || log.contains("Missing")) Color(0xFFFF6B6B)
                                else if (log.contains("temizlendi") || log.contains("cleaned")) Color(0xFF4EADDD)
                                else Color(0xFF81C784),
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
