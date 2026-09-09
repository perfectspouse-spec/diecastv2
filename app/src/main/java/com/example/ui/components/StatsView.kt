package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.DiecastCar
import com.example.util.formatAmount
import com.example.util.isTurkishLocale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

data class CategoryDetails(
    val categoryTitle: String,
    val categoryName: String,
    val cars: List<DiecastCar>
)

@Composable
fun StatsView(
    allCars: List<DiecastCar>,
    currencyCode: String = "TRL",
    onCarClick: ((DiecastCar) -> Unit)? = null,
    onOpenPriceSync: (() -> Unit)? = null,
    lastPriceSyncDate: String? = null,
    modifier: Modifier = Modifier
) {
    val isTr = isTurkishLocale()
    val collectionCars = allCars.filter { !it.isWishlist }
    val wishlistCars = allCars.filter { it.isWishlist }

    val totalCount = collectionCars.size
    val totalWishlistCount = wishlistCars.size

    val totalSpent = collectionCars.sumOf { it.purchasePrice }
    val totalEstimatedValue = collectionCars.sumOf { it.estimatedValue }
    val valueGain = totalEstimatedValue - totalSpent

    var selectedCategoryDetails by remember { mutableStateOf<CategoryDetails?>(null) }

    // Grouping Stats
    val manufacturerCounts = collectionCars.groupBy { it.manufacturer }
        .mapValues { it.value.size }
        .toList()
        .sortedByDescending { it.second }

    val carBrandCounts = collectionCars.groupBy { it.carBrand }
        .mapValues { it.value.size }
        .toList()
        .sortedByDescending { it.second }

    val scaleCounts = collectionCars.groupBy { it.scale.ifBlank { "1:64" } }
        .mapValues { it.value.size }
        .toList()
        .sortedByDescending { it.second }

    val conditionCounts = collectionCars.groupBy { it.condition.ifBlank { if (isTr) "Diğer" else "Other" } }
        .mapValues { it.value.size }
        .toList()
        .sortedByDescending { it.second }

    val topValueCars = collectionCars.sortedByDescending { it.estimatedValue }.take(3)

    fun selectCategory(title: String, name: String, items: List<Pair<String, Int>>) {
        val top5Names = items.take(5).map { it.first }
        val isOther = name == (if (isTr) "Diğer" else "Other")

        val filteredCars = when (title) {
            if (isTr) "Üretici Marka Dağılımı" else "Manufacturer Distribution" -> {
                if (isOther) {
                    collectionCars.filter { it.manufacturer !in top5Names }
                } else {
                    collectionCars.filter { it.manufacturer.equals(name, ignoreCase = true) }
                }
            }
            if (isTr) "Araba Markası Dağılımı" else "Car Brand Breakdown" -> {
                if (isOther) {
                    collectionCars.filter { it.carBrand !in top5Names }
                } else {
                    collectionCars.filter { it.carBrand.equals(name, ignoreCase = true) }
                }
            }
            if (isTr) "Ölçek Dağılımı" else "Scale Breakdown" -> {
                if (isOther) {
                    collectionCars.filter { it.scale !in top5Names }
                } else {
                    collectionCars.filter {
                        it.scale.equals(name, ignoreCase = true) || (it.scale.isBlank() && name == "1:64")
                    }
                }
            }
            if (isTr) "Paket & Hasar Durumu" else "Condition & Packaging" -> {
                if (isOther) {
                    collectionCars.filter { it.condition !in top5Names }
                } else {
                    collectionCars.filter {
                        it.condition.equals(name, ignoreCase = true) || (it.condition.isBlank() && name in listOf("Diğer", "Other"))
                    }
                }
            }
            else -> collectionCars
        }

        selectedCategoryDetails = CategoryDetails(
            categoryTitle = title,
            categoryName = name,
            cars = filteredCars
        )
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 1100.dp)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .testTag("stats_view")
        ) {
            Text(
            text = if (isTr) "Koleksiyon İstatistikleri" else "Collection Statistics",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = if (isTr) "Fiziksel envanterinizin değer ve marka analizi" else "Value and brand analysis of your physical inventory",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        val needsReviewCars = collectionCars.filter { com.example.util.ModelValidator.validate(it).needsReview }
        if (needsReviewCars.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedCategoryDetails = CategoryDetails(
                            categoryTitle = if (isTr) "İnceleme Gereken Modeller" else "Models Needing Review",
                            categoryName = if (isTr) "Standartlara Uymayan veya Şüpheli Kayıtlar" else "Non-standard or Flagged Entries",
                            cars = needsReviewCars
                        )
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                border = BorderStroke(1.dp, Color(0xFFD97706))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isTr) "${needsReviewCars.size} Model İnceleme Gerektiriyor" else "${needsReviewCars.size} Models Need Review",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                            Text(
                                text = if (isTr) "Marka/seri uyuşmazlığı veya hatalı girdi var. Listelemek için dokunun." else "Brand/series mismatch or invalid entry found. Tap to view.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF78350F)
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color(0xFFD97706)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Portfolio Summary Grid (Responsive 4-col for tablet/landscape, 2x2 for mobile)
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val isWide = maxWidth >= 600.dp
            if (isWide) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatSummaryCard(
                        title = if (isTr) "Toplam Parça" else "Total Items",
                        value = if (isTr) "$totalCount Adet" else "$totalCount Items",
                        subtitle = if (isTr) "+$totalWishlistCount İstek Listesi" else "+$totalWishlistCount Wishlist",
                        icon = Icons.Default.Inventory,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )

                    StatSummaryCard(
                        title = if (isTr) "Tahmini Değer" else "Estimated Value",
                        value = "$currencyCode ${formatAmount(totalEstimatedValue)}",
                        subtitle = if (valueGain >= 0) {
                            if (isTr) "+$currencyCode ${formatAmount(valueGain)} Artış" else "+$currencyCode ${formatAmount(valueGain)} Increase"
                        } else "$currencyCode ${formatAmount(valueGain)}",
                        icon = Icons.Default.ShowChart,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )

                    StatSummaryCard(
                        title = if (isTr) "Toplam Yatırım" else "Total Investment",
                        value = "$currencyCode ${formatAmount(totalSpent)}",
                        subtitle = if (isTr) "Alış Maliyeti" else "Purchase Cost",
                        icon = Icons.Default.AttachMoney,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    StatSummaryCard(
                        title = if (isTr) "İstek Listesi" else "Wishlist",
                        value = if (isTr) "$totalWishlistCount Model" else "$totalWishlistCount Models",
                        subtitle = if (isTr) "Hedefteki Diecastlar" else "Target Diecasts",
                        icon = Icons.Default.Favorite,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatSummaryCard(
                            title = if (isTr) "Toplam Parça" else "Total Items",
                            value = if (isTr) "$totalCount Adet" else "$totalCount Items",
                            subtitle = if (isTr) "+$totalWishlistCount İstek Listesi" else "+$totalWishlistCount Wishlist",
                            icon = Icons.Default.Inventory,
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.weight(1f)
                        )

                        StatSummaryCard(
                            title = if (isTr) "Tahmini Değer" else "Estimated Value",
                            value = "$currencyCode ${formatAmount(totalEstimatedValue)}",
                            subtitle = if (valueGain >= 0) {
                                if (isTr) "+$currencyCode ${formatAmount(valueGain)} Artış" else "+$currencyCode ${formatAmount(valueGain)} Increase"
                            } else "$currencyCode ${formatAmount(valueGain)}",
                            icon = Icons.Default.ShowChart,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatSummaryCard(
                            title = if (isTr) "Toplam Yatırım" else "Total Investment",
                            value = "$currencyCode ${formatAmount(totalSpent)}",
                            subtitle = if (isTr) "Alış Maliyeti" else "Purchase Cost",
                            icon = Icons.Default.AttachMoney,
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )

                        StatSummaryCard(
                            title = if (isTr) "İstek Listesi" else "Wishlist",
                            value = if (isTr) "$totalWishlistCount Model" else "$totalWishlistCount Models",
                            subtitle = if (isTr) "Hedefteki Diecastlar" else "Target Diecasts",
                            icon = Icons.Default.Favorite,
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Üretici Fiyatlandırma & Koleksiyon Değeri Güncelleme Kartı
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_manufacturer_price_sync"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
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
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PriceChange,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isTr) "Üretici Fiyatlandırma & Değerleme" else "Manufacturer Price Sync",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (lastPriceSyncDate != null) {
                                    if (isTr) "Son Güncelleme: $lastPriceSyncDate" else "Last Sync: $lastPriceSyncDate"
                                } else {
                                    if (isTr) "Hot Wheels, Mini GT, Inno64 rayiçleri hazır" else "Hot Wheels, Mini GT, Inno64 rates ready"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isTr) {
                        "Koleksiyonunuzdaki araçların model, seri, ölçek ve kutu durumuna göre üretici katalog fiyatlarını çekip toplam koleksiyon değerinizi tek dokunuşla güncelleyin."
                    } else {
                        "Fetch benchmark manufacturer pricing for your models, series, scale, and conditions to update your total collection valuation in 1 tap."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { onOpenPriceSync?.invoke() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("btn_open_price_sync"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isTr) "Üretici Fiyatlarını Yenile & Değerle" else "Refresh Manufacturer Prices",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Koleksiyon Değeri Artışı (Collection Value Increase / Growth Card)
        val percentageGain = if (totalSpent > 0) (valueGain / totalSpent) * 100.0 else 0.0
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_collection_value_gain"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (valueGain >= 0) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
            ),
            border = BorderStroke(
                1.dp,
                if (valueGain >= 0) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                else MaterialTheme.colorScheme.error.copy(alpha = 0.4f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
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
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (valueGain >= 0) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (valueGain >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = if (valueGain >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isTr) "Koleksiyon Değeri Artışı" else "Collection Value Growth",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isTr) "Alış maliyeti ile güncel piyasa değeri kazanç farkı"
                                else "Valuation profit based on purchase cost vs market value",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (valueGain >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    ) {
                        Text(
                            text = if (valueGain >= 0) "+${String.format(java.util.Locale.US, "%.1f", percentageGain)}% Değer Artışı"
                            else "${String.format(java.util.Locale.US, "%.1f", percentageGain)}% Fark",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = if (isTr) "Toplam Değer Artışı / Kazanç" else "Total Value Appreciation",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (valueGain >= 0) "+$currencyCode ${formatAmount(valueGain)}"
                            else "$currencyCode ${formatAmount(valueGain)}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (valueGain >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (isTr) "Alış: $currencyCode ${formatAmount(totalSpent)}" else "Cost: $currencyCode ${formatAmount(totalSpent)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (isTr) "Tahmini Değer: $currencyCode ${formatAmount(totalEstimatedValue)}"
                            else "Est. Value: $currencyCode ${formatAmount(totalEstimatedValue)}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4 Dağılım Grafiği (Responsive Grid: Tablet & Geniş ekranlarda 2x2 ızgara, dar telefon ekranlarında alt alta)
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val isWideCharts = maxWidth >= 680.dp
            if (isWideCharts) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        TirePieChartCard(
                            title = if (isTr) "Üretici Marka Dağılımı" else "Manufacturer Distribution",
                            subtitle = if (isTr) "Hot Wheels, Mini GT, Majorette vb. döküm üreticileri" else "Diecast manufacturers (Hot Wheels, Mini GT, etc.)",
                            items = manufacturerCounts,
                            totalCount = totalCount,
                            isTr = isTr,
                            onItemClick = { name ->
                                selectCategory(
                                    if (isTr) "Üretici Marka Dağılımı" else "Manufacturer Distribution",
                                    name,
                                    manufacturerCounts
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )

                        TirePieChartCard(
                            title = if (isTr) "Araba Markası Dağılımı" else "Car Brand Breakdown",
                            subtitle = if (isTr) "Porsche, Nissan, Ford, BMW gibi otomotiv markaları" else "Automotive brands like Porsche, Nissan, Ford, BMW",
                            items = carBrandCounts,
                            totalCount = totalCount,
                            isTr = isTr,
                            onItemClick = { name ->
                                selectCategory(
                                    if (isTr) "Araba Markası Dağılımı" else "Car Brand Breakdown",
                                    name,
                                    carBrandCounts
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        TirePieChartCard(
                            title = if (isTr) "Ölçek Dağılımı" else "Scale Breakdown",
                            subtitle = if (isTr) "1:64, 1:43, 1:24 ve 1:18 model boyut oranları" else "Model scale ratios (1:64, 1:43, 1:24, 1:18)",
                            items = scaleCounts,
                            totalCount = totalCount,
                            isTr = isTr,
                            onItemClick = { name ->
                                selectCategory(
                                    if (isTr) "Ölçek Dağılımı" else "Scale Breakdown",
                                    name,
                                    scaleCounts
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )

                        TirePieChartCard(
                            title = if (isTr) "Paket & Hasar Durumu" else "Condition & Packaging",
                            subtitle = if (isTr) "Kutulu, sergilenen açık veya hasarlı modeller" else "Boxed, displayed loose, or damaged models",
                            items = conditionCounts,
                            totalCount = totalCount,
                            isTr = isTr,
                            onItemClick = { name ->
                                selectCategory(
                                    if (isTr) "Paket & Hasar Durumu" else "Condition & Packaging",
                                    name,
                                    conditionCounts
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TirePieChartCard(
                        title = if (isTr) "Üretici Marka Dağılımı" else "Manufacturer Distribution",
                        subtitle = if (isTr) "Hot Wheels, Mini GT, Majorette vb. döküm üreticileri" else "Diecast manufacturers (Hot Wheels, Mini GT, etc.)",
                        items = manufacturerCounts,
                        totalCount = totalCount,
                        isTr = isTr,
                        onItemClick = { name ->
                            selectCategory(
                                if (isTr) "Üretici Marka Dağılımı" else "Manufacturer Distribution",
                                name,
                                manufacturerCounts
                            )
                        }
                    )

                    TirePieChartCard(
                        title = if (isTr) "Araba Markası Dağılımı" else "Car Brand Breakdown",
                        subtitle = if (isTr) "Porsche, Nissan, Ford, BMW gibi otomotiv markaları" else "Automotive brands like Porsche, Nissan, Ford, BMW",
                        items = carBrandCounts,
                        totalCount = totalCount,
                        isTr = isTr,
                        onItemClick = { name ->
                            selectCategory(
                                if (isTr) "Araba Markası Dağılımı" else "Car Brand Breakdown",
                                name,
                                carBrandCounts
                            )
                        }
                    )

                    TirePieChartCard(
                        title = if (isTr) "Ölçek Dağılımı" else "Scale Breakdown",
                        subtitle = if (isTr) "1:64, 1:43, 1:24 ve 1:18 model boyut oranları" else "Model scale ratios (1:64, 1:43, 1:24, 1:18)",
                        items = scaleCounts,
                        totalCount = totalCount,
                        isTr = isTr,
                        onItemClick = { name ->
                            selectCategory(
                                if (isTr) "Ölçek Dağılımı" else "Scale Breakdown",
                                name,
                                scaleCounts
                            )
                        }
                    )

                    TirePieChartCard(
                        title = if (isTr) "Paket & Hasar Durumu" else "Condition & Packaging",
                        subtitle = if (isTr) "Kutulu, sergilenen açık veya hasarlı modeller" else "Boxed, displayed loose, or damaged models",
                        items = conditionCounts,
                        totalCount = totalCount,
                        isTr = isTr,
                        onItemClick = { name ->
                            selectCategory(
                                if (isTr) "Paket & Hasar Durumu" else "Condition & Packaging",
                                name,
                                conditionCounts
                            )
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Top Most Valuable Models
        if (topValueCars.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isTr) "En Değerli Parçalarınız" else "Most Valuable Models",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    topValueCars.forEachIndexed { index, car ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = CircleShape,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${index + 1}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "${car.carBrand} ${car.model}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${car.manufacturer} • ${car.scale}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Text(
                                text = "$currencyCode ${formatAmount(car.estimatedValue)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        }
    }

    selectedCategoryDetails?.let { details ->
        CategoryModelsBottomSheet(
            categoryTitle = details.categoryTitle,
            categoryName = details.categoryName,
            cars = details.cars,
            currencyCode = currencyCode,
            isTr = isTr,
            onCarClick = onCarClick,
            onDismiss = { selectedCategoryDetails = null }
        )
    }
}

@Composable
fun StatSummaryCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun BreakdownSection(
    title: String,
    items: List<Pair<String, Int>>,
    total: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (items.isEmpty() || total == 0) {
                Text(
                    text = "Henüz veri yok",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                items.take(5).forEach { (name, count) ->
                    val progress = count.toFloat() / total.coerceAtLeast(1)

                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "$count (%${"%.0f".format(progress * 100)})",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TirePieChartCard(
    title: String,
    subtitle: String,
    items: List<Pair<String, Int>>,
    totalCount: Int,
    isTr: Boolean,
    onItemClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape,
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.PieChart,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (items.isNotEmpty() && totalCount > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isTr) "Grafiğe veya markaya dokunarak modelleri görün" else "Tap on chart or item to view models",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp
                    )
                }
            }

            if (items.isEmpty() || totalCount == 0) {
                Text(
                    text = if (isTr) "Henüz veri yok" else "No data available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                val topLimit = 5
                val topItems = items.take(topLimit)
                val remainingCount = items.drop(topLimit).sumOf { it.second }

                val chartItems = if (remainingCount > 0) {
                    topItems + Pair(if (isTr) "Diğer" else "Other", remainingCount)
                } else {
                    topItems
                }

                val sliceColors = listOf(
                    Color(0xFF2563EB), // Blue
                    Color(0xFFD97706), // Amber
                    Color(0xFF059669), // Emerald Green
                    Color(0xFFDB2777), // Pink
                    Color(0xFF7C3AED), // Purple
                    Color(0xFF0891B2), // Cyan
                    Color(0xFFE11D48)  // Red
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Donut Tire Pie Chart Canvas
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(140.dp)
                            .testTag("pie_chart_box_${title.take(10)}")
                    ) {
                        Canvas(
                            modifier = Modifier
                                .size(136.dp)
                                .testTag("pie_chart_canvas_${title.take(10)}")
                                .pointerInput(chartItems, totalCount) {
                                    detectTapGestures { tapOffset ->
                                        val center = Offset(size.width / 2f, size.height / 2f)
                                        val dx = tapOffset.x - center.x
                                        val dy = tapOffset.y - center.y
                                        var angleDeg = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                        if (angleDeg < 0) angleDeg += 360f
                                        val relAngle = (angleDeg + 90f) % 360f

                                        var currAngle = 0f
                                        for ((_, item) in chartItems.withIndex()) {
                                            val sweep = (item.second.toFloat() / totalCount) * 360f
                                            if (relAngle >= currAngle && relAngle < currAngle + sweep) {
                                                onItemClick(item.first)
                                                break
                                            }
                                            currAngle += sweep
                                        }
                                    }
                                }
                        ) {
                            val centerOffset = Offset(size.width / 2f, size.height / 2f)
                            val outerTireRadius = (size.minDimension / 2f) - 2.dp.toPx()
                            val tireWidth = 22.dp.toPx()

                            // 1. Dark Tire Outer Tread Ring (Rubber Base)
                            drawCircle(
                                color = Color(0xFF1F2937),
                                radius = outerTireRadius,
                                center = centerOffset
                            )

                            // 2. Tire Tread Notches around outer circumference
                            val numTreads = 24
                            val treadInnerR = outerTireRadius - 4.dp.toPx()
                            val treadOuterR = outerTireRadius
                            for (i in 0 until numTreads) {
                                val angleRad = Math.toRadians((i * (360.0 / numTreads)))
                                val p1 = Offset(
                                    centerOffset.x + (treadInnerR * cos(angleRad)).toFloat(),
                                    centerOffset.y + (treadInnerR * sin(angleRad)).toFloat()
                                )
                                val p2 = Offset(
                                    centerOffset.x + (treadOuterR * cos(angleRad)).toFloat(),
                                    centerOffset.y + (treadOuterR * sin(angleRad)).toFloat()
                                )
                                drawLine(
                                    color = Color(0xFF111827),
                                    start = p1,
                                    end = p2,
                                    strokeWidth = 2.5.dp.toPx()
                                )
                            }

                            // 3. Colored Pie Chart Slices (Tire Sidewall Ring)
                            val sliceRadius = outerTireRadius - (tireWidth / 2f) - 2.dp.toPx()
                            val arcTopLeft = Offset(centerOffset.x - sliceRadius, centerOffset.y - sliceRadius)
                            val arcSize = Size(sliceRadius * 2f, sliceRadius * 2f)

                            var startAngle = -90f
                            chartItems.forEachIndexed { index, item ->
                                val sweepAngle = (item.second.toFloat() / totalCount) * 360f
                                val color = sliceColors[index % sliceColors.size]

                                drawArc(
                                    color = color,
                                    startAngle = startAngle + 1f,
                                    sweepAngle = (sweepAngle - 2f).coerceAtLeast(0.5f),
                                    useCenter = false,
                                    topLeft = arcTopLeft,
                                    size = arcSize,
                                    style = Stroke(width = tireWidth, cap = StrokeCap.Butt)
                                )
                                startAngle += sweepAngle
                            }

                            // 4. Inner Rim Well & Metallic Silver Lip (Jant Kasnağı)
                            val innerRimRadius = outerTireRadius - tireWidth - 2.dp.toPx()
                            drawCircle(
                                color = Color(0xFF111827),
                                radius = innerRimRadius,
                                center = centerOffset
                            )
                            drawCircle(
                                color = Color(0xFF9CA3AF),
                                radius = innerRimRadius,
                                center = centerOffset,
                                style = Stroke(width = 2.dp.toPx())
                            )

                            // 5. Alloy Wheel Rim Spokes (5 Jant Kolu)
                            val spokeCount = 5
                            val spokeStartR = 18.dp.toPx()
                            val spokeEndR = innerRimRadius - 2.dp.toPx()
                            for (i in 0 until spokeCount) {
                                val angleRad = Math.toRadians(-90.0 + i * (360.0 / spokeCount))
                                val sp1 = Offset(
                                    centerOffset.x + (spokeStartR * cos(angleRad)).toFloat(),
                                    centerOffset.y + (spokeStartR * sin(angleRad)).toFloat()
                                )
                                val sp2 = Offset(
                                    centerOffset.x + (spokeEndR * cos(angleRad)).toFloat(),
                                    centerOffset.y + (spokeEndR * sin(angleRad)).toFloat()
                                )
                                drawLine(
                                    color = Color(0xFFE5E7EB),
                                    start = sp1,
                                    end = sp2,
                                    strokeWidth = 4.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                                drawLine(
                                    color = Color(0xFF9CA3AF),
                                    start = sp1,
                                    end = sp2,
                                    strokeWidth = 2.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            }

                            // 6. Central Hub Cap (Jant Göbeği)
                            drawCircle(
                                color = Color(0xFF1F2937),
                                radius = 20.dp.toPx(),
                                center = centerOffset
                            )
                            drawCircle(
                                color = Color(0xFFD1D5DB),
                                radius = 20.dp.toPx(),
                                center = centerOffset,
                                style = Stroke(width = 1.5.dp.toPx())
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Legend Column
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        chartItems.forEachIndexed { index, (name, count) ->
                            val color = sliceColors[index % sliceColors.size]
                            val percentage = (count.toFloat() / totalCount) * 100f

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { onItemClick(name) }
                                    .padding(vertical = 2.dp, horizontal = 4.dp)
                                    .testTag("pie_legend_$name")
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1
                                )
                                Text(
                                    text = "%${"%.0f".format(percentage)} ($count)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Detailed Progress Bars List below tire pie chart
                items.take(5).forEachIndexed { index, (name, count) ->
                    val progress = count.toFloat() / totalCount.coerceAtLeast(1)
                    val color = sliceColors[index % sliceColors.size]

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onItemClick(name) }
                            .padding(vertical = 4.dp, horizontal = 4.dp)
                            .testTag("pie_progress_$name")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = "$count (%${"%.0f".format(progress * 100)})",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = color
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = color,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryModelsBottomSheet(
    categoryTitle: String,
    categoryName: String,
    cars: List<DiecastCar>,
    currencyCode: String,
    isTr: Boolean,
    onCarClick: ((DiecastCar) -> Unit)?,
    onDismiss: () -> Unit
) {
    val totalEstimated = cars.sumOf { it.estimatedValue }
    val totalSpent = cars.sumOf { it.purchasePrice }
    val configuration = LocalConfiguration.current
    val isTablet = configuration.smallestScreenWidthDp >= 600

    if (isTablet) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .widthIn(min = 460.dp, max = 640.dp)
                    .fillMaxWidth(0.85f)
                    .padding(16.dp)
                    .testTag("category_models_dialog")
            ) {
                CategoryModelsContent(
                    categoryTitle = categoryTitle,
                    categoryName = categoryName,
                    cars = cars,
                    currencyCode = currencyCode,
                    isTr = isTr,
                    totalEstimated = totalEstimated,
                    totalSpent = totalSpent,
                    onCarClick = onCarClick,
                    onDismiss = onDismiss
                )
            }
        }
    } else {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.testTag("category_models_bottom_sheet")
        ) {
            CategoryModelsContent(
                categoryTitle = categoryTitle,
                categoryName = categoryName,
                cars = cars,
                currencyCode = currencyCode,
                isTr = isTr,
                totalEstimated = totalEstimated,
                totalSpent = totalSpent,
                onCarClick = onCarClick,
                onDismiss = onDismiss
            )
        }
    }
}

@Composable
private fun CategoryModelsContent(
    categoryTitle: String,
    categoryName: String,
    cars: List<DiecastCar>,
    currencyCode: String,
    isTr: Boolean,
    totalEstimated: Double,
    totalSpent: Double,
    onCarClick: ((DiecastCar) -> Unit)?,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = categoryTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.Default.Close, contentDescription = if (isTr) "Kapat" else "Close")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Summary Info Card inside dialog / bottom sheet
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isTr) "Model Sayısı" else "Total Models",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${cars.size}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isTr) "Tahmini Değer" else "Est. Value",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$currencyCode ${formatAmount(totalEstimated)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (totalSpent > 0) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isTr) "Yatırım" else "Investment",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$currencyCode ${formatAmount(totalSpent)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isTr) "Koleksiyondaki Modeller" else "Models in Collection",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (cars.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isTr) "Bu kategoride model bulunamadı" else "No models found in this category",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cars, key = { it.id }) { car ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onDismiss()
                                onCarClick?.invoke(car)
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Thumbnail or Icon
                            Surface(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                if (!car.photoPath.isNullOrBlank()) {
                                    val context = androidx.compose.ui.platform.LocalContext.current
                                    val imageModel = remember(car.photoPath) {
                                        com.example.util.ImageStorageHelper.resolveImageModel(context, car.photoPath)
                                    }
                                    AsyncImage(
                                        model = imageModel,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                } else {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.DirectionsCar,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${car.carBrand} ${car.model}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${car.manufacturer}${if (car.series.isNotBlank()) " • ${car.series}" else ""}${if (car.scale.isNotBlank()) " • ${car.scale}" else ""}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (car.estimatedValue > 0) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "$currencyCode ${formatAmount(car.estimatedValue)}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
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
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

