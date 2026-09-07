package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DiecastCar
import com.example.util.formatAmount
import com.example.util.isTurkishLocale

@Composable
fun CollectionTipCard(
    allCars: List<DiecastCar>,
    currencyCode: String = "TRL",
    isWishlistTab: Boolean = false,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTr = isTurkishLocale()

    val collectionCars = allCars.filter { !it.isWishlist }
    val totalSpent = collectionCars.sumOf { it.purchasePrice }
    val totalEstimated = collectionCars.sumOf { it.estimatedValue }
    val mostValuable = collectionCars.maxByOrNull { it.estimatedValue }
    val rareCarsCount = collectionCars.count { 
        it.series.contains("Chase", ignoreCase = true) || 
        it.series.contains("Boulevard", ignoreCase = true) || 
        it.estimatedValue >= 500.0 || 
        it.isFavorite 
    }

    val (icon, title, message) = when {
        isWishlistTab -> {
            Triple(
                Icons.Default.Stars,
                if (isTr) "İstek Listesi İpucu" else "Wishlist Tip",
                if (isTr) "Göz koyduğunuz ${allCars.count { it.isWishlist }} modeli seri veya markaya göre filtreleyerek takip edebilirsiniz."
                else "Filter your ${allCars.count { it.isWishlist }} wanted models by series or brand to track them easily."
            )
        }
        totalSpent > 0 && totalEstimated > totalSpent -> {
            val gainPercent = (((totalEstimated - totalSpent) / totalSpent) * 100).toInt()
            val bestCarText = mostValuable?.let { "${it.carBrand} ${it.model} ($currencyCode ${formatAmount(it.estimatedValue)})" } ?: ""
            Triple(
                Icons.Default.TrendingUp,
                if (isTr) "Koleksiyon Değeri Artışta (%$gainPercent)" else "Collection Value Surge (+$gainPercent%)",
                if (isTr) "Garajınız kârda! En değerli modeliniz: $bestCarText. Orijinal kutularında saklamak değer artışını destekler."
                else "Your garage is in profit! Top valued car: $bestCarText. Preserving mint packaging maintains value."
            )
        }
        rareCarsCount > 0 -> {
            Triple(
                Icons.Default.Stars,
                if (isTr) "Nadir & Özel Seriler ($rareCarsCount Parça)" else "Rare & Special Models ($rareCarsCount Pieces)",
                if (isTr) "Koleksiyonunuzda $rareCarsCount adet nadir veya özel seri model var. Doğrudan güneş ışığından uzak tutun."
                else "You have $rareCarsCount rare or special series models. Store away from direct sunlight."
            )
        }
        else -> {
            Triple(
                Icons.Default.Lightbulb,
                if (isTr) "Koleksiyoner İpucu" else "Collector Tip",
                if (isTr) "1:64 ölçekli Chase ve Premium seriler diecast pazarında zamanla en yüksek değer artışını gösteren modellerdir."
                else "1:64 scale Chase and Premium series tend to show the highest value appreciation over time."
            )
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("collection_tip_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(28.dp)
                    .testTag("btn_dismiss_tip")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = if (isTr) "Kapat" else "Dismiss",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
