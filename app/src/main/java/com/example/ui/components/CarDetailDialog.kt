package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.CheckCircle
import com.example.util.ManufacturerPriceService
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.DiecastCar
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.example.util.formatAmount
import com.example.util.isTurkishLocale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CarDetailContent(
    car: DiecastCar,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleWishlist: () -> Unit,
    onToggleFavorite: () -> Unit = {},
    onUpdateCar: ((DiecastCar) -> Unit)? = null,
    onDismiss: () -> Unit,
    currencyCode: String = "TRL",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTr = isTurkishLocale()
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showFullScreenPreview by remember { mutableStateOf(false) }
    var showQuickNoteEditor by remember { mutableStateOf(false) }
    var quickNoteInput by remember { mutableStateOf(car.notes) }
    val photoList = car.getPhotoList()
    var selectedPhotoIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(car.notes) {
        quickNoteInput = car.notes
    }

    if (showQuickNoteEditor) {
        AlertDialog(
            onDismissRequest = { showQuickNoteEditor = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(
                    text = if (isTr) "Kondisyon & Geçmiş Notu" else "Condition & History Note",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (isTr) {
                            "Araç hakkındaki kutu/kartonet hasarları, kondisyon veya hikayesini güncelleyin."
                        } else {
                            "Update condition, box/card wear, or provenance history for this car."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val quickTags = if (isTr) {
                        listOf(
                            "Kutusunda kusursuz (Mint)",
                            "Kartonette hafif bükülme",
                            "Blisterde çatlak/sararma",
                            "İlk sahibinden alındı",
                            "Koruma kabında (Protector)",
                            "Yurtdışı hatırası",
                            "Custom yapıldı"
                        )
                    } else {
                        listOf(
                            "Mint in Blister",
                            "Card corner wear",
                            "Cracked blister",
                            "Original owner",
                            "In protector case",
                            "Travel souvenir",
                            "Customized"
                        )
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        quickTags.forEach { tag ->
                            Surface(
                                onClick = {
                                    quickNoteInput = if (quickNoteInput.isBlank()) {
                                        tag
                                    } else if (!quickNoteInput.contains(tag, ignoreCase = true)) {
                                        "$quickNoteInput • $tag"
                                    } else {
                                        quickNoteInput
                                    }
                                },
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.padding(bottom = 2.dp)
                            ) {
                                Text(
                                    text = "+ $tag",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = quickNoteInput,
                        onValueChange = { quickNoteInput = it },
                        placeholder = {
                            Text(if (isTr) "Kondisyon ve geçmiş notlarınızı girin..." else "Enter condition and history notes...")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_quick_note"),
                        minLines = 3,
                        maxLines = 5
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateCar?.invoke(car.copy(notes = quickNoteInput.trim()))
                        showQuickNoteEditor = false
                    },
                    modifier = Modifier.testTag("btn_save_quick_note")
                ) {
                    Text(if (isTr) "Kaydet" else "Save")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showQuickNoteEditor = false }) {
                    Text(if (isTr) "İptal" else "Cancel")
                }
            }
        )
    }

    if (showFullScreenPreview) {
        CoilImagePreviewDialog(
            photos = if (photoList.isNotEmpty()) photoList else listOfNotNull(car.photoPath?.takeIf { it.isNotBlank() }),
            initialIndex = selectedPhotoIndex,
            title = "${car.manufacturer} ${car.carBrand} ${car.model}".trim(),
            subtitle = car.series.ifBlank { car.scale },
            onDismiss = { showFullScreenPreview = false }
        )
    }

    Column(
        modifier = modifier
    ) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (car.manufacturer.isNotBlank()) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = car.manufacturer,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Row {
                        IconButton(onClick = { shareDiecastCar(context, car, currencyCode, isTr) }, modifier = Modifier.testTag("btn_share_car")) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = if (isTr) "Paylaş" else "Share",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = onToggleFavorite, modifier = Modifier.testTag("btn_toggle_favorite")) {
                            Icon(
                                imageVector = if (car.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isTr) "Favorilere Ekle" else "Toggle Favorite",
                                tint = if (car.isFavorite) Color(0xFFE11D48) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(onClick = onEdit, modifier = Modifier.testTag("btn_edit_car")) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = if (isTr) "Düzenle" else "Edit")
                        }
                        IconButton(onClick = { showDeleteConfirmDialog = true }, modifier = Modifier.testTag("btn_delete_car")) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = if (isTr) "Sil" else "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = if (isTr) "Kapat" else "Close")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Main Photo Card
                val activePhoto = photoList.getOrNull(selectedPhotoIndex) ?: car.photoPath
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.33f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { showFullScreenPreview = true }
                        .testTag("btn_open_full_image_preview")
                ) {
                    AnimatedContent(
                        targetState = activePhoto,
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.96f, animationSpec = tween(300)))
                                .togetherWith(fadeOut(animationSpec = tween(200)))
                        },
                        label = "car_photo_anim"
                    ) { photoPath ->
                        CarPhotoImage(
                            photoPath = photoPath,
                            contentDescription = "${car.carBrand} ${car.model}",
                            modifier = Modifier.matchParentSize()
                        )
                    }

                // Scale Badge
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(bottomEnd = 12.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = car.scale.ifBlank { "1:64" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                // Photo Counter Badge if multiple
                if (photoList.size > 1) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(bottomStart = 12.dp),
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = "${selectedPhotoIndex + 1} / ${photoList.size}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                // Zoom / Fullscreen Preview Badge
                Surface(
                    color = Color.Black.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .clickable { showFullScreenPreview = true }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ZoomIn,
                            contentDescription = "Zoom",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isTr) "Coil Önizleme" else "Coil Preview",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Condition Badge
                if (car.condition.isNotBlank()) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = car.condition,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Photo Gallery Thumbnails Row (if multiple photos exist)
            if (photoList.size > 1) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    photoList.forEachIndexed { idx, pPath ->
                        val isSelected = idx == selectedPhotoIndex
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedPhotoIndex = idx }
                        ) {
                            CarPhotoImage(
                                photoPath = pPath,
                                contentDescription = "Photo ${idx + 1}",
                                modifier = Modifier.matchParentSize()
                            )

                            Surface(
                                color = Color.Black.copy(alpha = 0.65f),
                                shape = RoundedCornerShape(topStart = 4.dp),
                                modifier = Modifier.align(Alignment.BottomEnd)
                            ) {
                                Text(
                                    text = "${idx + 1}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title & Subtitle
            Text(
                text = "${car.carBrand} ${car.model}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (car.series.isNotBlank()) {
                Text(
                    text = if (isTr) "Seri: ${car.series}" else "Series: ${car.series}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Needs Review Warning Box
            val validation = remember(car) { com.example.util.ModelValidator.validate(car) }
            if (validation.needsReview) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFEF3C7)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFD97706))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFD97706),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isTr) "İnceleme Gerekiyor" else "Needs Review",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E)
                                )
                            }
                            TextButton(onClick = onEdit) {
                                Text(
                                    text = if (isTr) "Düzelt" else "Fix",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD97706)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        validation.issues.forEach { issue ->
                            Text(
                                text = "• $issue",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF78350F),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Price / Valuation Banner Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isTr) "Alış Fiyatı" else "Purchase Price",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (car.purchasePrice > 0) "$currencyCode ${formatAmount(car.purchasePrice)}" else "-",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isTr) "Tahmini Değer" else "Estimated Value",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (car.estimatedValue > 0) "$currencyCode ${formatAmount(car.estimatedValue)}" else "-",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Manufacturer Price Benchmark Card
            val manufacturerEstimate = remember(car, currencyCode) {
                ManufacturerPriceService.estimateCarPrice(car, currencyCode)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_mfg_price_estimate"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PriceChange,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isTr) "Üretici Güncel Piyasa Rayici" else "Manufacturer Market Benchmark",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = "$currencyCode ${formatAmount(manufacturerEstimate.estimatedValue)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "${manufacturerEstimate.marketNotes} (${manufacturerEstimate.confidence})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (car.estimatedValue != manufacturerEstimate.estimatedValue) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                onUpdateCar?.invoke(car.copy(estimatedValue = manufacturerEstimate.estimatedValue))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .testTag("btn_apply_mfg_price"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isTr) "Tahmini Değeri Üretici Fiyatına Eşitle" else "Set Estimated Value to Manufacturer Price",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Detailed Specifications Grid
            Text(
                text = if (isTr) "Detaylı Özellikler" else "Detailed Specifications",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DetailRow(label = if (isTr) "Üretici Marka" else "Manufacturer", value = car.manufacturer.ifBlank { "-" })
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                DetailRow(label = if (isTr) "Araba Markası" else "Car Brand", value = car.carBrand)
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                DetailRow(label = if (isTr) "Model Yılı" else "Model Year", value = car.modelYear.ifBlank { "-" })
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                DetailRow(label = if (isTr) "Ölçek" else "Scale", value = car.scale.ifBlank { "1:64" })
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                DetailRow(label = if (isTr) "Seri" else "Series", value = car.series.ifBlank { "-" })
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                DetailRow(label = if (isTr) "Üretim Yılı" else "Release Year", value = car.productionYear.ifBlank { "-" })
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                DetailRow(label = if (isTr) "Renk" else "Color", value = car.color.ifBlank { "-" })
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                DetailRow(label = if (isTr) "Paket Durumu" else "Condition", value = car.condition.ifBlank { "-" })
                if (car.barcode.isNotBlank()) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    DetailRow(label = if (isTr) "Barkod / Kod" else "Barcode / Code", value = car.barcode)
                }
            }

            // Notes Card (Koleksiyoner Notları & Kondisyon / Geçmiş)
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_car_detail_notes"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isTr) "Notlar (Kondisyon & Geçmiş)" else "Notes (Condition & History)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(
                            onClick = {
                                quickNoteInput = car.notes
                                showQuickNoteEditor = true
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("btn_edit_quick_notes")
                        ) {
                            Icon(
                                imageVector = if (car.notes.isNotBlank()) Icons.Default.Edit else Icons.Default.Add,
                                contentDescription = if (isTr) "Notu Düzenle" else "Edit Note",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    if (car.notes.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = car.notes,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    } else {
                        Surface(
                            onClick = {
                                quickNoteInput = ""
                                showQuickNoteEditor = true
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isTr) "Kondisyon veya hikaye notu eklemek için dokunun..." else "Tap to add condition or history notes...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Sınıflandırma ve Özel Etiketler (Tags / Classification)
            val carTags = car.getTagList()
            if (carTags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_car_detail_tags"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocalOffer,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isTr) "Sınıflandırma & Etiketler" else "Classification & Tags",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            IconButton(
                                onClick = onEdit,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("btn_edit_tags_shortcut")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = if (isTr) "Etiketleri Düzenle" else "Edit Tags",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            carTags.forEach { tag ->
                                val tagColors = com.example.util.TagColorHelper.getColorSchemeForTag(tag)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = tagColors.containerColor,
                                    border = BorderStroke(1.dp, tagColors.borderColor)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocalOffer,
                                            contentDescription = null,
                                            tint = tagColors.contentColor,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = tag,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = tagColors.contentColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons: Share, Edit & Wishlist Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { shareDiecastCar(context, car, currencyCode, isTr) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_share_car_detail_bottom"),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isTr) "Paylaş" else "Share",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_edit_car_detail_bottom"),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isTr) "Düzenle" else "Edit",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onToggleWishlist,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_toggle_wishlist"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (car.isWishlist) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = if (car.isWishlist) Icons.Default.SwapHoriz else Icons.Default.Favorite,
                    contentDescription = null,
                    tint = if (car.isWishlist) Color.White else Color.Black,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (car.isWishlist) {
                        if (isTr) "Koleksiyona Taşı" else "Move to Collection"
                    } else {
                        if (isTr) "İstek Listesine Taşı" else "Move to Wishlist"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (car.isWishlist) Color.White else Color.Black,
                    maxLines = 1
                )
            }
        }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = if (isTr) "Modeli Silmek İstiyor Musunuz?" else "Delete Model?",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${car.manufacturer} ${car.carBrand} ${car.model}".trim(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    val subInfo = listOfNotNull(
                        car.scale.takeIf { it.isNotBlank() }?.let { if (isTr) "Ölçek: $it" else "Scale: $it" },
                        car.series.takeIf { it.isNotBlank() }?.let { if (isTr) "Seri: $it" else "Series: $it" }
                    ).joinToString(" • ")
                    
                    if (subInfo.isNotBlank()) {
                        Text(
                            text = subInfo,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isTr) {
                            "Bu araç ve ekli tüm fotoğrafları koleksiyonunuzdan kalıcı olarak silinecektir. Bu işlem geri alınamaz."
                        } else {
                            "This vehicle and all attached photos will be permanently deleted from your collection. This action cannot be undone."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("btn_confirm_delete")
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isTr) "Evet, Sil" else "Yes, Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteConfirmDialog = false },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("btn_cancel_delete")
                ) {
                    Text(if (isTr) "Vazgeç" else "Cancel")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDetailBottomSheet(
    car: DiecastCar,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleWishlist: () -> Unit,
    onToggleFavorite: () -> Unit = {},
    onUpdateCar: ((DiecastCar) -> Unit)? = null,
    onDismiss: () -> Unit,
    currencyCode: String = "TRL",
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        CarDetailContent(
            car = car,
            onEdit = onEdit,
            onDelete = onDelete,
            onToggleWishlist = onToggleWishlist,
            onToggleFavorite = onToggleFavorite,
            onUpdateCar = onUpdateCar,
            onDismiss = onDismiss,
            currencyCode = currencyCode,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 720.dp)
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        )
    }
}

@Composable
fun TabletCarDetailPane(
    car: DiecastCar,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleWishlist: () -> Unit,
    onToggleFavorite: () -> Unit = {},
    onUpdateCar: ((DiecastCar) -> Unit)? = null,
    onDismiss: () -> Unit,
    currencyCode: String = "TRL",
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        CarDetailContent(
            car = car,
            onEdit = onEdit,
            onDelete = onDelete,
            onToggleWishlist = onToggleWishlist,
            onToggleFavorite = onToggleFavorite,
            onUpdateCar = onUpdateCar,
            onDismiss = onDismiss,
            currencyCode = currencyCode,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 28.dp)
        )
    }
}

/**
 * Tablet çift panel modunda sağ tarafta henüz bir araç seçilmediğinde gösterilen boş durum paneli.
 */
@Composable
fun TabletCarDetailEmptyPane(
    isTr: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(42.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = if (isTr) "Model Detayı Seçilmedi" else "No Model Selected",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isTr) "Detayları, fotoğrafları, piyasa değerini ve özellikleri görmek için soldaki listeden bir model seçin."
                else "Select a model car from the list on the left to view specifications, photos, and valuation.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

/**
 * Share diecast car details via social media or messaging apps
 */
fun shareDiecastCar(context: Context, car: DiecastCar, currencyCode: String, isTr: Boolean) {
    val title = "${car.manufacturer} ${car.carBrand} ${car.model}".trim()
    val sb = StringBuilder()
    sb.append("🏎️ $title\n\n")
    if (car.scale.isNotBlank()) sb.append("• ${if (isTr) "Ölçek" else "Scale"}: ${car.scale}\n")
    if (car.series.isNotBlank()) sb.append("• ${if (isTr) "Seri" else "Series"}: ${car.series}\n")
    if (car.modelYear.isNotBlank()) sb.append("• ${if (isTr) "Model Yılı" else "Model Year"}: ${car.modelYear}\n")
    if (car.productionYear.isNotBlank()) sb.append("• ${if (isTr) "Üretim Yılı" else "Release Year"}: ${car.productionYear}\n")
    if (car.color.isNotBlank()) sb.append("• ${if (isTr) "Renk" else "Color"}: ${car.color}\n")
    if (car.condition.isNotBlank()) sb.append("• ${if (isTr) "Paket Durumu" else "Condition"}: ${car.condition}\n")
    if (car.purchasePrice > 0) sb.append("• ${if (isTr) "Alış Fiyatı" else "Purchase Price"}: $currencyCode ${formatAmount(car.purchasePrice)}\n")
    if (car.estimatedValue > 0) sb.append("• ${if (isTr) "Tahmini Değer" else "Estimated Value"}: $currencyCode ${formatAmount(car.estimatedValue)}\n")
    if (car.barcode.isNotBlank()) sb.append("• ${if (isTr) "Barkod" else "Barcode"}: ${car.barcode}\n")
    if (car.notes.isNotBlank()) sb.append("\n📝 ${if (isTr) "Notlar" else "Notes"}:\n${car.notes}\n")
    sb.append("\n${if (isTr) "📱 Diecast Koleksiyonumdan paylaşıldı." else "📱 Shared from my Diecast Collection."}")

    val shareText = sb.toString()
    val primaryPhoto = car.getPhotoList().firstOrNull() ?: car.photoPath

    val photoFile = primaryPhoto?.takeIf { it.isNotBlank() }?.let { File(it) }
    if (photoFile != null && photoFile.exists()) {
        try {
            val photoUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, photoUri)
                putExtra(Intent.EXTRA_TEXT, shareText)
                putExtra(Intent.EXTRA_SUBJECT, title)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, if (isTr) "Modeli Paylaş" else "Share Model"))
            return
        } catch (e: Exception) {
            android.util.Log.e("CarDetailDialog", "Error sharing with photo uri: ${e.message}")
        }
    }

    // Text only fallback
    val textIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(textIntent, if (isTr) "Modeli Paylaş" else "Share Model"))
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
