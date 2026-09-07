package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.isTurkishLocale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTr = isTurkishLocale()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.testTag("privacy_policy_sheet")
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
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PrivacyTip,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isTr) "Gizlilik Politikası" else "Privacy Policy",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isTr) "Veri Güvenliği & Gizlilik Taahhüdü" else "Data Protection & Privacy Commitment",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("btn_close_privacy_policy")) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = if (isTr) "Kapat" else "Close"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Highlight Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isTr) {
                            "Gizliliğinize saygı duyuyoruz. Koleksiyon verileriniz, fotoğraflarınız ve notlarınız yalnızca sizin kontrolünüzdedir ve asla üçüncü taraflara satılmaz."
                        } else {
                            "We respect your privacy. Your collection data, photos, and notes remain under your full control and are never sold to third parties."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 1: Data Storage & Privacy
            PolicySectionCard(
                icon = Icons.Default.Storage,
                title = if (isTr) "1. Veri Depolama & Yerel Gizlilik" else "1. Data Storage & Local Privacy",
                description = if (isTr) {
                    "Koleksiyonunuza eklediğiniz tüm diecast model bilgileri (marka, ölçek, satın alma fiyatı, saklama kutusu, özel notlar vb.) ve fotoğraflar varsayılan olarak cihazınızın yerel depolama alanında (Room SQLite Veritabanı) güvenli bir şekilde saklanır."
                } else {
                    "All diecast model records (brand, scale, purchase price, storage box, custom notes, etc.) and photos added to your collection are securely stored locally on your device's internal storage (Room SQLite Database) by default."
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Section 2: Camera & Permissions
            PolicySectionCard(
                icon = Icons.Default.CameraAlt,
                title = if (isTr) "2. Kamera & İzin Kullanımı" else "2. Camera & Permissions",
                description = if (isTr) {
                    "Uygulama, modellerinizin fotoğraflarını çekmek veya barkod tarayıcı ile model kodlarını hızlıca okumak amacıyla kamera izni talep edebilir. Kamera görüntüsü hiçbir uzak sunucuya aktarılmaz; yalnızca yerel tarama ve fotoğraf kaydı amacıyla işlenir."
                } else {
                    "The app may request Camera permissions solely to take model photos or scan barcodes for instant cataloging. Camera frames are processed entirely on-device and are never uploaded or streamed to external servers."
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Section 3: Google Play Billing & Payments
            PolicySectionCard(
                icon = Icons.Default.CreditCard,
                title = if (isTr) "3. Ödemeler & Google Play Faturalandırma" else "3. Payments & Google Play Billing",
                description = if (isTr) {
                    "Uygulama içi satın alımlar ve PRO lisans işlemleri doğrudan resmi Google Play Billing altyapısı üzerinden gerçekleştirilir. Kredi kartı, banka veya ödeme bilgileriniz tarafımızca asla görüntülenemez, işlenemez veya saklanamaz."
                } else {
                    "In-app purchases and PRO licenses are processed securely through the official Google Play Billing system. Your payment and credit card credentials are never visible, stored, or processed by us."
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Section 4: Data Export & Ownership
            PolicySectionCard(
                icon = Icons.Default.FileDownload,
                title = if (isTr) "4. Veri Sahipliği & Dışa Aktarma (CSV)" else "4. Data Ownership & Export (CSV)",
                description = if (isTr) {
                    "Koleksiyon verileriniz tamamen size aittir. İstediğiniz zaman Ayarlar menüsünden tüm envanterinizi CSV/Excel formatında dışa aktarabilir, yedekleyebilir veya cihazınızdan tamamen silebilirsiniz."
                } else {
                    "You retain 100% ownership of your catalog data. You can export your full inventory to CSV/Excel format at any time via Settings, backup your files, or permanently delete records from your device."
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Section 5: Third-Party Analytics & Tracking
            PolicySectionCard(
                icon = Icons.Default.Lock,
                title = if (isTr) "5. Üçüncü Taraf Takip & Reklamlar" else "5. Third-Party Analytics & Ads",
                description = if (isTr) {
                    "Uygulamamız kullanıcı verilerini izleyen reklam veya pazarlama amaçlı üçüncü taraf takip yazılımları (trackers) içermez. Deneyiminiz tamamen gizli ve güvenlidir."
                } else {
                    "Our app contains zero third-party advertising trackers or behavioral analytics SDKs. Your collecting experience is fully confidential and secure."
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(12.dp))

            // Version & Effective Date Footer
            Text(
                text = if (isTr) "Son Güncelleme: 2026 • Diecast Collection Manager"
                else "Last Updated: 2026 • Diecast Collection Manager",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_privacy_policy_confirm"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isTr) "Anladım ve Kabul Ediyorum" else "I Understand & Accept",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PolicySectionCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
        }
    }
}
