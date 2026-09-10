package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import com.example.util.formatAmount
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.activity.compose.BackHandler
import android.content.res.Configuration
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.DiecastCar
import com.example.ui.components.AddEditCarBottomSheet
import com.example.ui.components.AddEditCarDialog
import com.example.ui.components.BarcodeScannerDialog
import com.example.ui.components.CarDetailBottomSheet
import com.example.ui.components.CollectionTipCard
import com.example.ui.components.DiagnosticsDialog
import com.example.ui.components.DiecastCard
import com.example.ui.components.FilterBar
import com.example.ui.components.ManufacturerPriceSyncDialog
import com.example.ui.components.PaywallDialog
import com.example.ui.components.PrivacyPolicyDialog
import com.example.ui.components.SettingsDialog
import com.example.ui.components.StatsView
import com.example.ui.components.TabletCarDetailEmptyPane
import com.example.ui.components.TabletCarDetailPane
import com.example.ui.viewmodel.CollectionTab
import com.example.ui.viewmodel.DiecastViewModel

import com.example.util.isTurkishLocale

/**
 * Ekran Genişlik Sınıfları (Window Width Size Class):
 * - COMPACT: Telefon (< 600 dp)
 * - MEDIUM: Katlanabilir cihaz / dikey tablet (600 dp - 839 dp)
 * - EXPANDED: Tablet / Geniş ekran (>= 840 dp)
 */
enum class WindowWidthType {
    COMPACT,   // Telefon
    MEDIUM,    // Katlanabilir cihaz
    EXPANDED   // Tablet
}

/**
 * Ekranın genişliğini anlık ölçerek Compact (Telefon), Medium (Katlanabilir cihaz)
 * veya Expanded (Tablet) değerlerini döndüren fonksiyon.
 */
@Composable
fun rememberWindowWidthType(windowSizeClass: WindowSizeClass? = null): WindowWidthType {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    return remember(screenWidth, windowSizeClass) {
        if (windowSizeClass != null) {
            when (windowSizeClass.widthSizeClass) {
                WindowWidthSizeClass.Compact -> WindowWidthType.COMPACT
                WindowWidthSizeClass.Medium -> WindowWidthType.MEDIUM
                WindowWidthSizeClass.Expanded -> WindowWidthType.EXPANDED
                else -> WindowWidthType.COMPACT
            }
        } else {
            when {
                screenWidth < 600.dp -> WindowWidthType.COMPACT
                screenWidth < 840.dp -> WindowWidthType.MEDIUM
                else -> WindowWidthType.EXPANDED
            }
        }
    }
}

/**
 * Verilen Dp genişliğine göre anlık WindowWidthType hesaplayan yardımcı fonksiyon.
 */
fun calculateWindowWidthType(width: Dp): WindowWidthType {
    return when {
        width < 600.dp -> WindowWidthType.COMPACT
        width < 840.dp -> WindowWidthType.MEDIUM
        else -> WindowWidthType.EXPANDED
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: DiecastViewModel,
    windowSizeClass: WindowSizeClass? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTr = isTurkishLocale()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val isGridView by viewModel.isGridView.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()

    val filteredCars by viewModel.filteredCars.collectAsState()
    val allCars by viewModel.allCarsFlow.collectAsState(initial = emptyList())

    val availableManufacturers by viewModel.availableManufacturers.collectAsState()
    val availableCarBrands by viewModel.availableCarBrands.collectAsState()
    val availableScales by viewModel.availableScales.collectAsState()
    val availableTags by viewModel.availableTags.collectAsState()

    val selectedCarForDetail by viewModel.selectedCarForDetail.collectAsState()
    val carToEdit by viewModel.carToEdit.collectAsState()
    val showAddEditDialog by viewModel.showAddEditDialog.collectAsState()
    val showBarcodeScanner by viewModel.showBarcodeScanner.collectAsState()

    val showSettingsDialog by viewModel.showSettingsDialog.collectAsState()
    val showPaywallDialog by viewModel.showPaywallDialog.collectAsState()
    val showDiagnosticsDialog by viewModel.showDiagnosticsDialog.collectAsState()
    val showPrivacyPolicyDialog by viewModel.showPrivacyPolicyDialog.collectAsState()
    val showManufacturerPriceSyncDialog by viewModel.showManufacturerPriceSyncDialog.collectAsState()
    val lastPriceSyncDate by viewModel.lastPriceSyncDate.collectAsState()
    val isCloudProSubscriber by viewModel.isCloudProSubscriber.collectAsState()
    val trialDaysRemaining by viewModel.trialDaysRemaining.collectAsState()
    val formattedPrice by viewModel.formattedPrice.collectAsState()
    val billingFeedback by viewModel.billingFeedback.collectAsState()
    val isBillingConnecting by viewModel.isBillingConnecting.collectAsState()
    val lastSyncTime by viewModel.lastSyncTime.collectAsState()
    val isEngineSoundEnabled by viewModel.isEngineSoundEnabled.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()
    val currencyCode by viewModel.currencyCode.collectAsState()

    val isTrialExpired = !isCloudProSubscriber && trialDaysRemaining <= 0

    // Auto-prompt paywall if trial is expired
    LaunchedEffect(isCloudProSubscriber, trialDaysRemaining) {
        if (!isCloudProSubscriber && trialDaysRemaining <= 0) {
            viewModel.openPaywall()
        }
    }

    val collectionCount = allCars.count { !it.isWishlist }
    val wishlistCount = allCars.count { it.isWishlist }

    val pageSize = 20
    var currentPage by rememberSaveable { mutableIntStateOf(1) }

    val totalItems = filteredCars.size
    val totalPages = if (totalItems <= 0) {
        1
    } else if (totalItems % pageSize == 0) {
        totalItems / pageSize
    } else {
        (totalItems / pageSize) + 1
    }

    LaunchedEffect(selectedTab, filterState, sortOption) {
        currentPage = 1
    }

    LaunchedEffect(totalItems) {
        if (currentPage > totalPages) {
            currentPage = maxOf(1, totalPages)
        }
    }

    val startIndex = (currentPage - 1) * pageSize
    val endIndex = minOf(startIndex + pageSize, totalItems)
    val paginatedCars = remember(filteredCars, currentPage) {
        if (startIndex < totalItems) {
            filteredCars.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val windowWidthType = rememberWindowWidthType(windowSizeClass)

    // Tablet tespiti: Android sw600dp standardı veya geniş pencere boyutu
    val isTablet = (configuration.smallestScreenWidthDp >= 600) ||
                   (windowWidthType == WindowWidthType.EXPANDED) ||
                   (configuration.screenWidthDp >= 768) ||
                   (windowSizeClass?.widthSizeClass != WindowWidthSizeClass.Compact && windowSizeClass?.heightSizeClass != WindowHeightSizeClass.Compact)

    // Google Play Large Screen App Quality Spec:
    // 1. Compact (< 600dp): Ekranın altında NavigationBar (Bottom Navigation)
    // 2. Medium (600dp - 839dp): Sol tarafa yerleşen NavigationRail (dikey ince bar)
    // 3. Expanded (≥ 840dp): Sol tarafta açık kalan kalıcı NavigationDrawer (Geniş menü)
    val isExpandedNav = (windowWidthType == WindowWidthType.EXPANDED) || (configuration.screenWidthDp >= 840)
    val isMediumNav = !isExpandedNav && ((windowWidthType == WindowWidthType.MEDIUM) || (configuration.screenWidthDp >= 600) || isTablet || (isLandscape && configuration.screenWidthDp >= 540))
    val showBottomBar = !isExpandedNav && !isMediumNav

    // Tablet modunda bir araç detayı açıkken Android geri tuşu/hareketi ile detayı kapatıp listeye dönme
    BackHandler(enabled = isTablet && selectedCarForDetail != null) {
        viewModel.selectedCarForDetail.value = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.img_diecast_logo_1785160977141),
                            contentDescription = "Diecast Collection Logo",
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Diecast Collection",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isTr) "Model Araba Kataloğu" else "Diecast Model Catalog",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (isCloudProSubscriber) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFFFB300), // Amber / Gold Highlight
                                        border = BorderStroke(1.dp, Color(0xFFFF8F00)),
                                        shadowElevation = 2.dp
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.WorkspacePremium,
                                                contentDescription = null,
                                                tint = Color(0xFF1E293B),
                                                modifier = Modifier.size(11.dp)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = "PRO",
                                                color = Color(0xFF0F172A),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.openAddCarDialog(isWishlist = (selectedTab == CollectionTab.WISHLIST))
                        },
                        modifier = Modifier.testTag("btn_add_car")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = if (selectedTab == CollectionTab.WISHLIST) {
                                if (isTr) "İstek Ekle" else "Add to Wishlist"
                            } else {
                                if (isTr) "Model Ekle" else "Add Model"
                            },
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { viewModel.openSettings() },
                        modifier = Modifier.testTag("btn_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = if (isTr) "Ayarlar" else "Settings",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        // Telefonlarda (veya dikey dar modda) ekranın altında yer alan menü barı (Bottom Navigation)
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bottom_navigation_bar")
                ) {
                    CollectionTab.values().forEach { tab ->
                        val icon = when (tab) {
                            CollectionTab.COLLECTION -> Icons.Default.Inventory2
                            CollectionTab.WISHLIST -> Icons.Default.Favorite
                            CollectionTab.STATS -> Icons.Default.PieChart
                        }
                        NavigationBarItem(
                            selected = selectedTab == tab,
                            onClick = { viewModel.selectedTab.value = tab },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = tab.label
                                )
                            },
                            label = {
                                Text(
                                    text = tab.label,
                                    fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier
                                .pointerHoverIcon(PointerIcon.Hand)
                                .testTag("bottom_nav_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Anlık ölçüme göre dikey / yatay durum tespiti
            val localIsLandscape = maxWidth > maxHeight
            val isCompactHeight = maxHeight < 500.dp

            // Tablet çift panel (split-pane) durumu:
            // Tablet ekranlarında (>768px, >1024px hem dikey hem yatay) koleksiyon ve istek listesi sekmelerinde
            // daima sol tarafta liste/seçimler, sağ tarafta detay içeriği olan iki sütunlu (Master-Detail) yapı kullanılır.
            val isTabletSplitPane = (isTablet || maxWidth >= 720.dp) && (selectedTab != CollectionTab.STATS)

            // Esnek weight oranları (Kesinlikle sabit piksel/dp genişlik kullanılmaz, ekrana göre dinamik esner):
            val listPaneWeight = when {
                !isTabletSplitPane -> 1.0f
                maxWidth >= 1000.dp -> 0.38f // Geniş ekranlarda sol panel %38
                localIsLandscape -> 0.40f   // Yatay tablet modunda sol panel %40
                else -> 0.42f              // Dikey tablet modunda sol panel %42
            }
            val detailPaneWeight = when {
                !isTabletSplitPane -> 0.0f
                maxWidth >= 1000.dp -> 0.62f // Geniş ekranlarda sağ panel %62
                localIsLandscape -> 0.60f   // Yatay tablet modunda sağ panel %60
                else -> 0.58f              // Dikey tablet modunda sağ panel %58
            }

            Row(modifier = Modifier.fillMaxSize()) {
                // Large Screen Navigation Architecture (Google Play Large Screen App Quality Tier 1/2 Spec):
                // 1. Expanded (≥ 840dp): Permanent Navigation Drawer (Geniş yan menü, açık kalır)
                // 2. Medium (600dp - 839dp): Navigation Rail (Dikey ince bar)
                // 3. Compact (< 600dp): Bottom Navigation Bar
                if (isExpandedNav) {
                    PermanentDrawerSheet(
                        modifier = Modifier
                            .width(240.dp)
                            .fillMaxHeight(),
                        drawerContainerColor = MaterialTheme.colorScheme.surface,
                        drawerContentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        // App Branding Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsCar,
                                        contentDescription = "Diecast Collection",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Diecast Collection",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isTr) "Model Kataloğu" else "Diecast Catalog",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick Add Extended Button
                        Button(
                            onClick = {
                                viewModel.openAddCarDialog(isWishlist = (selectedTab == CollectionTab.WISHLIST))
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp)
                                .height(48.dp)
                                .pointerHoverIcon(PointerIcon.Hand)
                                .testTag("drawer_btn_add_car")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (selectedTab == CollectionTab.WISHLIST) {
                                    if (isTr) "İstek Listesine Ekle" else "Add to Wishlist"
                                } else {
                                    if (isTr) "Yeni Model Ekle" else "Add New Model"
                                },
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Navigation Items
                        CollectionTab.values().forEach { tab ->
                            val icon = when (tab) {
                                CollectionTab.COLLECTION -> Icons.Default.Inventory2
                                CollectionTab.WISHLIST -> Icons.Default.Favorite
                                CollectionTab.STATS -> Icons.Default.PieChart
                            }
                            val count = when (tab) {
                                CollectionTab.COLLECTION -> collectionCount
                                CollectionTab.WISHLIST -> wishlistCount
                                CollectionTab.STATS -> null
                            }
                            NavigationDrawerItem(
                                label = {
                                    Text(
                                        text = tab.label,
                                        fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                selected = selectedTab == tab,
                                onClick = { viewModel.selectedTab.value = tab },
                                icon = {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = tab.label
                                    )
                                },
                                badge = count?.let {
                                    {
                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = if (selectedTab == tab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        ) {
                                            Text(
                                                text = "$it",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (selectedTab == tab) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                                    .pointerHoverIcon(PointerIcon.Hand)
                                    .testTag("drawer_${tab.name.lowercase()}"),
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    selectedIconColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Settings Item
                        NavigationDrawerItem(
                            label = {
                                Text(if (isTr) "Ayarlar" else "Settings")
                            },
                            selected = showSettingsDialog,
                            onClick = { viewModel.openSettings() },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = if (isTr) "Ayarlar" else "Settings"
                                )
                            },
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .pointerHoverIcon(PointerIcon.Hand)
                                .testTag("drawer_settings"),
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedIconColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                } else if (isMediumNav) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxHeight(),
                        header = {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsCar,
                                        contentDescription = "Diecast Collection",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            FloatingActionButton(
                                onClick = {
                                    viewModel.openAddCarDialog(isWishlist = (selectedTab == CollectionTab.WISHLIST))
                                },
                                shape = RoundedCornerShape(14.dp),
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier
                                    .size(48.dp)
                                    .pointerHoverIcon(PointerIcon.Hand)
                                    .testTag("rail_fab_add_car")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = if (selectedTab == CollectionTab.WISHLIST) {
                                        if (isTr) "İstek Ekle" else "Add Wishlist"
                                    } else {
                                        if (isTr) "Model Ekle" else "Add Model"
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    ) {
                        CollectionTab.values().forEach { tab ->
                            val icon = when (tab) {
                                CollectionTab.COLLECTION -> Icons.Default.Inventory2
                                CollectionTab.WISHLIST -> Icons.Default.Favorite
                                CollectionTab.STATS -> Icons.Default.PieChart
                            }
                            NavigationRailItem(
                                selected = selectedTab == tab,
                                onClick = { viewModel.selectedTab.value = tab },
                                icon = {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.label,
                                        fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 11.sp
                                    )
                                },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier
                                    .pointerHoverIcon(PointerIcon.Hand)
                                    .testTag("rail_${tab.name.lowercase()}")
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        NavigationRailItem(
                            selected = showSettingsDialog,
                            onClick = { viewModel.openSettings() },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = if (isTr) "Ayarlar" else "Settings"
                                )
                            },
                            label = {
                                Text(
                                    text = if (isTr) "Ayarlar" else "Settings",
                                    fontSize = 10.sp
                                )
                            },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier
                                .pointerHoverIcon(PointerIcon.Hand)
                                .testTag("rail_settings")
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                // Sol Panel: Liste, Filtreler ve İçerik (Ekrana göre esneyen weight kullanımı: Tablet modunda %40/%42, tek panelde %100)
                Column(
                    modifier = Modifier
                        .weight(listPaneWeight)
                        .fillMaxHeight()
                ) {
            // 10-Day Free Trial Banner (Shown only when not purchased)
            if (!isCloudProSubscriber) {
                Surface(
                    color = if (trialDaysRemaining > 0) MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = if (isCompactHeight) 4.dp else 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (trialDaysRemaining > 0) Icons.Default.Star else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (trialDaysRemaining > 0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (trialDaysRemaining > 0) {
                                    if (isTr) "10 Günlük Deneme: $trialDaysRemaining gün kaldı"
                                    else "10-Day Trial: $trialDaysRemaining days left"
                                } else {
                                    if (isTr) "Deneme Süresi Doldu ($15.00)"
                                    else "Trial Expired ($15.00)"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (trialDaysRemaining > 0) MaterialTheme.colorScheme.onTertiaryContainer
                                else MaterialTheme.colorScheme.onErrorContainer
                            )
                        }

                        Button(
                            onClick = { viewModel.openPaywall() },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (trialDaysRemaining > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = if (trialDaysRemaining > 0) {
                                    if (isTr) "$15 ile PRO Al" else "Get PRO ($15)"
                                } else {
                                    if (isTr) "Satın Al ($15)" else "Unlock ($15)"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Ekran üstündeki sekmeler yerine telefonlarda altta Bottom Navigation, tabletlerde ise solda Navigation Rail kullanılır.

            if (selectedTab == CollectionTab.STATS) {
                // Statistics Screen View
                StatsView(
                    allCars = allCars,
                    currencyCode = currencyCode,
                    onCarClick = { car ->
                        viewModel.selectedCarForDetail.value = car
                    },
                    onOpenPriceSync = {
                        viewModel.openManufacturerPriceSync()
                    },
                    lastPriceSyncDate = lastPriceSyncDate,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            } else {
                if (filteredCars.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        FilterBar(
                            filterState = filterState,
                            sortOption = sortOption,
                            isGridView = isGridView,
                            availableManufacturers = availableManufacturers,
                            availableCarBrands = availableCarBrands,
                            availableScales = availableScales,
                            availableTags = availableTags,
                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                            onFilterChange = { newFilter -> viewModel.filterState.value = newFilter },
                            onSortChange = { newSort -> viewModel.sortOption.value = newSort },
                            onToggleViewMode = { viewModel.toggleViewMode() },
                            onOpenScanner = { viewModel.showBarcodeScanner.value = true },
                            onClearFilters = { viewModel.clearFilters() }
                        )

                        EmptyCollectionState(
                            isSearchActive = filterState.searchQuery.isNotBlank() || filterState.manufacturer != null,
                            tab = selectedTab,
                            onAddClick = { viewModel.openAddCarDialog(isWishlist = selectedTab == CollectionTab.WISHLIST) },
                            onClearFilter = { viewModel.clearFilters() }
                        )
                    }
                } else {
                    if (isGridView) {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 180.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .testTag("car_grid")
                        ) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                FilterBar(
                                    filterState = filterState,
                                    sortOption = sortOption,
                                    isGridView = isGridView,
                                    availableManufacturers = availableManufacturers,
                                    availableCarBrands = availableCarBrands,
                                    availableScales = availableScales,
                                    availableTags = availableTags,
                                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                    onFilterChange = { newFilter -> viewModel.filterState.value = newFilter },
                                    onSortChange = { newSort -> viewModel.sortOption.value = newSort },
                                    onToggleViewMode = { viewModel.toggleViewMode() },
                                    onOpenScanner = { viewModel.showBarcodeScanner.value = true },
                                    onClearFilters = { viewModel.clearFilters() },
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }

                            items(paginatedCars, key = { it.id }) { car ->
                                DiecastCard(
                                    car = car,
                                    isGridView = true,
                                    isSelected = (isTabletSplitPane && selectedCarForDetail?.id == car.id),
                                    onClick = { viewModel.selectedCarForDetail.value = car },
                                    onToggleWishlist = { viewModel.toggleWishlistStatus(car) },
                                    onToggleFavorite = { viewModel.toggleFavoriteStatus(car) },
                                    currencyCode = currencyCode
                                )
                            }

                            item(span = { GridItemSpan(maxLineSpan) }) {
                                PaginationBar(
                                    currentPage = currentPage,
                                    totalPages = totalPages,
                                    totalItems = totalItems,
                                    pageSize = pageSize,
                                    onPageChange = { newPage -> currentPage = newPage }
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .testTag("car_list")
                        ) {
                            item {
                                FilterBar(
                                    filterState = filterState,
                                    sortOption = sortOption,
                                    isGridView = isGridView,
                                    availableManufacturers = availableManufacturers,
                                    availableCarBrands = availableCarBrands,
                                    availableScales = availableScales,
                                    availableTags = availableTags,
                                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                    onFilterChange = { newFilter -> viewModel.filterState.value = newFilter },
                                    onSortChange = { newSort -> viewModel.sortOption.value = newSort },
                                    onToggleViewMode = { viewModel.toggleViewMode() },
                                    onOpenScanner = { viewModel.showBarcodeScanner.value = true },
                                    onClearFilters = { viewModel.clearFilters() },
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }

                            items(paginatedCars, key = { it.id }) { car ->
                                DiecastCard(
                                    car = car,
                                    isGridView = false,
                                    isSelected = (isTabletSplitPane && selectedCarForDetail?.id == car.id),
                                    onClick = { viewModel.selectedCarForDetail.value = car },
                                    onToggleWishlist = { viewModel.toggleWishlistStatus(car) },
                                    onToggleFavorite = { viewModel.toggleFavoriteStatus(car) },
                                    currencyCode = currencyCode
                                )
                            }

                            item {
                                PaginationBar(
                                    currentPage = currentPage,
                                    totalPages = totalPages,
                                    totalItems = totalItems,
                                    pageSize = pageSize,
                                    onPageChange = { newPage -> currentPage = newPage }
                                )
                            }
                        }
                    }
                }
            }
        }

                // Tablet ekranındayken ekranı ikiye böl; sağ tarafta seçilen öğenin detayları yan yana görünsün
                if (isTabletSplitPane) {
                    if (selectedCarForDetail != null) {
                        TabletCarDetailPane(
                            car = selectedCarForDetail!!,
                            onEdit = {
                                viewModel.openEditCarDialog(selectedCarForDetail!!)
                            },
                            onDelete = {
                                viewModel.deleteCar(selectedCarForDetail!!)
                            },
                            onToggleWishlist = {
                                viewModel.toggleWishlistStatus(selectedCarForDetail!!)
                            },
                            onToggleFavorite = {
                                viewModel.toggleFavoriteStatus(selectedCarForDetail!!)
                            },
                            onDismiss = {
                                viewModel.selectedCarForDetail.value = null
                            },
                            onUpdateCar = { carToUpdate ->
                                viewModel.saveCar(carToUpdate)
                            },
                            currencyCode = currencyCode,
                            modifier = Modifier
                                .weight(detailPaneWeight)
                                .fillMaxHeight()
                                .padding(start = 8.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
                        )
                    } else {
                        TabletCarDetailEmptyPane(
                            isTr = isTr,
                            modifier = Modifier
                                .weight(detailPaneWeight)
                                .fillMaxHeight()
                                .padding(start = 8.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
                        )
                    }
                }
            }

            // Mobile (Telefon) BottomSheet: Tablet olmayan cihazlarda alttan açılan detay sayfası
            if (!isTablet) {
                selectedCarForDetail?.let { car ->
                    CarDetailBottomSheet(
                        car = car,
                        onEdit = {
                            viewModel.openEditCarDialog(car)
                        },
                        onDelete = {
                            viewModel.deleteCar(car)
                        },
                        onToggleWishlist = {
                            viewModel.toggleWishlistStatus(car)
                        },
                        onToggleFavorite = {
                            viewModel.toggleFavoriteStatus(car)
                        },
                        onDismiss = {
                            viewModel.selectedCarForDetail.value = null
                        },
                        onUpdateCar = { carToUpdate ->
                            viewModel.saveCar(carToUpdate)
                        },
                        currencyCode = currencyCode
                    )
                }
            }
        }
    }

    // Modal BottomSheets & Dialogs
    if (showAddEditDialog) {
        if (isTablet) {
            AddEditCarDialog(
                car = carToEdit,
                onSave = { carToSave -> viewModel.saveCar(carToSave) },
                onDelete = carToEdit?.let { car ->
                    {
                        viewModel.deleteCar(car)
                        viewModel.closeAddEditDialog()
                    }
                },
                onDismiss = { viewModel.closeAddEditDialog() },
                onOpenScanner = { onScanned ->
                    viewModel.openBarcodeScanner(onScanned)
                },
                currencyCode = currencyCode
            )
        } else {
            AddEditCarBottomSheet(
                car = carToEdit,
                onSave = { carToSave -> viewModel.saveCar(carToSave) },
                onDelete = carToEdit?.let { car ->
                    {
                        viewModel.deleteCar(car)
                        viewModel.closeAddEditDialog()
                    }
                },
                onDismiss = { viewModel.closeAddEditDialog() },
                onOpenScanner = { onScanned ->
                    viewModel.openBarcodeScanner(onScanned)
                },
                currencyCode = currencyCode
            )
        }
    }

    if (showManufacturerPriceSyncDialog) {
        ManufacturerPriceSyncDialog(
            allCars = allCars,
            currencyCode = currencyCode,
            onApplyUpdates = { updatedCars ->
                viewModel.applyManufacturerPriceUpdates(updatedCars)
            },
            onDismiss = {
                viewModel.closeManufacturerPriceSync()
            }
        )
    }

    if (showBarcodeScanner) {
        BarcodeScannerDialog(
            onBarcodeScanned = { scannedCode ->
                viewModel.handleBarcodeScanned(scannedCode)
            },
            onDismiss = {
                viewModel.showBarcodeScanner.value = false
            }
        )
    }

    if (showSettingsDialog) {
        val isDarkMode by viewModel.isDarkMode.collectAsState()
        val needsReviewCount = remember(allCars) {
            allCars.count { com.example.util.ModelValidator.validate(it).needsReview }
        }
        SettingsDialog(
            isProSubscriber = isCloudProSubscriber,
            trialDaysRemaining = trialDaysRemaining,
            lastSyncTime = lastSyncTime,
            isEngineSoundEnabled = isEngineSoundEnabled,
            isDarkMode = isDarkMode,
            appLanguage = appLanguage,
            currencyCode = currencyCode,
            needsReviewCount = needsReviewCount,
            onToggleEngineSound = { enabled -> viewModel.toggleEngineSound(enabled) },
            onToggleDarkMode = { isDark -> viewModel.toggleDarkMode(isDark) },
            onLanguageSelected = { lang -> viewModel.setLanguage(lang) },
            onCurrencySelected = { code -> viewModel.selectCurrency(code) },
            onExportCsv = { viewModel.exportCollectionCsv(context) },
            onExportJson = { viewModel.exportCollectionJson(context) },
            onOpenPaywall = { viewModel.openPaywall() },
            onDisconnectCloud = { viewModel.cancelCloudSubscription() },
            onRestorePurchases = { viewModel.restorePurchases() },
            onResetTrial = { viewModel.resetTrialForTesting() },
            onExpireTrial = { viewModel.expireTrialForTesting() },
            onOpenDiagnostics = { viewModel.openDiagnostics() },
            onOpenNeedsReview = { viewModel.openNeedsReviewFilter() },
            onOpenPriceSync = { viewModel.openManufacturerPriceSync() },
            onOpenPrivacyPolicy = { viewModel.openPrivacyPolicy() },
            onLoadSampleData = { viewModel.loadSampleCars() },
            onDismiss = { viewModel.closeSettings() }
        )
    }

    if (showPaywallDialog) {
        PaywallDialog(
            trialDaysRemaining = trialDaysRemaining,
            isTrialExpired = isTrialExpired,
            isProSubscriber = isCloudProSubscriber,
            formattedPrice = formattedPrice,
            billingFeedback = billingFeedback,
            isConnecting = isBillingConnecting,
            onDismissFeedback = { viewModel.clearBillingFeedback() },
            onRestorePurchases = { viewModel.restorePurchases() },
            onOpenPrivacyPolicy = { viewModel.openPrivacyPolicy() },
            onSubscribe = {
                val activity = context as? android.app.Activity
                viewModel.subscribeToCloudPro(activity)
            },
            onDismiss = { viewModel.closePaywall() }
        )
    }

    if (showDiagnosticsDialog) {
        DiagnosticsDialog(
            cars = allCars,
            onDismiss = { viewModel.closeDiagnostics() }
        )
    }

    if (showPrivacyPolicyDialog) {
        PrivacyPolicyDialog(
            onDismiss = { viewModel.closePrivacyPolicy() }
        )
    }
}

@Composable
fun EmptyCollectionState(
    isSearchActive: Boolean,
    tab: CollectionTab,
    onAddClick: () -> Unit,
    onClearFilter: () -> Unit
) {
    val isTr = isTurkishLocale()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = CircleShape,
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isSearchActive) Icons.Default.SearchOff else Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isSearchActive) {
                    if (isTr) "Aramanıza Uygun Model Bulunamadı" else "No Matching Model Found"
                } else if (tab == CollectionTab.WISHLIST) {
                    if (isTr) "İstek Listeniz Boş" else "Your Wishlist is Empty"
                } else {
                    if (isTr) "Koleksiyonunuzda Henüz Araba Yok" else "No Cars in Your Collection"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isSearchActive) {
                    if (isTr) "Filtreleri veya arama kelimesini temizleyerek tekrar deneyin." else "Try clearing filters or search keywords."
                } else {
                    if (isTr) "Koleksiyonunuzu büyütmek için yeni bir model ekleyin." else "Add a new model to expand your collection."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (isSearchActive) {
                Button(onClick = onClearFilter) {
                    Text(if (isTr) "Aramayı Temizle" else "Clear Search")
                }
            } else {
                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isTr) "İlk Modeli Ekle" else "Add First Model")
                }
            }
        }
    }
}

@Composable
fun PaginationBar(
    currentPage: Int,
    totalPages: Int,
    totalItems: Int,
    pageSize: Int,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isTr = isTurkishLocale()
    val startItem = if (totalItems == 0) 0 else ((currentPage - 1) * pageSize) + 1
    val endItem = minOf(currentPage * pageSize, totalItems)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Page Info (Left aligned)
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = if (isTr) "Sayfa $currentPage / $totalPages" else "Page $currentPage of $totalPages",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isTr) "$startItem - $endItem / Toplam $totalItems Model" else "$startItem - $endItem of $totalItems Models",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Prev / Next Navigation Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalIconButton(
                        onClick = { if (currentPage > 1) onPageChange(currentPage - 1) },
                        enabled = currentPage > 1,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_prev_page")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = if (isTr) "Önceki Sayfa" else "Previous Page",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    FilledTonalIconButton(
                        onClick = { if (currentPage < totalPages) onPageChange(currentPage + 1) },
                        enabled = currentPage < totalPages,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_next_page")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = if (isTr) "Sonraki Sayfa" else "Next Page",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Quick Page Chips (if totalPages > 1)
            if (totalPages > 1) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val pagesToShow = remember(currentPage, totalPages) {
                        val list = mutableListOf<Int>()
                        val rangeStart = maxOf(1, currentPage - 2)
                        val rangeEnd = minOf(totalPages, currentPage + 2)
                        if (rangeStart > 1) list.add(1)
                        if (rangeStart > 2) list.add(-1)
                        for (p in rangeStart..rangeEnd) list.add(p)
                        if (rangeEnd < totalPages - 1) list.add(-1)
                        if (rangeEnd < totalPages) list.add(totalPages)
                        list
                    }

                    pagesToShow.forEach { pageNum ->
                        if (pageNum == -1) {
                            Text(
                                text = "…",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )
                        } else {
                            Surface(
                                onClick = { onPageChange(pageNum) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (pageNum == currentPage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                border = if (pageNum == currentPage) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("chip_page_$pageNum")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "$pageNum",
                                        fontSize = 11.sp,
                                        fontWeight = if (pageNum == currentPage) FontWeight.Bold else FontWeight.Medium,
                                        color = if (pageNum == currentPage) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
