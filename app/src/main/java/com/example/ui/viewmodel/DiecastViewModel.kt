package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.DiecastCar
import com.example.data.DiecastRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import androidx.compose.runtime.Composable
import com.example.util.isTurkishLocale

enum class SortOption {
    NEWEST,
    OLDEST,
    PRICE_HIGH,
    PRICE_LOW,
    VALUE_HIGH,
    BRAND_AZ,
    MODEL_YEAR_NEWEST;

    val label: String
        @Composable
        get() {
            val isTr = isTurkishLocale()
            return when (this) {
                NEWEST -> if (isTr) "En Yeni Eklenen" else "Newest First"
                OLDEST -> if (isTr) "En Eski Eklenen" else "Oldest First"
                PRICE_HIGH -> if (isTr) "Alış Fiyatı (Yüksekten Düşüğe)" else "Price (High to Low)"
                PRICE_LOW -> if (isTr) "Alış Fiyatı (Düşükten Yükseğe)" else "Price (Low to High)"
                VALUE_HIGH -> if (isTr) "Tahmini Değer (Yüksekten Düşüğe)" else "Value (High to Low)"
                BRAND_AZ -> if (isTr) "Marka (A-Z)" else "Brand (A-Z)"
                MODEL_YEAR_NEWEST -> if (isTr) "Model Yılı (En Yeni)" else "Model Year (Newest)"
            }
        }
}

enum class CollectionTab {
    COLLECTION,
    WISHLIST,
    STATS;

    val label: String
        @Composable
        get() {
            val isTr = isTurkishLocale()
            return when (this) {
                COLLECTION -> if (isTr) "Koleksiyon" else "Collection"
                WISHLIST -> if (isTr) "İstek Listesi" else "Wishlist"
                STATS -> if (isTr) "İstatistikler" else "Statistics"
            }
        }
}

data class FilterState(
    val searchQuery: String = "",
    val manufacturer: String? = null,
    val carBrand: String? = null,
    val scale: String? = null,
    val condition: String? = null,
    val color: String? = null,
    val series: String? = null,
    val selectedTag: String? = null,
    val selectedTags: Set<String> = emptySet(),
    val onlyFavorites: Boolean = false,
    val onlyNeedsReview: Boolean = false
)

class DiecastViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DiecastRepository

    init {
        val dao = AppDatabase.getDatabase(application).diecastDao()
        repository = DiecastRepository(dao)

        val prefs = application.getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE)
        val savedLang = prefs.getString("app_language", "EN") ?: "EN"
        com.example.util.LocaleHelper.setLanguage(savedLang)

        // Seed 30 realistic sample diecast cars with authentic images if empty or upgrading to v2
        val sampleDataVersion = prefs.getInt("sample_data_version", 0)
        viewModelScope.launch {
            if (repository.getCarCount() == 0 || sampleDataVersion < 2) {
                repository.deleteAllCars()
                repository.insertCars(com.example.data.SampleDiecastData.getSampleCars())
                prefs.edit().putInt("sample_data_version", 2).apply()
            }
        }
    }

    val selectedTab = MutableStateFlow(CollectionTab.COLLECTION)
    val isGridView = MutableStateFlow(true)
    val sortOption = MutableStateFlow(SortOption.NEWEST)
    val filterState = MutableStateFlow(FilterState())

    // Selected car for details or editing
    val selectedCarForDetail = MutableStateFlow<DiecastCar?>(null)
    val carToEdit = MutableStateFlow<DiecastCar?>(null)
    val showAddEditDialog = MutableStateFlow(false)
    val showBarcodeScanner = MutableStateFlow(false)
    var pendingBarcodeCallback: ((String) -> Unit)? = null

    fun openBarcodeScanner(onScanned: ((String) -> Unit)? = null) {
        pendingBarcodeCallback = onScanned
        showBarcodeScanner.value = true
    }

    fun handleBarcodeScanned(scannedCode: String) {
        showBarcodeScanner.value = false
        val cb = pendingBarcodeCallback
        if (cb != null) {
            cb.invoke(scannedCode)
            pendingBarcodeCallback = null
        } else {
            setSearchQuery(scannedCode)
        }
    }

    // BillingManager & Subscription / 10-Day Trial Pro
    val billingManager by lazy {
        com.example.util.BillingManager(getApplication<Application>(), viewModelScope)
    }

    // Google Play Integrity API Manager
    val playIntegrityManager by lazy {
        com.example.util.PlayIntegrityManager.getInstance(getApplication<Application>())
    }
    val integrityState get() = playIntegrityManager.integrityState

    fun refreshPlayIntegrity() {
        viewModelScope.launch {
            playIntegrityManager.requestIntegrityToken()
        }
    }

    val showSettingsDialog = MutableStateFlow(false)
    val showPaywallDialog = MutableStateFlow(false)
    val showDiagnosticsDialog = MutableStateFlow(false)
    val showPrivacyPolicyDialog = MutableStateFlow(false)
    val showManufacturerPriceSyncDialog = MutableStateFlow(false)

    val isCloudProSubscriber: StateFlow<Boolean> get() = billingManager.isProUnlocked
    val trialDaysRemaining: StateFlow<Int> get() = billingManager.trialDaysRemaining
    val isTrialActive: StateFlow<Boolean> get() = billingManager.isTrialActive
    val isTrialExpired: StateFlow<Boolean> get() = billingManager.isTrialExpired
    val formattedPrice: StateFlow<String> get() = billingManager.formattedPrice
    val billingFeedback: StateFlow<com.example.util.BillingFeedback?> get() = billingManager.billingFeedback
    val billingStatusMessage: StateFlow<String?> get() = billingManager.billingStatusMessage
    val isBillingConnecting: StateFlow<Boolean> get() = billingManager.isConnecting

    val lastPriceSyncDate = MutableStateFlow(
        getApplication<Application>()
            .getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE)
            .getString("last_price_sync_date", null)
    )

    fun refreshTrialStatus() {
        billingManager.refreshLicenseState()
    }

    fun clearBillingFeedback() {
        billingManager.clearFeedback()
    }

    fun restorePurchases(onComplete: ((Boolean) -> Unit)? = null) {
        billingManager.restorePurchases(onComplete)
    }

    val lastSyncTime = MutableStateFlow<String?>("Aktif")
    val isEngineSoundEnabled = MutableStateFlow(true)
    val appLanguage = com.example.util.LocaleHelper.appLanguage

    // Currency Code (Default: TRL, options: TRL, USD, EUR)
    val currencyCode = MutableStateFlow(
        getApplication<Application>()
            .getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE)
            .getString("currency_code", "TRL") ?: "TRL"
    )

    fun selectCurrency(code: String) {
        val oldCode = currencyCode.value
        if (oldCode == code) return
        currencyCode.value = code
        getApplication<Application>()
            .getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE)
            .edit()
            .putString("currency_code", code)
            .apply()

        viewModelScope.launch {
            val oldRateInTry = when (oldCode.uppercase()) {
                "USD" -> 36.5
                "EUR" -> 38.2
                else -> 1.0
            }
            val newRateInTry = when (code.uppercase()) {
                "USD" -> 36.5
                "EUR" -> 38.2
                else -> 1.0
            }
            val ratio = oldRateInTry / newRateInTry
            val currentCars = repository.allCars.first()
            val updatedCars = currentCars.map { car ->
                val newPurchase = if (car.purchasePrice > 0) {
                    if (code == "TRL") {
                        Math.round(car.purchasePrice * ratio / 5.0) * 5.0
                    } else {
                        Math.round(car.purchasePrice * ratio * 100.0) / 100.0
                    }
                } else 0.0

                val newEstimated = if (car.estimatedValue > 0) {
                    if (code == "TRL") {
                        Math.round(car.estimatedValue * ratio / 5.0) * 5.0
                    } else {
                        Math.round(car.estimatedValue * ratio * 10.0) / 10.0
                    }
                } else 0.0

                car.copy(
                    purchasePrice = newPurchase,
                    estimatedValue = newEstimated
                )
            }
            repository.insertCars(updatedCars)
            selectedCarForDetail.value?.let { currentDetail ->
                val match = updatedCars.find { it.id == currentDetail.id }
                if (match != null) {
                    selectedCarForDetail.value = match
                }
            }
        }
    }

    // Theme Mode (Default: Dark Mode)
    val isDarkMode = MutableStateFlow(
        getApplication<Application>()
            .getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE)
            .getBoolean("is_dark_mode", true)
    )

    fun toggleDarkMode(isDark: Boolean) {
        isDarkMode.value = isDark
        getApplication<Application>()
            .getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE)
            .edit()
            .putBoolean("is_dark_mode", isDark)
            .apply()
    }

    fun openSettings() { showSettingsDialog.value = true }
    fun closeSettings() { showSettingsDialog.value = false }
    fun openPaywall() { showPaywallDialog.value = true }
    fun closePaywall() { showPaywallDialog.value = false }
    fun openDiagnostics() { showDiagnosticsDialog.value = true }
    fun closeDiagnostics() { showDiagnosticsDialog.value = false }
    fun openPrivacyPolicy() { showPrivacyPolicyDialog.value = true }
    fun closePrivacyPolicy() { showPrivacyPolicyDialog.value = false }
    fun openManufacturerPriceSync() { showManufacturerPriceSyncDialog.value = true }
    fun closeManufacturerPriceSync() { showManufacturerPriceSyncDialog.value = false }

    fun applyManufacturerPriceUpdates(updatedCars: List<DiecastCar>) {
        viewModelScope.launch {
            repository.insertCars(updatedCars)
            val currentDateStr = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
            lastPriceSyncDate.value = currentDateStr
            getApplication<Application>()
                .getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE)
                .edit()
                .putString("last_price_sync_date", currentDateStr)
                .apply()
            
            // If currently selected car was updated, refresh it
            val currentSelected = selectedCarForDetail.value
            if (currentSelected != null) {
                val updatedMatch = updatedCars.find { it.id == currentSelected.id }
                if (updatedMatch != null) {
                    selectedCarForDetail.value = updatedMatch
                }
            }
        }
    }

    fun fetchAndUpdateSingleCarPrice(car: DiecastCar) {
        viewModelScope.launch {
            val estimate = com.example.util.ManufacturerPriceService.estimateCarPrice(car, currencyCode.value)
            val updated = car.copy(estimatedValue = estimate.estimatedValue)
            repository.updateCar(updated)
            if (selectedCarForDetail.value?.id == car.id) {
                selectedCarForDetail.value = updated
            }
        }
    }

    fun getEstimatedPrice(car: DiecastCar): com.example.util.PriceEstimateResult {
        return com.example.util.ManufacturerPriceService.estimateCarPrice(car, currencyCode.value)
    }

    fun exportCollectionCsv(context: android.content.Context) {
        viewModelScope.launch {
            val cars = repository.allCars.first()
            com.example.util.CsvExporter.exportAndShareCsv(
                context = context,
                cars = cars,
                currencyCode = currencyCode.value
            )
        }
    }

    fun exportCollectionJson(context: android.content.Context) {
        viewModelScope.launch {
            val cars = repository.allCars.first()
            com.example.util.JsonWebExporter.exportAndShareJson(
                context = context,
                cars = cars,
                currencyCode = currencyCode.value
            )
        }
    }

    fun importCarsFromJsonString(jsonString: String, onResult: (Int) -> Unit) {
        viewModelScope.launch {
            val cars = com.example.util.JsonWebExporter.parseCarsFromJson(jsonString)
            if (cars.isNotEmpty()) {
                repository.insertCars(cars)
            }
            onResult(cars.size)
        }
    }

    fun selectRandomCar() {
        val currentList = filteredCars.value
        if (currentList.isNotEmpty()) {
            selectedCarForDetail.value = currentList.random()
        }
    }

    fun toggleEngineSound(enabled: Boolean) {
        isEngineSoundEnabled.value = enabled
    }

    fun setLanguage(lang: String) {
        com.example.util.LocaleHelper.setLanguage(lang)
        getApplication<Application>()
            .getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE)
            .edit()
            .putString("app_language", lang)
            .apply()
    }

    fun subscribeToCloudPro(activity: android.app.Activity? = null) {
        if (activity != null) {
            billingManager.launchBillingFlow(activity)
        } else {
            billingManager.unlockProLocally()
        }
        showPaywallDialog.value = false
        val currentTime = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        lastSyncTime.value = currentTime
    }

    fun cancelCloudSubscription() {
        billingManager.revokeProLocally()
        lastSyncTime.value = null
    }

    fun resetTrialForTesting() {
        billingManager.resetTrialForTesting()
        lastSyncTime.value = null
    }

    fun expireTrialForTesting() {
        billingManager.expireTrialForTesting()
        lastSyncTime.value = null
    }

    override fun onCleared() {
        super.onCleared()
        billingManager.endConnection()
    }

    fun openNeedsReviewFilter() {
        filterState.value = filterState.value.copy(onlyNeedsReview = true)
        selectedTab.value = CollectionTab.COLLECTION
        showSettingsDialog.value = false
    }

    val allCarsFlow = repository.allCars

    val filteredCars: StateFlow<List<DiecastCar>> = combine(
        allCarsFlow,
        selectedTab,
        filterState,
        sortOption
    ) { allCars, tab, filter, sort ->
        var list = when (tab) {
            CollectionTab.COLLECTION -> allCars.filter { !it.isWishlist }
            CollectionTab.WISHLIST -> allCars.filter { it.isWishlist }
            CollectionTab.STATS -> allCars.filter { !it.isWishlist }
        }

        // Apply Search Query
        if (filter.searchQuery.isNotBlank()) {
            val q = filter.searchQuery.trim().lowercase()
            list = list.filter { car ->
                car.manufacturer.lowercase().contains(q) ||
                car.carBrand.lowercase().contains(q) ||
                car.model.lowercase().contains(q) ||
                car.series.lowercase().contains(q) ||
                car.color.lowercase().contains(q) ||
                car.barcode.lowercase().contains(q) ||
                car.condition.lowercase().contains(q) ||
                car.notes.lowercase().contains(q) ||
                car.tags.lowercase().contains(q)
            }
        }

        // Apply Filters
        if (filter.onlyFavorites) {
            list = list.filter { it.isFavorite }
        }
        if (filter.onlyNeedsReview) {
            list = list.filter { com.example.util.ModelValidator.validate(it).needsReview }
        }
        if (filter.selectedTags.isNotEmpty()) {
            list = list.filter { car ->
                val carTagList = car.getTagList()
                filter.selectedTags.any { selTag ->
                    carTagList.any { it.equals(selTag, ignoreCase = true) }
                }
            }
        }
        if (!filter.selectedTag.isNullOrBlank()) {
            list = list.filter { it.hasTag(filter.selectedTag) }
        }
        if (!filter.manufacturer.isNullOrBlank()) {
            list = list.filter { it.manufacturer.equals(filter.manufacturer, ignoreCase = true) }
        }
        if (!filter.carBrand.isNullOrBlank()) {
            list = list.filter { it.carBrand.equals(filter.carBrand, ignoreCase = true) }
        }
        if (!filter.scale.isNullOrBlank()) {
            list = list.filter { it.scale.equals(filter.scale, ignoreCase = true) }
        }
        if (!filter.condition.isNullOrBlank()) {
            list = list.filter { it.condition.equals(filter.condition, ignoreCase = true) }
        }
        if (!filter.color.isNullOrBlank()) {
            list = list.filter { it.color.equals(filter.color, ignoreCase = true) }
        }
        if (!filter.series.isNullOrBlank()) {
            list = list.filter { it.series.equals(filter.series, ignoreCase = true) }
        }

        // Apply Sorting
        when (sort) {
            SortOption.NEWEST -> list.sortedByDescending { it.createdAt }
            SortOption.OLDEST -> list.sortedBy { it.createdAt }
            SortOption.PRICE_HIGH -> list.sortedByDescending { it.purchasePrice }
            SortOption.PRICE_LOW -> list.sortedBy { it.purchasePrice }
            SortOption.VALUE_HIGH -> list.sortedByDescending { it.estimatedValue }
            SortOption.BRAND_AZ -> list.sortedBy { "${it.carBrand} ${it.model}" }
            SortOption.MODEL_YEAR_NEWEST -> list.sortedByDescending { it.modelYear }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Helper data sets for filter chips
    val availableManufacturers: StateFlow<List<String>> = allCarsFlow.combine(filterState) { cars, _ ->
        cars.map { it.manufacturer }.filter { it.isNotBlank() }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val availableCarBrands: StateFlow<List<String>> = allCarsFlow.combine(filterState) { cars, _ ->
        cars.map { it.carBrand }.filter { it.isNotBlank() }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val availableScales: StateFlow<List<String>> = allCarsFlow.combine(filterState) { cars, _ ->
        cars.map { it.scale }.filter { it.isNotBlank() }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val availableConditions: StateFlow<List<String>> = allCarsFlow.combine(filterState) { cars, _ ->
        cars.map { it.condition }.filter { it.isNotBlank() }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val availableTags: StateFlow<List<String>> = allCarsFlow.combine(filterState) { cars, _ ->
        val preset = listOf(
            "Spor", "Klasik", "Yarış", "Supercar", "JDM", "Muscle", 
            "Off-Road", "Custom", "Film / Dizi", "Polis / Acil", "Konsept", "Elektrikli"
        )
        val fromCars = cars.flatMap { it.getTagList() }
        (preset + fromCars).filter { it.isNotBlank() }.distinct()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // CRUD Actions
    fun saveCar(car: DiecastCar) {
        viewModelScope.launch {
            val isNewCar = (car.id == 0)
            if (isNewCar) {
                repository.insertCar(car)
                if (isEngineSoundEnabled.value) {
                    com.example.util.EngineSoundPlayer.playEngineRevSound()
                }
            } else {
                repository.updateCar(car)
                if (selectedCarForDetail.value?.id == car.id) {
                    selectedCarForDetail.value = car
                }
            }
            closeAddEditDialog()
        }
    }

    fun deleteCar(car: DiecastCar) {
        viewModelScope.launch {
            repository.deleteCar(car)
            if (selectedCarForDetail.value?.id == car.id) {
                selectedCarForDetail.value = null
            }
        }
    }

    fun toggleWishlistStatus(car: DiecastCar) {
        viewModelScope.launch {
            val updated = car.copy(isWishlist = !car.isWishlist)
            repository.updateCar(updated)
            if (selectedCarForDetail.value?.id == car.id) {
                selectedCarForDetail.value = updated
            }
        }
    }

    fun toggleFavoriteStatus(car: DiecastCar) {
        viewModelScope.launch {
            val updated = car.copy(isFavorite = !car.isFavorite)
            repository.updateCar(updated)
            if (selectedCarForDetail.value?.id == car.id) {
                selectedCarForDetail.value = updated
            }
        }
    }

    fun openAddCarDialog(isWishlist: Boolean = false) {
        carToEdit.value = DiecastCar(
            manufacturer = "Hot Wheels",
            carBrand = "Porsche",
            model = "",
            isWishlist = isWishlist
        )
        showAddEditDialog.value = true
    }

    fun openEditCarDialog(car: DiecastCar) {
        carToEdit.value = car
        showAddEditDialog.value = true
    }

    fun closeAddEditDialog() {
        showAddEditDialog.value = false
        carToEdit.value = null
    }

    fun setSearchQuery(query: String) {
        filterState.value = filterState.value.copy(searchQuery = query)
    }

    fun setFilter(
        manufacturer: String? = filterState.value.manufacturer,
        carBrand: String? = filterState.value.carBrand,
        scale: String? = filterState.value.scale,
        condition: String? = filterState.value.condition,
        color: String? = filterState.value.color,
        series: String? = filterState.value.series
    ) {
        filterState.value = filterState.value.copy(
            manufacturer = manufacturer,
            carBrand = carBrand,
            scale = scale,
            condition = condition,
            color = color,
            series = series
        )
    }

    fun clearFilters() {
        filterState.value = FilterState()
    }

    fun toggleViewMode() {
        isGridView.value = !isGridView.value
    }

    fun deleteAllCars() {
        viewModelScope.launch {
            repository.deleteAllCars()
            selectedCarForDetail.value = null
            carToEdit.value = null
        }
    }

    fun importCars(cars: List<DiecastCar>, onComplete: ((Int) -> Unit)? = null) {
        viewModelScope.launch {
            repository.insertCars(cars)
            onComplete?.invoke(cars.size)
        }
    }

    fun loadSampleCars() {
        viewModelScope.launch {
            repository.deleteAllCars()
            repository.insertCars(com.example.data.SampleDiecastData.getSampleCars())
            val prefs = getApplication<Application>().getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE)
            prefs.edit().putInt("sample_data_version", 2).apply()
            selectedCarForDetail.value = null
            carToEdit.value = null
        }
    }
}
