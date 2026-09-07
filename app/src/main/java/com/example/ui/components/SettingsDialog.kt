package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.util.BillingFeedback
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.isTurkishLocale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    isProSubscriber: Boolean,
    trialDaysRemaining: Int = 10,
    lastSyncTime: String?,
    isEngineSoundEnabled: Boolean,
    isDarkMode: Boolean,
    appLanguage: String,
    currencyCode: String = "TRL",
    needsReviewCount: Int = 0,
    onToggleEngineSound: (Boolean) -> Unit,
    onToggleDarkMode: (Boolean) -> Unit,
    onLanguageSelected: (String) -> Unit,
    onCurrencySelected: (String) -> Unit = {},
    onExportCsv: (() -> Unit)? = null,
    onExportJson: (() -> Unit)? = null,
    onOpenPaywall: () -> Unit,
    onDisconnectCloud: () -> Unit,
    onRestorePurchases: (() -> Unit)? = null,
    onResetTrial: (() -> Unit)? = null,
    onExpireTrial: (() -> Unit)? = null,
    onOpenDiagnostics: (() -> Unit)? = null,
    onOpenNeedsReview: (() -> Unit)? = null,
    onOpenPriceSync: (() -> Unit)? = null,
    onOpenPrivacyPolicy: (() -> Unit)? = null,
    onLoadSampleData: (() -> Unit)? = null,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTr = isTurkishLocale()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showWebGuideDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.testTag("settings_modal_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 700.dp)
                .align(Alignment.CenterHorizontally)
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
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isTr) "Ayarlar & Lisans Durumu" else "Settings & License Status",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = if (isTr) "Kapat" else "Close"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // License & Trial Section (Lisans ve Deneme Durumu)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        isProSubscriber -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        trialDaysRemaining > 0 -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.35f)
                        else -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                    }
                ),
                border = BorderStroke(
                    1.dp,
                    when {
                        isProSubscriber -> MaterialTheme.colorScheme.primary
                        trialDaysRemaining > 0 -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.error
                    }
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isProSubscriber -> MaterialTheme.colorScheme.primary
                                            trialDaysRemaining > 0 -> MaterialTheme.colorScheme.tertiary
                                            else -> MaterialTheme.colorScheme.error
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when {
                                        isProSubscriber -> Icons.Default.WorkspacePremium
                                        trialDaysRemaining > 0 -> Icons.Default.Star
                                        else -> Icons.Default.Lock
                                    },
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isTr) "Uygulama Lisansı & Deneme" else "App License & Trial",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = when {
                                        isProSubscriber -> if (isTr) "PRO Tam Sürüm (Ömür Boyu)" else "PRO Lifetime License Active"
                                        trialDaysRemaining > 0 -> if (isTr) "10 Günlük Deneme Sürümü ($trialDaysRemaining Gün Kaldı)" else "10-Day Trial ($trialDaysRemaining Days Left)"
                                        else -> if (isTr) "Deneme Süresi Doldu ($15.00 Gerekli)" else "Trial Expired ($15.00 Required)"
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    color = when {
                                        isProSubscriber -> MaterialTheme.colorScheme.primary
                                        trialDaysRemaining > 0 -> MaterialTheme.colorScheme.tertiary
                                        else -> MaterialTheme.colorScheme.error
                                    },
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Status Badge
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = when {
                                isProSubscriber -> MaterialTheme.colorScheme.primary
                                trialDaysRemaining > 0 -> MaterialTheme.colorScheme.tertiary
                                else -> MaterialTheme.colorScheme.error
                            }
                        ) {
                            Text(
                                text = when {
                                    isProSubscriber -> "PRO"
                                    trialDaysRemaining > 0 -> if (isTr) "$trialDaysRemaining GÜN" else "$trialDaysRemaining DAYS"
                                    else -> if (isTr) "SÜRE DOLDU" else "EXPIRED"
                                },
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = when {
                            isProSubscriber -> {
                                if (isTr) "Tebrikler! Diecast Collection PRO tam sürüm lisansına sahipsiniz. Sınırsız model ekleme, bulut yedekleme, barkod okuma ve CSV dışa aktarma özellikleriniz ömür boyu aktiftir."
                                else "You have the full Diecast Collection PRO license. Unlimited cars, cloud backup, barcode scanner, and CSV exports are permanently unlocked."
                            }
                            trialDaysRemaining > 0 -> {
                                if (isTr) "10 günlük ücretsiz deneme süreniz devam ediyor ($trialDaysRemaining gün kaldı). Süre sonunda uygulamayı kullanmaya devam etmek için $15.00 tek seferlik ödeme yapabilirsiniz."
                                else "Your 10-day free trial is active ($trialDaysRemaining days left). Unlock lifetime full access for $15.00 once trial ends."
                            }
                            else -> {
                                if (isTr) "10 günlük ücretsiz deneme süreniz sona erdi. Koleksiyonunuza erişmeye ve yeni modeller eklemeye devam etmek için $15.00 ücretle tam sürüm lisansı alabilirsiniz."
                                else "Your 10-day trial has ended. Please purchase the full lifetime license for $15.00 to continue."
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (isProSubscriber && lastSyncTime != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isTr) "Bulut Senkronizasyon: $lastSyncTime" else "Cloud Sync: $lastSyncTime",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isProSubscriber) {
                        OutlinedButton(
                            onClick = onDisconnectCloud,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (isTr) "Lisansı / Bulut Bağlantısını Sıfırla" else "Reset License / Cloud Sync")
                        }
                    } else {
                        Button(
                            onClick = {
                                onDismiss()
                                onOpenPaywall()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (trialDaysRemaining > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (trialDaysRemaining > 0) {
                                    if (isTr) "PRO Sürüme Yükselt ($15.00)" else "Upgrade to PRO ($15.00)"
                                } else {
                                    if (isTr) "Tam Sürümü Satın Al ($15.00)" else "Purchase Full Version ($15.00)"
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (onRestorePurchases != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = onRestorePurchases,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isTr) "Satın Alımları Geri Yükle" else "Restore Purchases")
                            }
                        }
                    }

                    // Developer / Testing Buttons
                    if (onResetTrial != null || onExpireTrial != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (onResetTrial != null) {
                                OutlinedButton(
                                    onClick = onResetTrial,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(if (isTr) "Denemeyi Sıfırla (10 Gün)" else "Reset 10-Day Trial", fontSize = 11.sp)
                                }
                            }
                            if (onExpireTrial != null) {
                                OutlinedButton(
                                    onClick = onExpireTrial,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(if (isTr) "Denemeyi Bitir (Test)" else "Expire Trial (Test)", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Theme Selection Card (Görünüm & Tema)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isTr) "Görünüm & Tema" else "Appearance & Theme",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isTr) "Koyu veya açık arayüz temasını seçin" else "Choose dark or light interface theme",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = isDarkMode,
                            onClick = { onToggleDarkMode(true) },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.DarkMode,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (isTr) "Koyu Tema 🌙" else "Dark Theme 🌙", fontSize = 12.sp)
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f).testTag("chip_theme_dark")
                        )
                        FilterChip(
                            selected = !isDarkMode,
                            onClick = { onToggleDarkMode(false) },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LightMode,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (isTr) "Açık Tema ☀️" else "Light Theme ☀️", fontSize = 12.sp)
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f).testTag("chip_theme_light")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sound Settings Card (Motor Sesi Efekti)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isEngineSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isTr) "Motor Sesi Efekti" else "Engine Rev Sound",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isTr) "Yeni model eklenirken gaza basma motor sesi çalar" else "Play engine acceleration sound when adding a model",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Switch(
                        checked = isEngineSoundEnabled,
                        onCheckedChange = onToggleEngineSound,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("switch_engine_sound")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Language Selection Card (Dil Seçeneği)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isTr) "Uygulama Dili" else "App Language",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isTr) "Arayüz dilini seçin" else "Select interface language",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = appLanguage == "TR",
                            onClick = { onLanguageSelected("TR") },
                            label = { Text("Türkçe 🇹🇷", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = appLanguage == "EN",
                            onClick = { onLanguageSelected("EN") },
                            label = { Text("English 🇬🇧", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = appLanguage == "AUTO",
                            onClick = { onLanguageSelected("AUTO") },
                            label = { Text(if (isTr) "Otomatik" else "Auto", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Currency Selection Card (Döviz Kodu)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachMoney,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isTr) "Döviz Kodu / Para Birimi" else "Currency Code",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isTr) "Fiyat ve değer gösterimleri için döviz kodunu seçin" else "Select currency code for prices and values",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = currencyCode == "TRL",
                            onClick = { onCurrencySelected("TRL") },
                            label = { Text("TRL (₺)", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f).testTag("chip_currency_trl")
                        )
                        FilterChip(
                            selected = currencyCode == "USD",
                            onClick = { onCurrencySelected("USD") },
                            label = { Text("USD ($)", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f).testTag("chip_currency_usd")
                        )
                        FilterChip(
                            selected = currencyCode == "EUR",
                            onClick = { onCurrencySelected("EUR") },
                            label = { Text("EUR (€)", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f).testTag("chip_currency_eur")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Üretici Fiyatlandırma & Rayiç Güncelleme Kartı (Manufacturer Price Benchmark Sync)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDismiss()
                        onOpenPriceSync?.invoke()
                    }
                    .testTag("card_settings_price_sync"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PriceChange,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isTr) "Üretici Fiyatlandırma & Rayiçler" else "Manufacturer Price Benchmarks",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isTr) "Koleksiyon değerlerini üretici kataloglarına göre toplu güncelle" else "Batch update collection values from manufacturer catalogs",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Model Standard Check & Review Card (İnceleme Gerekenler)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDismiss()
                        onOpenNeedsReview?.invoke()
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (needsReviewCount > 0) Color(0xFFFEF3C7) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = BorderStroke(
                    1.dp,
                    if (needsReviewCount > 0) Color(0xFFD97706) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (needsReviewCount > 0) Color(0xFFF59E0B) else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (needsReviewCount > 0) Color.White else MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isTr) "Model Standart Kontrolü & İnceleme" else "Model Standard Check & Review",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (needsReviewCount > 0) Color(0xFF92400E) else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (needsReviewCount > 0) {
                                    if (isTr) "$needsReviewCount model standartlara uymuyor veya inceleme gerektiriyor"
                                    else "$needsReviewCount models need review or standard check"
                                } else {
                                    if (isTr) "Tüm model verileri standartlara uygun (0 İnceleme)"
                                    else "All model entries comply with standards (0 Issues)"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = if (needsReviewCount > 0) Color(0xFF78350F) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (needsReviewCount > 0) Color(0xFFD97706) else MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.testTag("btn_settings_needs_review")
                    ) {
                        Text(
                            text = if (isTr) "İncele ($needsReviewCount)" else "Review ($needsReviewCount)",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (needsReviewCount > 0) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // CSV Export Card (Koleksiyon Verilerini Dışa Aktar)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onExportCsv?.invoke()
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileDownload,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isTr) "Koleksiyonu CSV Olarak Dışa Aktar" else "Export Collection to CSV",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isTr) "Envanterinizi, fiyatları ve notları Excel/CSV formatında indirin veya paylaşın"
                                else "Backup or share your inventory, prices, and notes in Excel/CSV format",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(
                        onClick = { onExportCsv?.invoke() },
                        modifier = Modifier.testTag("btn_export_csv")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = if (isTr) "Dışa Aktar" else "Export",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (onExportJson != null) {
                Spacer(modifier = Modifier.height(16.dp))

                // Web JSON Export Card (Web Siteleri & API Uyumlu)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onExportJson.invoke()
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isTr) "Web & API İçin JSON Olarak Dışa Aktar" else "Export Collection to Web JSON",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isTr) "Web siteleri, veritabanları (MongoDB, Firebase) ve API'ler ile tam uyumlu JSON"
                                    else "Standard JSON format for web backends, cloud databases, and APIs",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        IconButton(
                            onClick = { onExportJson.invoke() },
                            modifier = Modifier.testTag("btn_export_json")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = if (isTr) "JSON Dışa Aktar" else "Export JSON",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Web & Cross-Platform Publishing Guide Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showWebGuideDialog = true
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isTr) "Web'de Yayınlama & Masaüstü Rehberi" else "Web & Desktop Publishing Guide",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isTr) "Tarayıcı, ChromeOS, Compose Multiplatform (Wasm) ve Web API entegrasyonu"
                                else "Browser, ChromeOS, Compose Multiplatform (Wasm) and Web API integrations",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (onLoadSampleData != null) {
                Spacer(modifier = Modifier.height(16.dp))

                // 30 Sample Diecast Data Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onLoadSampleData.invoke()
                            onDismiss()
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isTr) "30 Örnek Model & Fotoğraf Yükle" else "Load 30 Sample Models & Photos",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isTr) "Gerçek diecast arabalara ait 30 zengin veriyi ve fotoğrafları koleksiyona yükler"
                                    else "Loads 30 authentic diecast models and car photos into collection",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        IconButton(
                            onClick = {
                                onLoadSampleData.invoke()
                                onDismiss()
                            },
                            modifier = Modifier.testTag("btn_load_sample_data")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = if (isTr) "Yükle" else "Load",
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // System Diagnostics Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDismiss()
                        onOpenDiagnostics?.invoke()
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HealthAndSafety,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isTr) "Sistem Sağlığı & Tanılama" else "System Health & Diagnostics",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isTr) "Görsel, veritabanı ve bellek testlerini çalıştır" else "Run image, database, and memory tests",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Google Play Integrity Status Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDismiss()
                        onOpenDiagnostics?.invoke()
                    }
                    .testTag("card_play_integrity"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2E7D32)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Google Play Integrity",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF2E7D32).copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = if (isTr) "Aktif" else "Active",
                                        color = Color(0xFF2E7D32),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (isTr) "Orijinal APK ve donanım güvenlik koruması devrede" else "Official binary & device security attestation active",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Privacy Policy Card (Gizlilik Politikası)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDismiss()
                        onOpenPrivacyPolicy?.invoke()
                    }
                    .testTag("card_privacy_policy"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PrivacyTip,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isTr) "Gizlilik Politikası" else "Privacy Policy",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isTr) "Veri güvenliği, kamera ve yerel saklama kuralları" else "Data safety, camera permissions & local storage",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App Information Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.img_diecast_logo_1785160977141),
                            contentDescription = "Diecast Collection Logo",
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Diecast Collection",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isTr) "Model Araba Koleksiyon Yöneticisi" else "Diecast Model Collection Manager",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (isTr) "Sürüm" else "Version", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "23.0 (Build 23)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Play Integrity", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = if (isTr) "Aktif & Korumalı" else "Active & Protected",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (isTr) "Seçili Dil" else "Selected Language", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = when (appLanguage) {
                                "TR" -> "Türkçe"
                                "EN" -> "English"
                                else -> if (isTr) "Otomatik (Sistem)" else "Auto (System)"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (isTr) "Döviz Kodu" else "Currency Code", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = currencyCode,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }

    if (showWebGuideDialog) {
        WebPublishingGuideDialog(
            isTr = isTr,
            onDismiss = { showWebGuideDialog = false }
        )
    }
}

@Composable
fun WebPublishingGuideDialog(
    isTr: Boolean,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isTr) "Web'de Yayınlama Rehberi" else "Web Publishing Guide",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Feature 1
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (isTr) "1. Geniş Ekran & Web Tarayıcı Uyumu" else "1. Responsive Large Screen & Web Layout",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isTr) "Uygulama tam duyarlı (responsive) ızgara (Grid), fare imleci vurgusu (hover effect) ve klavye araması ile masaüstü tarayıcılara ve ChromeOS ekranlarına hazır hale getirilmiştir."
                            else "The app has been adapted with responsive adaptive grid, mouse hover feedback, and physical keyboard support for desktop web viewports.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Feature 2
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (isTr) "2. Web / REST API & JSON Entegrasyonu" else "2. Web / REST API & JSON Data Portability",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isTr) "Koleksiyonunuzdaki tüm modelleri standart Web JSON formatında dışa aktarabilir; Node.js, Python, Firebase veya MongoDB gibi web sitenizin veritabanına aktarabilirsiniz."
                            else "Export your entire collection in standard Web JSON schema to sync with your website's database (MongoDB, Firebase, PostgreSQL, REST API).",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Feature 3
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (isTr) "3. Compose Multiplatform (Web / Wasm)" else "3. Compose Multiplatform (Web / Wasm)",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isTr) "Yazdığımız Jetpack Compose UI bileşenleri Kotlin Multiplatform / Compose Multiplatform ile doğrudan uyumludur. Projeyi ZIP olarak indirip Wasm (WebAssembly) target'ı ile saf web sitesi olarak derleyebilirsiniz."
                            else "The UI components are written in Compose standards and can be targeted to WebAssembly (Wasm) using Compose Multiplatform to run natively on the web.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Feature 4
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (isTr) "4. ChromeOS & Web Streaming" else "4. ChromeOS & Web Streaming",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isTr) "AndroidManifest.xml içerisinde serbest boyutlandırılabilir pencere (resizeableActivity) ve dokunmatik ekran zorunluluğu kaldırılmıştır. Web emülatörlerinde ve Chromebook'larda pencere boyutu dilediğiniz gibi değiştirilebilir."
                            else "Freeform window resizing and optional touchscreen declarations are enabled, making it run seamlessly in web emulators and ChromeOS.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(if (isTr) "Anladım" else "Got It")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallDialog(
    trialDaysRemaining: Int = 10,
    isTrialExpired: Boolean = false,
    isProSubscriber: Boolean = false,
    formattedPrice: String = "$15.00",
    billingFeedback: BillingFeedback? = null,
    isConnecting: Boolean = false,
    onDismissFeedback: () -> Unit = {},
    onRestorePurchases: () -> Unit = {},
    onOpenPrivacyPolicy: (() -> Unit)? = null,
    onSubscribe: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTr = isTurkishLocale()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = {
            if (!isTrialExpired || isProSubscriber) {
                onDismiss()
            }
        },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.testTag("paywall_modal_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 700.dp)
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Close Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when {
                        isProSubscriber -> MaterialTheme.colorScheme.primaryContainer
                        isTrialExpired -> MaterialTheme.colorScheme.errorContainer
                        else -> MaterialTheme.colorScheme.primaryContainer
                    }
                ) {
                    Text(
                        text = when {
                            isProSubscriber -> if (isTr) "TAM SÜRÜM SATIN ALINDI (PRO)" else "FULL VERSION PURCHASED (PRO)"
                            isTrialExpired -> if (isTr) "DENEME SÜRESİ DOLDU" else "TRIAL EXPIRED"
                            else -> if (isTr) "10 GÜNLÜK ÜCRETSİZ DENEME ($trialDaysRemaining GÜN KALDI)" else "10-DAY TRIAL ($trialDaysRemaining DAYS LEFT)"
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isProSubscriber -> MaterialTheme.colorScheme.primary
                            isTrialExpired -> MaterialTheme.colorScheme.onErrorContainer
                            else -> MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )
                }

                if (!isTrialExpired || isProSubscriber) {
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = if (isTr) "Kapat" else "Close")
                    }
                } else {
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }

            // Billing Feedback Banner (Success / Cancellation / Network Error / Failure)
            if (billingFeedback != null) {
                Spacer(modifier = Modifier.height(12.dp))
                BillingFeedbackBanner(
                    feedback = billingFeedback,
                    isTr = isTr,
                    onDismiss = onDismissFeedback,
                    onRetry = onSubscribe
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Crown / PRO Badge Icon
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isProSubscriber -> MaterialTheme.colorScheme.primaryContainer
                            isTrialExpired -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                            else -> MaterialTheme.colorScheme.primaryContainer
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        isProSubscriber -> Icons.Default.WorkspacePremium
                        isTrialExpired -> Icons.Default.Lock
                        else -> Icons.Default.WorkspacePremium
                    },
                    contentDescription = null,
                    modifier = Modifier.size(38.dp),
                    tint = when {
                        isProSubscriber -> MaterialTheme.colorScheme.primary
                        isTrialExpired -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.primary
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isProSubscriber) {
                    if (isTr) "PRO Tam Sürüm Aktif" else "PRO Full License Active"
                } else "Diecast Collection PRO",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = when {
                    isProSubscriber -> {
                        if (isTr) "Tebrikler! Satın alımınız başarıyla onaylandı. Ömür boyu sınırsız model ekleme ve bulut özellikleri aktiftir."
                        else "Congratulations! Lifetime full access is unlocked for your account."
                    }
                    isTrialExpired -> {
                        if (isTr) "10 günlük ücretsiz deneme süreniz sona ermiştir. Koleksiyonunuza erişmeye ve yeni model eklemeye devam etmek için lütfen tam sürümü satın alın."
                        else "Your 10-day free trial has ended. Please purchase the full lifetime license to continue."
                    }
                    else -> {
                        if (isTr) "10 günlük ücretsiz deneme süresi sonrasında $15.00 ile ömür boyu tam erişim"
                        else "10 days free trial, then $15.00 for lifetime unlimited access"
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Highlighted Pricing Card ($15 USD Lifetime)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isTr) "Ömür Boyu Tam Lisans" else "Lifetime Full License",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = if (isTr) "TEK SEFERLİK" else "ONE-TIME",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isTr) "10 gün deneme dahil • Tek ödeme ile sınırsız model ve bulut erişimi"
                            else "10-day trial included • One-time purchase for unlimited cars & cloud sync",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = formattedPrice,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (formattedPrice.contains("$") || formattedPrice.contains("USD")) "USD" else "",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Feature Checklist
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProFeatureItem(
                    title = if (isTr) "10 Günlük Ücretsiz Deneme Süresi" else "10-Day Free Trial Period",
                    description = if (isTr) "İlk 10 gün tüm özellikleri ücretsiz deneyin" else "Enjoy all features free for the first 10 days"
                )
                ProFeatureItem(
                    title = if (isTr) "Sınırsız Model & Fotoğraf Kaydı" else "Unlimited Models & HD Photos",
                    description = if (isTr) "Binlerce arabayı yüksek çözünürlüklü fotoğraflarla saklayın" else "Catalog thousands of diecast cars with gallery photos"
                )
                ProFeatureItem(
                    title = if (isTr) "Otomatik Bulut Yedekleme & Senkronizasyon" else "Cloud Backup & Multi-Device Sync",
                    description = if (isTr) "Koleksiyonunuz güvenle buluta yedeklenir, telefon ve tabletlerinizde senkronize olur" else "Continuous cloud backup across phones and tablets"
                )
                ProFeatureItem(
                    title = if (isTr) "Barkod Tarayıcı ile Hızlı Model Tanıma" else "Barcode Scanner & Instant Lookup",
                    description = if (isTr) "Kamera ile barkod okutarak saniyeler içinde model ekleyin" else "Scan barcodes to quickly identify and catalog models"
                )
                ProFeatureItem(
                    title = if (isTr) "Excel / CSV Dışa Aktarma & Değerleme Raporları" else "Excel / CSV Export & Valuation Stats",
                    description = if (isTr) "Koleksiyonunuzun toplam piyasa değerini ve envanterini dışa aktarın" else "Export full inventory and generate valuation charts"
                )
                ProFeatureItem(
                    title = if (isTr) "Reklamsız ve Kesintisiz Kullanım" else "100% Ad-Free Experience",
                    description = if (isTr) "Hiçbir reklam olmadan akıcı ve hızlı koleksiyon yönetimi" else "Clean, lightning-fast catalog with zero ads"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Subscription / Purchase CTA Button
            Button(
                onClick = onSubscribe,
                enabled = !isConnecting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btn_purchase_pro"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isConnecting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isTr) "Google Play'e Bağlanıyor..." else "Connecting to Google Play...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isTr) "Tam Sürümü Satın Al ($formattedPrice)" else "Unlock Full License ($formattedPrice)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Restore Purchases CTA
            OutlinedButton(
                onClick = onRestorePurchases,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isTr) "Daha Önce Satın Alındı mı? Geri Yükle" else "Already Purchased? Restore Purchases",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isTr) "10 günlük deneme süresi sonrasında $formattedPrice tek seferlik ödeme ile ömür boyu sınırsız kullanım."
                else "10 days free trial, then $formattedPrice one-time payment for lifetime access.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            if (onOpenPrivacyPolicy != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.clickable { onOpenPrivacyPolicy() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PrivacyTip,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isTr) "Gizlilik Politikası & Veri Güvenliği" else "Privacy Policy & Data Protection",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun BillingFeedbackBanner(
    feedback: BillingFeedback,
    isTr: Boolean,
    onDismiss: () -> Unit,
    onRetry: () -> Unit
) {
    val (bgColor, icon, tintColor, titleText, descText, showRetry) = when (feedback) {
        is BillingFeedback.Success -> {
            Tuple6(
                MaterialTheme.colorScheme.primaryContainer,
                Icons.Default.CheckCircle,
                MaterialTheme.colorScheme.primary,
                if (isTr) "Başarılı!" else "Success!",
                feedback.message,
                false
            )
        }
        is BillingFeedback.Cancelled -> {
            Tuple6(
                MaterialTheme.colorScheme.surfaceVariant,
                Icons.Default.Info,
                MaterialTheme.colorScheme.onSurfaceVariant,
                if (isTr) "İşlem İptal Edildi" else "Purchase Cancelled",
                feedback.message,
                false
            )
        }
        is BillingFeedback.NetworkError -> {
            Tuple6(
                MaterialTheme.colorScheme.errorContainer,
                Icons.Default.WifiOff,
                MaterialTheme.colorScheme.error,
                if (isTr) "Bağlantı Hatası" else "Network Error",
                feedback.message,
                true
            )
        }
        is BillingFeedback.ItemAlreadyOwned -> {
            Tuple6(
                MaterialTheme.colorScheme.secondaryContainer,
                Icons.Default.WorkspacePremium,
                MaterialTheme.colorScheme.secondary,
                if (isTr) "Lisans Zaten Mevcut" else "License Already Owned",
                feedback.message,
                false
            )
        }
        is BillingFeedback.Error -> {
            Tuple6(
                MaterialTheme.colorScheme.errorContainer,
                Icons.Default.ErrorOutline,
                MaterialTheme.colorScheme.error,
                if (isTr) "Satın Alma Başarısız" else "Purchase Failed",
                feedback.message,
                true
            )
        }
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tintColor,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = tintColor,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Kapat",
                        modifier = Modifier.size(16.dp),
                        tint = tintColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = descText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (showRetry) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = {
                            onDismiss()
                            onRetry()
                        },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = if (isTr) "Tekrar Dene" else "Retry",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private data class Tuple6<A, B, C, D, E, F>(
    val a: A,
    val b: B,
    val c: C,
    val d: D,
    val e: E,
    val f: F
)

@Composable
private fun ProFeatureItem(title: String, description: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun PlanCard(
    title: String,
    price: String,
    subtitle: String,
    badge: String?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            2.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = isSelected,
                    onClick = onClick
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        if (badge != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = badge,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                    Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(text = price, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
        }
    }
}
