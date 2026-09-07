package com.example.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.CircularProgressIndicator
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.R
import com.example.data.DiecastCar
import com.example.util.ImageStorageHelper
import com.example.util.formatAmount
import com.example.util.parseAmount
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditCarFormContent(
    car: DiecastCar?,
    onSave: (DiecastCar) -> Unit,
    onDelete: (() -> Unit)? = null,
    onDismiss: () -> Unit,
    onOpenScanner: (onScanned: (String) -> Unit) -> Unit,
    currencyCode: String = "TRL",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appLanguage by com.example.util.LocaleHelper.appLanguage.collectAsState()
    val isTr = com.example.util.LocaleHelper.isTurkish()
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val initialCar = car ?: DiecastCar(
        manufacturer = "",
        carBrand = "",
        model = "",
        modelYear = "",
        scale = "",
        series = "",
        productionYear = "",
        condition = "",
        color = "",
        notes = "",
        barcode = ""
    )

    var manufacturer by remember { mutableStateOf(initialCar.manufacturer) }
    var carBrand by remember { mutableStateOf(initialCar.carBrand) }
    var model by remember { mutableStateOf(initialCar.model) }
    var modelYear by remember { mutableStateOf(initialCar.modelYear) }
    var scale by remember { mutableStateOf(initialCar.scale) }
    var series by remember { mutableStateOf(initialCar.series) }
    var productionYear by remember { mutableStateOf(initialCar.productionYear) }
    var condition by remember { mutableStateOf(initialCar.condition) }
    var color by remember { mutableStateOf(initialCar.color) }
    var purchasePriceStr by remember { mutableStateOf(if (initialCar.purchasePrice > 0) formatAmount(initialCar.purchasePrice) else "") }
    var estimatedValueStr by remember { mutableStateOf(if (initialCar.estimatedValue > 0) formatAmount(initialCar.estimatedValue) else "") }
    var photoList by remember { mutableStateOf(initialCar.getPhotoList()) }
    var selectedPreviewIndex by remember { mutableIntStateOf(0) }
    var pendingSlotIndex by remember { mutableStateOf<Int?>(null) }
    var showFullScreenPreview by remember { mutableStateOf(false) }
    var imageUrlInput by remember { mutableStateOf("") }
    var showUrlInputRow by remember { mutableStateOf(false) }

    if (showFullScreenPreview && photoList.isNotEmpty()) {
        CoilImagePreviewDialog(
            photos = photoList,
            initialIndex = selectedPreviewIndex,
            title = if (carBrand.isNotBlank() || model.isNotBlank()) "$carBrand $model".trim() else (if (isTr) "Model Görsel Önizleme" else "Model Image Preview"),
            subtitle = manufacturer.ifBlank { scale },
            onDismiss = { showFullScreenPreview = false }
        )
    }
    var notes by remember { mutableStateOf(initialCar.notes) }
    var tagsList by remember { mutableStateOf(initialCar.getTagList()) }
    var customTagInput by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf(initialCar.barcode) }
    var isWishlist by remember { mutableStateOf(initialCar.isWishlist) }

    // Field validation error states
    var brandError by remember { mutableStateOf<String?>(null) }
    var modelError by remember { mutableStateOf<String?>(null) }
    var seriesError by remember { mutableStateOf<String?>(null) }
    var colorError by remember { mutableStateOf<String?>(null) }

    val scaleList = listOf("1:18", "1:24", "1:32", "1:43", "1:64")
    val manufacturerList = listOf("Auto World", "Bburago", "Greenlight", "Hot Wheels", "Majorette", "Maisto", "Matchbox", "Mini GT", "Solido", if (isTr) "Diğer" else "Other")
    val carBrandList = listOf(
        "Alfa Romeo", "Aston Martin", "Audi", "Bentley", "BMW", "Bugatti",
        "Chevrolet", "Dodge", "Ferrari", "Ford", "Honda", "Hyundai", "Jaguar",
        "Koenigsegg", "Lamborghini", "Lancia", "Land Rover", "Lexus", "Lotus",
        "Maserati", "Mazda", "McLaren", "Mercedes-Benz", "Mitsubishi", "Nissan",
        "Pagani", "Peugeot", "Pontiac", "Porsche", "Renault", "Shelby",
        "Subaru", "Tesla", "Toyota", "Volkswagen", "Volvo",
        if (isTr) "Diğer" else "Other"
    )
    val seriesList = if (isTr) {
        listOf(
            "Mainline", "Boulevard", "Car Culture", "Fast & Furious",
            "Team Transport", "Track Stars", "Exotics", "Factory Fresh",
            "HW Screen Time", "HW Drift", "Moving Parts", "Retro Racers",
            "Target Red Edition", "Dollar General Exclusive", "Custom", "Diğer"
        )
    } else {
        listOf(
            "Mainline", "Boulevard", "Car Culture", "Fast & Furious",
            "Team Transport", "Track Stars", "Exotics", "Factory Fresh",
            "HW Screen Time", "HW Drift", "Moving Parts", "Retro Racers",
            "Target Red Edition", "Dollar General Exclusive", "Custom", "Other"
        )
    }
    val colorList = if (isTr) {
        listOf("Kırmızı", "Mavi", "Siyah", "Beyaz", "Sarı", "Yeşil", "Turuncu", "Gümüş", "Gri", "Altın", "Mor", "Bordo", "Lacivert", "Pembe", "Çok Renkli")
    } else {
        listOf("Red", "Blue", "Black", "White", "Yellow", "Green", "Orange", "Silver", "Grey", "Gold", "Purple", "Maroon", "Navy", "Pink", "Multi-Color")
    }

    val conditionOptions = if (isTr) {
        listOf("Kutulu", "Kutusuz / Açık", "Hasarlı / Deforme")
    } else {
        listOf("In Box", "Loose / Unboxed", "Damaged")
    }

    // Single Image Picker Launcher
    val singleImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val savedPath = ImageStorageHelper.saveImageToInternalStorage(context, it)
            if (savedPath != null) {
                val current = photoList.toMutableList()
                val idx = pendingSlotIndex
                if (idx != null && idx < 4) {
                    if (idx < current.size) {
                        current[idx] = savedPath
                    } else {
                        current.add(savedPath)
                    }
                    selectedPreviewIndex = idx
                } else if (current.size < 4) {
                    current.add(savedPath)
                    selectedPreviewIndex = current.size - 1
                }
                photoList = current.take(4)
            }
        }
    }

    // Multiple Image Picker Launcher
    val multipleImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val current = photoList.toMutableList()
            val availableSpace = 4 - current.size
            uris.take(availableSpace).forEach { uri ->
                ImageStorageHelper.saveImageToInternalStorage(context, uri)?.let { saved ->
                    current.add(saved)
                }
            }
            photoList = current.take(4)
            if (photoList.isNotEmpty()) {
                selectedPreviewIndex = photoList.size - 1
            }
        }
    }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(car) {
        isVisible = true
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .padding(bottom = 28.dp)
    ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (initialCar.id == 0) (if (isTr) "Yeni Diecast Ekle" else "Add New Diecast") else (if (isTr) "Modeli Düzenle" else "Edit Model"),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (initialCar.id != 0 && onDelete != null) {
                        IconButton(
                            onClick = { showDeleteConfirmDialog = true },
                            modifier = Modifier.testTag("btn_delete_car_from_edit")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = if (isTr) "Modeli Sil" else "Delete Model",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("btn_close_add_car")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (isTr) "Çıkış" else "Exit", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick QR / Barcode Scanner Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onOpenScanner { scannedCode ->
                            barcode = scannedCode
                            if (scannedCode.startsWith("887961") || scannedCode.startsWith("027084")) {
                                if (manufacturer.isBlank()) manufacturer = "Hot Wheels"
                            } else if (scannedCode.startsWith("4895180")) {
                                if (manufacturer.isBlank()) manufacturer = "Mini GT"
                            } else if (scannedCode.startsWith("090159")) {
                                if (manufacturer.isBlank()) manufacturer = "Matchbox"
                            } else if (scannedCode.startsWith("347333")) {
                                if (manufacturer.isBlank()) manufacturer = "Majorette"
                            }
                        }
                    },
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
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
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isTr) "Karekod / Barkod ile Modeli Doldur" else "Fill Model via QR / Barcode",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (barcode.isNotBlank()) (if (isTr) "Barkod: $barcode" else "Barcode: $barcode")
                                else (if (isTr) "Kutu üzerindeki karekodu taratarak otomatik kod ekleyin" else "Scan box code to automatically populate model details"),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    OutlinedButton(
                        onClick = {
                            onOpenScanner { scannedCode ->
                                barcode = scannedCode
                                if (scannedCode.startsWith("887961") || scannedCode.startsWith("027084")) {
                                    if (manufacturer.isBlank()) manufacturer = "Hot Wheels"
                                } else if (scannedCode.startsWith("4895180")) {
                                    if (manufacturer.isBlank()) manufacturer = "Mini GT"
                                } else if (scannedCode.startsWith("090159")) {
                                    if (manufacturer.isBlank()) manufacturer = "Matchbox"
                                } else if (scannedCode.startsWith("347333")) {
                                    if (manufacturer.isBlank()) manufacturer = "Majorette"
                                }
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("btn_scan_qr_top")
                    ) {
                        Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isTr) "Tarat" else "Scan", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Manuel Barkod Kodu Girişi (Karekod Girişi Satırının Altında)
            OutlinedTextField(
                value = barcode,
                onValueChange = { barcode = it },
                label = {
                    Text(
                        text = if (isTr) "Barkod / Kutu Kodu (Manuel Giriş)" else "Barcode / Box Code (Manual Entry)",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                placeholder = { Text(if (isTr) "Barkod numarasını yazın..." else "Type barcode number...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (barcode.isNotBlank()) {
                        IconButton(onClick = { barcode = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = if (isTr) "Temizle" else "Clear",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_manual_barcode"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Photo Selection Section (En Fazla 4 Adet)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isTr) "Araba Fotoğrafları (En fazla 4)" else "Car Photos (Max 4)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${photoList.size} / 4",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_exit_photo_section")
                    ) {
                        Text(
                            text = if (isTr) "Çıkış" else "Exit",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Preview Box
            val previewPhoto = photoList.getOrNull(selectedPreviewIndex) ?: photoList.firstOrNull()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        if (photoList.isNotEmpty()) {
                            showFullScreenPreview = true
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (!previewPhoto.isNullOrBlank()) {
                    val imgModel = if (File(previewPhoto).exists()) File(previewPhoto) else previewPhoto
                    SubcomposeAsyncImage(
                        model = imgModel,
                        contentDescription = if (isTr) "Coil önizleme fotoğrafı" else "Coil preview photo",
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop
                    ) {
                        val state = painter.state
                        if (state is coil.compose.AsyncImagePainter.State.Loading) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(32.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 3.dp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isTr) "Coil Yüklüyor..." else "Coil Loading...",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else if (state is coil.compose.AsyncImagePainter.State.Error) {
                            Image(
                                painter = painterResource(id = R.drawable.img_diecast_banner_1785160991932),
                                contentDescription = if (isTr) "Varsayılan kapak" else "Default cover",
                                modifier = Modifier.matchParentSize(),
                                contentScale = ContentScale.Crop,
                                alpha = 0.5f
                            )
                        } else {
                            SubcomposeAsyncImageContent()
                        }
                    }

                    // Zoom Badge
                    Surface(
                        color = Color.Black.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp)
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
                                text = if (isTr) "Coil Büyüt" else "Coil Zoom",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.img_diecast_banner_1785160991932),
                        contentDescription = if (isTr) "Varsayılan kapak" else "Default cover",
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop,
                        alpha = 0.4f
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = if (isTr) "Fotoğraf Ekle" else "Add Photo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isTr) "4 adede kadar fotoğraf veya URL ekleyin" else "Add up to 4 photos or URLs",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Main Photo label badge
                if (photoList.isNotEmpty() && (selectedPreviewIndex == 0 || previewPhoto == photoList.firstOrNull())) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(bottomEnd = 12.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = if (isTr) "Ana Fotoğraf" else "Main Photo",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4 Photo Thumbnails Grid Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (slotIndex in 0..3) {
                    val slotPhoto = photoList.getOrNull(slotIndex)
                    val isSelected = (slotIndex == selectedPreviewIndex) || (selectedPreviewIndex >= photoList.size && slotIndex == photoList.size - 1 && photoList.isNotEmpty())

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(
                                width = if (isSelected && slotPhoto != null) 2.dp else 1.dp,
                                color = if (isSelected && slotPhoto != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                if (slotPhoto != null) {
                                    selectedPreviewIndex = slotIndex
                                } else {
                                    pendingSlotIndex = slotIndex
                                    singleImagePickerLauncher.launch("image/*")
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (!slotPhoto.isNullOrBlank()) {
                            val imgFile = File(slotPhoto)
                            AsyncImage(
                                model = if (imgFile.exists()) imgFile else slotPhoto,
                                contentDescription = "Photo ${slotIndex + 1}",
                                modifier = Modifier.matchParentSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Delete X button on top-right corner
                            IconButton(
                                onClick = {
                                    val current = photoList.toMutableList()
                                    if (slotIndex < current.size) {
                                        current.removeAt(slotIndex)
                                        photoList = current
                                        selectedPreviewIndex = 0
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(24.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove photo",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            // Slot Label badge at bottom
                            Surface(
                                color = Color.Black.copy(alpha = 0.65f),
                                shape = RoundedCornerShape(topStart = 6.dp),
                                modifier = Modifier.align(Alignment.BottomEnd)
                            ) {
                                Text(
                                    text = if (slotIndex == 0) (if (isTr) "1. Ana" else "1st Main") else "${slotIndex + 1}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        } else {
                            // Empty slot placeholder
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Photo",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = if (slotIndex == 0) (if (isTr) "Foto 1 (Ana)" else "Photo 1") else "Foto ${slotIndex + 1}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row (Multiple picker & URL input toggle)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = {
                            if (photoList.size < 4) {
                                multipleImagePickerLauncher.launch("image/*")
                            }
                        },
                        enabled = photoList.size < 4,
                        modifier = Modifier.testTag("btn_pick_multiple_photos")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isTr) "Çoklu Fotoğraf" else "Multiple Photos",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    TextButton(
                        onClick = { showUrlInputRow = !showUrlInputRow },
                        modifier = Modifier.testTag("btn_toggle_url_input")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isTr) "URL Ekle" else "Add URL",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (photoList.isNotEmpty()) {
                    TextButton(
                        onClick = {
                            photoList = emptyList()
                            selectedPreviewIndex = 0
                        }
                    ) {
                        Text(
                            text = if (isTr) "Tümünü Temizle" else "Clear All",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Web URL Input Row (Animated)
            AnimatedVisibility(
                visible = showUrlInputRow,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = imageUrlInput,
                            onValueChange = { imageUrlInput = it },
                            label = { Text(if (isTr) "Web Görsel / Bağlantı URL'si" else "Web Image / Photo URL") },
                            placeholder = { Text("https://example.com/car.jpg") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("input_image_url"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Button(
                            onClick = {
                                if (imageUrlInput.isNotBlank()) {
                                    val current = photoList.toMutableList()
                                    if (current.size < 4) {
                                        current.add(imageUrlInput.trim())
                                        photoList = current
                                        selectedPreviewIndex = current.size - 1
                                        imageUrlInput = ""
                                        showUrlInputRow = false
                                        Toast.makeText(context, if (isTr) "Görsel Coil ile yüklendi!" else "Image loaded via Coil!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, if (isTr) "En fazla 4 fotoğraf eklenebilir!" else "Max 4 photos allowed!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            enabled = imageUrlInput.isNotBlank() && photoList.size < 4,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("btn_add_image_url")
                        ) {
                            Text(if (isTr) "Coil Ekle" else "Add")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Wishlist Switch Row
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isWishlist) (if (isTr) "İstek Listesinde Tutuluyor" else "In Wishlist") else (if (isTr) "Koleksiyonda Bulunuyor" else "In Collection"),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isWishlist) (if (isTr) "Henüz satın alınmadı, hedef listede gösterilir." else "Not purchased yet, shown in target list.") else (if (isTr) "Fiziksel koleksiyonunuzda mevcut." else "In physical collection."),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = isWishlist,
                        onCheckedChange = { isWishlist = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section: Core Specs
            Text(
                text = if (isTr) "Model Bilgileri" else "Model Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            var manufacturerExpanded by remember { mutableStateOf(false) }
            var brandExpanded by remember { mutableStateOf(false) }
            var seriesExpanded by remember { mutableStateOf(false) }
            var colorExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = manufacturerExpanded,
                onExpandedChange = { manufacturerExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = manufacturer,
                    onValueChange = {
                        manufacturer = it
                        manufacturerExpanded = true
                    },
                    label = { Text(if (isTr) "Üretici Marka" else "Manufacturer") },
                    placeholder = { Text(if (isTr) "ör. Hot Wheels, Matchbox, Mini GT" else "e.g. Hot Wheels, Matchbox, Mini GT") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = manufacturerExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .testTag("input_manufacturer"),
                    singleLine = true
                )

                val filteredManufacturers = manufacturerList
                    .filter { it.contains(manufacturer, ignoreCase = true) || manufacturer.isBlank() }
                    .sortedWith(compareBy { if (it == "Diğer" || it == "Other") "zzzz" else it.lowercase() })
                if (filteredManufacturers.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = manufacturerExpanded,
                        onDismissRequest = { manufacturerExpanded = false }
                    ) {
                        filteredManufacturers.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    manufacturer = item
                                    manufacturerExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Araba Markası & Model Adı
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = brandExpanded,
                    onExpandedChange = { brandExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = carBrand,
                        onValueChange = {
                            carBrand = it
                            brandError = null
                            brandExpanded = true
                        },
                        label = { Text(if (isTr) "Araba Markası *" else "Car Brand *") },
                        placeholder = { Text(if (isTr) "ör. Porsche" else "e.g. Porsche") },
                        isError = brandError != null,
                        supportingText = brandError?.let { err -> { Text(err, color = MaterialTheme.colorScheme.error) } },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("input_car_brand"),
                        singleLine = true
                    )

                    val filteredBrands = carBrandList
                        .filter { it.contains(carBrand, ignoreCase = true) || carBrand.isBlank() }
                        .sortedWith(compareBy { if (it == "Diğer" || it == "Other") "zzzz" else it.lowercase() })
                    if (filteredBrands.isNotEmpty()) {
                        ExposedDropdownMenu(
                            expanded = brandExpanded,
                            onDismissRequest = { brandExpanded = false }
                        ) {
                            filteredBrands.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        carBrand = item
                                        brandError = null
                                        brandExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = model,
                    onValueChange = {
                        model = it
                        modelError = null
                    },
                    label = { Text(if (isTr) "Model Adı *" else "Model Name *") },
                    placeholder = { Text(if (isTr) "ör. 911 GT3 RS" else "e.g. 911 GT3 RS") },
                    isError = modelError != null,
                    supportingText = modelError?.let { err -> { Text(err, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_model_name"),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Model Yılı & Üretim Yılı
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = modelYear,
                    onValueChange = { modelYear = it },
                    label = { Text(if (isTr) "Model Yılı" else "Model Year") },
                    placeholder = { Text(if (isTr) "ör. 2023" else "e.g. 2023") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_model_year"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = productionYear,
                    onValueChange = { productionYear = it },
                    label = { Text(if (isTr) "Üretim Yılı" else "Release Year") },
                    placeholder = { Text(if (isTr) "ör. 2024" else "e.g. 2024") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_production_year"),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ölçek Seçimi
            Text(
                text = if (isTr) "Ölçek" else "Scale",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                scaleList.forEach { item ->
                    FilterChip(
                        selected = scale.equals(item, ignoreCase = true),
                        onClick = { scale = item },
                        label = { Text(item, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Seri Adı & Renk (Otomatik Tamamlama & Doğrulama Destekli)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Seri Adı Dropdown
                ExposedDropdownMenuBox(
                    expanded = seriesExpanded,
                    onExpandedChange = { seriesExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = series,
                        onValueChange = {
                            series = it
                            seriesExpanded = true
                            seriesError = null
                        },
                        label = { Text(if (isTr) "Seri Adı" else "Series Name") },
                        placeholder = { Text(if (isTr) "ör. Boulevard / Mainline" else "e.g. Boulevard / Mainline") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = seriesExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        isError = seriesError != null,
                        supportingText = seriesError?.let { err -> { Text(err, color = MaterialTheme.colorScheme.error) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        singleLine = true
                    )

                    val filteredSeries = seriesList
                        .filter { it.contains(series, ignoreCase = true) || series.isBlank() }
                        .sortedWith(compareBy { if (it == "Diğer" || it == "Other") "zzzz" else it.lowercase() })
                    if (filteredSeries.isNotEmpty()) {
                        ExposedDropdownMenu(
                            expanded = seriesExpanded,
                            onDismissRequest = { seriesExpanded = false }
                        ) {
                            filteredSeries.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        series = item
                                        seriesExpanded = false
                                        seriesError = null
                                    }
                                )
                            }
                        }
                    }
                }

                // Renk Dropdown
                ExposedDropdownMenuBox(
                    expanded = colorExpanded,
                    onExpandedChange = { colorExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = color,
                        onValueChange = {
                            color = it
                            colorExpanded = true
                            colorError = null
                        },
                        label = { Text(if (isTr) "Renk" else "Color") },
                        placeholder = { Text(if (isTr) "ör. Kırmızı" else "e.g. Red") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = colorExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        isError = colorError != null,
                        supportingText = colorError?.let { err -> { Text(err, color = MaterialTheme.colorScheme.error) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        singleLine = true
                    )

                    val filteredColors = colorList
                        .filter { it.contains(color, ignoreCase = true) || color.isBlank() }
                    if (filteredColors.isNotEmpty()) {
                        ExposedDropdownMenu(
                            expanded = colorExpanded,
                            onDismissRequest = { colorExpanded = false }
                        ) {
                            filteredColors.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        color = item
                                        colorExpanded = false
                                        colorError = null
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Hızlı Renk Seçim Rozetleri (Quick Color Chips)
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val quickColors = colorList.take(8)
                quickColors.forEach { colorName ->
                    FilterChip(
                        selected = color.equals(colorName, ignoreCase = true),
                        onClick = {
                            color = colorName
                            colorError = null
                        },
                        label = { Text(colorName, style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hasar / Paket Durumu
            Text(
                text = if (isTr) "Paket / Hasar Durumu" else "Package / Condition",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                conditionOptions.forEach { optionLabel ->
                    FilterChip(
                        selected = condition.equals(optionLabel, ignoreCase = true),
                        onClick = { condition = optionLabel },
                        label = { Text(optionLabel) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondary,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Alış Fiyatı & Tahmini Değer (Üretici Rayici ile Birlikte)
            val currentDraftCarForPrice = remember(manufacturer, carBrand, model, scale, series, condition, modelYear, productionYear) {
                DiecastCar(
                    manufacturer = manufacturer,
                    carBrand = carBrand,
                    model = model,
                    scale = scale,
                    series = series,
                    condition = condition,
                    modelYear = modelYear,
                    productionYear = productionYear
                )
            }
            val autoEstimateResult = remember(currentDraftCarForPrice, currencyCode) {
                com.example.util.ManufacturerPriceService.estimateCarPrice(currentDraftCarForPrice, currencyCode)
            }

            // Auto-fill manufacturer benchmark value when blank
            LaunchedEffect(autoEstimateResult.estimatedValue) {
                if (estimatedValueStr.isBlank() && autoEstimateResult.estimatedValue > 0) {
                    estimatedValueStr = if (currencyCode == "TRL") {
                        autoEstimateResult.estimatedValue.toInt().toString()
                    } else {
                        com.example.util.formatAmount(autoEstimateResult.estimatedValue)
                    }
                }
            }

            Text(
                text = if (isTr) "Fiyat ve Değerleme" else "Pricing & Valuation",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = purchasePriceStr,
                    onValueChange = { purchasePriceStr = it },
                    label = {
                        Text(
                            text = if (isTr) "Alış Fiyatı" else "Purchase Price",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    prefix = { Text("$currencyCode ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp) },
                    placeholder = { Text("0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_purchase_price"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = estimatedValueStr,
                    onValueChange = { estimatedValueStr = it },
                    label = {
                        Text(
                            text = if (isTr) "Tahmini Değer ($currencyCode)" else "Est. Value ($currencyCode)",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    prefix = { Text("$currencyCode ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp) },
                    placeholder = { Text(com.example.util.formatAmount(autoEstimateResult.estimatedValue)) },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                estimatedValueStr = if (currencyCode == "TRL") {
                                    autoEstimateResult.estimatedValue.toInt().toString()
                                } else {
                                    com.example.util.formatAmount(autoEstimateResult.estimatedValue)
                                }
                            },
                            modifier = Modifier.size(28.dp).testTag("btn_auto_fill_mfg_price")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = if (isTr) "Üretici rayicini uygula" else "Apply MSRP",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    },
                    supportingText = {
                        Text(
                            text = if (isTr) "Üretici Rayici: $currencyCode ${com.example.util.formatAmount(autoEstimateResult.estimatedValue)}"
                            else "MSRP: $currencyCode ${com.example.util.formatAmount(autoEstimateResult.estimatedValue)}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_estimated_value"),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notlar & Kondisyon / Geçmiş Bilgisi (Notes, Condition & Provenance)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_car_notes_section"),
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

                        if (notes.isNotBlank()) {
                            Surface(
                                onClick = { notes = "" },
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Text(
                                    text = if (isTr) "Temizle" else "Clear",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isTr) {
                            "Modelin kutu/blister kondisyonu, hafif ezilme/çatlaklar, önceki sahibi veya hatıra geçmişini buraya kaydedebilirsiniz."
                        } else {
                            "Record box/blister condition, wear/cracks, previous owners, or provenance history here."
                        },
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Hızlı Not / Kondisyon & Geçmiş Etiketleri (Quick Condition & History Tags)
                    Text(
                        text = if (isTr) "Hızlı Etiket Ekle:" else "Quick Add Tag:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val quickNoteTags = if (isTr) {
                        listOf(
                            "Kutusunda kusursuz (Mint)",
                            "Kartonette hafif bükülme",
                            "Blisterde çatlak/sararma",
                            "İlk sahibinden alındı",
                            "Koruma kabında (Protector)",
                            "Yurtdışı seyahat hatırası",
                            "Custom tekerlek/boyama",
                            "Takas ile edinildi"
                        )
                    } else {
                        listOf(
                            "Mint in Blister",
                            "Minor card corner wear",
                            "Cracked/aged blister",
                            "From original owner",
                            "In protective case",
                            "Travel souvenir",
                            "Custom wheels/paint",
                            "Acquired via trade"
                        )
                    }

                    androidx.compose.foundation.layout.FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        quickNoteTags.forEach { tag ->
                            Surface(
                                onClick = {
                                    notes = if (notes.isBlank()) {
                                        tag
                                    } else if (!notes.contains(tag, ignoreCase = true)) {
                                        "$notes • $tag"
                                    } else {
                                        notes
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
                                modifier = Modifier.testTag("tag_note_${tag.hashCode()}")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = tag,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text(if (isTr) "Detaylı Notlar & Geçmiş" else "Detailed Notes & History") },
                        placeholder = {
                            Text(
                                if (isTr) "Örn: 2023 yılında koleksiyoner buluşmasından alındı, arka kartonette küçük bir kılcal çizik var."
                                else "e.g., Acquired at collector meet in 2023, minor hairline crease on bottom card corner."
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_car_notes"),
                        minLines = 3,
                        maxLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sınıflandırma ve Özel Etiketler (Tags / Classification System)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_car_tags_section"),
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
                                text = if (isTr) "Sınıflandırma ve Etiketler" else "Classification & Tags",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (tagsList.isNotEmpty()) {
                            Surface(
                                onClick = { tagsList = emptyList() },
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Text(
                                    text = if (isTr) "Temizle" else "Clear",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isTr) {
                            "Modeli 'Spor', 'Klasik', 'Yarış' gibi hazır kategorilerle veya kendi belirleyeceğiniz özel etiketlerle gruplayın."
                        } else {
                            "Classify your model with preset tags like 'Sport', 'Classic', 'Racing' or create your own custom tags."
                        },
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Preset Quick Tags
                    val presetTags = if (isTr) {
                        listOf("Spor", "Klasik", "Yarış", "Supercar", "JDM", "Muscle", "Off-Road", "Custom", "Film / Dizi", "Polis / Acil", "Konsept", "Elektrikli")
                    } else {
                        listOf("Sport", "Classic", "Racing", "Supercar", "JDM", "Muscle", "Off-Road", "Custom", "Movie / TV", "Police / Rescue", "Concept", "Electric")
                    }

                    Text(
                        text = if (isTr) "Önerilen Etiketler:" else "Suggested Tags:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        presetTags.forEach { presetTag ->
                            val isSelected = tagsList.any { it.equals(presetTag, ignoreCase = true) }
                            val tagColors = com.example.util.TagColorHelper.getColorSchemeForTag(presetTag)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    tagsList = if (isSelected) {
                                        tagsList.filterNot { it.equals(presetTag, ignoreCase = true) }
                                    } else {
                                        tagsList + presetTag
                                    }
                                },
                                label = { Text(presetTag, style = MaterialTheme.typography.labelSmall) },
                                leadingIcon = if (isSelected) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = tagColors.contentColor,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                } else {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = tagColors.contentColor,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                },
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = tagColors.borderColor.copy(alpha = 0.6f),
                                    selectedBorderColor = tagColors.borderColor,
                                    enabled = true,
                                    selected = isSelected
                                ),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = tagColors.containerColor,
                                    selectedLabelColor = tagColors.contentColor,
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    labelColor = MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.testTag("chip_preset_tag_$presetTag")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Custom Tag Input Field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customTagInput,
                            onValueChange = { customTagInput = it },
                            label = { Text(if (isTr) "Özel Etiket Yazın" else "Type Custom Tag") },
                            placeholder = { Text(if (isTr) "ör. Chase, Kırmızı Çizgi, Nadir..." else "e.g. Chase, Redline, Rare...") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_custom_tag"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )

                        Button(
                            onClick = {
                                val trimmed = customTagInput.trim()
                                if (trimmed.isNotBlank()) {
                                    if (!tagsList.any { it.equals(trimmed, ignoreCase = true) }) {
                                        tagsList = tagsList + trimmed
                                    }
                                    customTagInput = ""
                                }
                            },
                            enabled = customTagInput.isNotBlank(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(52.dp).testTag("btn_add_custom_tag")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isTr) "Ekle" else "Add")
                        }
                    }

                    // Display active assigned tags list with delete chip
                    if (tagsList.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isTr) "Seçili Etiketler (${tagsList.size}):" else "Assigned Tags (${tagsList.size}):",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            tagsList.forEach { tagItem ->
                                val tagColors = com.example.util.TagColorHelper.getColorSchemeForTag(tagItem)
                                Surface(
                                    onClick = {
                                        tagsList = tagsList.filterNot { it.equals(tagItem, ignoreCase = true) }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = tagColors.containerColor,
                                    border = BorderStroke(1.dp, tagColors.borderColor),
                                    modifier = Modifier.testTag("tag_item_$tagItem")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Sell,
                                            contentDescription = null,
                                            tint = tagColors.contentColor,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = tagItem,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = tagColors.contentColor
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Kaldır",
                                            tint = tagColors.contentColor,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Live Model Validation Warning Hint Box
            val liveDraftCar = initialCar.copy(
                manufacturer = manufacturer,
                carBrand = carBrand,
                model = model,
                modelYear = modelYear,
                scale = scale,
                series = series,
                productionYear = productionYear,
                purchasePrice = parseAmount(purchasePriceStr),
                estimatedValue = parseAmount(estimatedValueStr)
            )

            val liveValidation = remember(carBrand, model, manufacturer, series, scale, modelYear, productionYear, purchasePriceStr, estimatedValueStr) {
                com.example.util.ModelValidator.validate(liveDraftCar)
            }

            if (liveValidation.needsReview && carBrand.isNotBlank() && model.isNotBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                    border = BorderStroke(1.dp, Color(0xFFD97706))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isTr) "Model Standart Kontrol Uyarısı" else "Model Standard Check Warning",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        liveValidation.issues.forEach { issue ->
                            Text(
                                text = "• $issue",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = Color(0xFF78350F)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit & Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("btn_cancel_add_car"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isTr) "Çıkış" else "Exit",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        var hasError = false
                        if (carBrand.trim().isBlank()) {
                            brandError = if (isTr) "Araba markası boş bırakılamaz (*)" else "Car brand is required (*)"
                            hasError = true
                        } else {
                            brandError = null
                        }

                        if (model.trim().isBlank()) {
                            modelError = if (isTr) "Model adı boş bırakılamaz (*)" else "Model name is required (*)"
                            hasError = true
                        } else if (model.trim().length < 2) {
                            modelError = if (isTr) "Model adı en az 2 karakter olmalıdır" else "Model name must be at least 2 characters"
                            hasError = true
                        } else {
                            modelError = null
                        }

                        if (hasError) {
                            Toast.makeText(
                                context,
                                if (isTr) "Lütfen zorunlu alanları (Marka ve Model Adı) doldurun!" else "Please fill mandatory fields (Brand and Model Name)!",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        val pPrice = parseAmount(purchasePriceStr)
                        val eVal = parseAmount(estimatedValueStr)

                        val updatedCar = (car ?: DiecastCar()).copy(
                            manufacturer = manufacturer.trim(),
                            carBrand = carBrand.trim(),
                            model = model.trim(),
                            modelYear = modelYear.trim(),
                            scale = scale.trim(),
                            series = series.trim(),
                            productionYear = productionYear.trim(),
                            condition = condition.trim(),
                            color = color.trim(),
                            purchasePrice = pPrice,
                            estimatedValue = eVal,
                            photoPath = photoList.getOrNull(0),
                            photoPath2 = photoList.getOrNull(1),
                            photoPath3 = photoList.getOrNull(2),
                            photoPath4 = photoList.getOrNull(3),
                            notes = notes.trim(),
                            tags = tagsList.filter { it.isNotBlank() }.distinct().joinToString(", "),
                            barcode = barcode.trim(),
                            isWishlist = isWishlist
                        )
                        onSave(updatedCar)
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .height(52.dp)
                        .testTag("btn_save_car"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (initialCar.id == 0) (if (isTr) "Kaydet" else "Save") else (if (isTr) "Güncelle" else "Update"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
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
                    val carTitle = listOf(manufacturer, carBrand, model).filter { it.isNotBlank() }.joinToString(" ")
                    Text(
                        text = carTitle.ifBlank { if (isTr) "Bu Model" else "This Model" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    val subInfo = listOfNotNull(
                        scale.takeIf { it.isNotBlank() }?.let { if (isTr) "Ölçek: $it" else "Scale: $it" },
                        series.takeIf { it.isNotBlank() }?.let { if (isTr) "Seri: $it" else "Series: $it" }
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
                        onDelete?.invoke()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("btn_confirm_delete_from_edit")
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
                    modifier = Modifier.testTag("btn_cancel_delete_from_edit")
                ) {
                    Text(if (isTr) "Vazgeç" else "Cancel")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

/**
 * Telefon / Compact ekranlar için alttan açılan form sayfası (Modal Bottom Sheet)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCarBottomSheet(
    car: DiecastCar?,
    onSave: (DiecastCar) -> Unit,
    onDelete: (() -> Unit)? = null,
    onDismiss: () -> Unit,
    onOpenScanner: (onScanned: (String) -> Unit) -> Unit,
    currencyCode: String = "TRL",
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        AddEditCarFormContent(
            car = car,
            onSave = onSave,
            onDelete = onDelete,
            onDismiss = onDismiss,
            onOpenScanner = onOpenScanner,
            currencyCode = currencyCode
        )
    }
}

/**
 * Tablet / Geniş ekranlar için şık ve yönelime göre esnek diyalog penceresi (Adaptive Dialog)
 */
@Composable
fun AddEditCarDialog(
    car: DiecastCar?,
    onSave: (DiecastCar) -> Unit,
    onDelete: (() -> Unit)? = null,
    onDismiss: () -> Unit,
    onOpenScanner: (onScanned: (String) -> Unit) -> Unit,
    currencyCode: String = "TRL"
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val widthFraction = if (isLandscape) 0.72f else 0.88f

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(widthFraction)
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            AddEditCarFormContent(
                car = car,
                onSave = onSave,
                onDelete = onDelete,
                onDismiss = onDismiss,
                onOpenScanner = onOpenScanner,
                currencyCode = currencyCode
            )
        }
    }
}
