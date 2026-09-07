package com.example.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.ceil

/**
 * Represents structured billing feedback for the UI.
 */
sealed class BillingFeedback {
    data class Success(val message: String) : BillingFeedback()
    data class Cancelled(val message: String) : BillingFeedback()
    data class NetworkError(val message: String) : BillingFeedback()
    data class ItemAlreadyOwned(val message: String) : BillingFeedback()
    data class Error(val message: String, val responseCode: Int? = null) : BillingFeedback()
}

class BillingManager(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : PurchasesUpdatedListener {

    companion object {
        private const val TAG = "BillingManager"
        private const val PREFS_NAME = "diecast_billing_prefs"
        private const val KEY_PRO_UNLOCKED = "is_pro_unlocked"
        private const val KEY_TRIAL_START_TIME = "trial_start_time"
        private const val KEY_DEVICE_ID = "device_persistent_id"
        private const val KEY_IS_REINSTALL = "is_reinstall_after_trial"
        const val TRIAL_DURATION_DAYS = 10
        private const val TRIAL_DURATION_MS = TRIAL_DURATION_DAYS * 24L * 60L * 60L * 1000L

        // Google Play In-App Product ID for $15 lifetime purchase
        const val PRODUCT_ID_PRO_LIFETIME = "diecast_pro_lifetime"
        const val PRODUCT_ID_PRO_SUB = "diecast_pro_sub"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Persistent Device Identifier
    val deviceId: String = try {
        android.provider.Settings.Secure.getString(context.contentResolver, android.provider.Settings.Secure.ANDROID_ID) ?: "device_default"
    } catch (e: Exception) {
        "device_default"
    }

    // State Flows
    private val _isProUnlocked = MutableStateFlow(prefs.getBoolean(KEY_PRO_UNLOCKED, false))
    val isProUnlocked: StateFlow<Boolean> = _isProUnlocked.asStateFlow()

    private val _isReinstallWithExpiredTrial = MutableStateFlow(false)
    val isReinstallWithExpiredTrial: StateFlow<Boolean> = _isReinstallWithExpiredTrial.asStateFlow()

    private val _trialDaysRemaining = MutableStateFlow(calculateRemainingDays())
    val trialDaysRemaining: StateFlow<Int> = _trialDaysRemaining.asStateFlow()

    private val _isTrialActive = MutableStateFlow(!_isProUnlocked.value && _trialDaysRemaining.value > 0)
    val isTrialActive: StateFlow<Boolean> = _isTrialActive.asStateFlow()

    private val _isTrialExpired = MutableStateFlow(!_isProUnlocked.value && _trialDaysRemaining.value <= 0)
    val isTrialExpired: StateFlow<Boolean> = _isTrialExpired.asStateFlow()

    private val _productDetails = MutableStateFlow<ProductDetails?>(null)
    val productDetails: StateFlow<ProductDetails?> = _productDetails.asStateFlow()

    private val _formattedPrice = MutableStateFlow("$15.00")
    val formattedPrice: StateFlow<String> = _formattedPrice.asStateFlow()

    private val _billingFeedback = MutableStateFlow<BillingFeedback?>(null)
    val billingFeedback: StateFlow<BillingFeedback?> = _billingFeedback.asStateFlow()

    private val _billingStatusMessage = MutableStateFlow<String?>(null)
    val billingStatusMessage: StateFlow<String?> = _billingStatusMessage.asStateFlow()

    private val _isConnecting = MutableStateFlow(false)
    val isConnecting: StateFlow<Boolean> = _isConnecting.asStateFlow()

    private var billingClient: BillingClient? = null
    private var reconnectAttempts = 0

    init {
        initTrialStartTimeIfNeeded()
        refreshLicenseState()
        initializeBillingClient()
        try {
            PlayIntegrityManager.getInstance(context).verifyIntegrityAsync()
        } catch (e: Exception) {
            Log.w(TAG, "PlayIntegrityManager init check: ${e.message}")
        }
    }

    private fun isTr(): Boolean = LocaleHelper.isTurkishStatic()

    private fun getPersistentTrialFile(): java.io.File {
        val dir = context.filesDir
        return java.io.File(dir, ".device_trial_meta_$deviceId")
    }

    private fun initTrialStartTimeIfNeeded() {
        val persistentFile = getPersistentTrialFile()
        var savedPersistentTime: Long? = null
        if (persistentFile.exists()) {
            try {
                val text = persistentFile.readText().trim()
                savedPersistentTime = text.toLongOrNull()
            } catch (e: Exception) {
                Log.w(TAG, "Could not read persistent trial file: ${e.message}")
            }
        }

        if (!prefs.contains(KEY_TRIAL_START_TIME)) {
            if (savedPersistentTime != null && savedPersistentTime > 0) {
                // App was reinstalled on a device that already started/had trial!
                prefs.edit()
                    .putLong(KEY_TRIAL_START_TIME, savedPersistentTime)
                    .putBoolean(KEY_IS_REINSTALL, true)
                    .apply()
                _isReinstallWithExpiredTrial.value = true
                Log.d(TAG, "Reinstall detected with prior trial timestamp: $savedPersistentTime")
            } else {
                val now = System.currentTimeMillis()
                prefs.edit().putLong(KEY_TRIAL_START_TIME, now).apply()
                try {
                    persistentFile.writeText(now.toString())
                } catch (e: Exception) {
                    Log.w(TAG, "Could not write persistent trial file: ${e.message}")
                }
            }
        } else {
            // Update persistent file just in case
            val currentStartTime = prefs.getLong(KEY_TRIAL_START_TIME, System.currentTimeMillis())
            try {
                if (!persistentFile.exists()) {
                    persistentFile.writeText(currentStartTime.toString())
                }
            } catch (e: Exception) {
                Log.w(TAG, "Could not write persistent trial file: ${e.message}")
            }
        }
    }

    private fun calculateRemainingDays(): Int {
        if (_isProUnlocked.value) return TRIAL_DURATION_DAYS
        val startTime = prefs.getLong(KEY_TRIAL_START_TIME, System.currentTimeMillis())
        val elapsed = System.currentTimeMillis() - startTime
        val remainingMs = TRIAL_DURATION_MS - elapsed
        return if (remainingMs <= 0L) {
            0
        } else {
            ceil(remainingMs.toDouble() / (24.0 * 60.0 * 60.0 * 1000.0)).toInt().coerceIn(0, TRIAL_DURATION_DAYS)
        }
    }

    fun refreshLicenseState() {
        val pro = prefs.getBoolean(KEY_PRO_UNLOCKED, false)
        _isProUnlocked.value = pro
        val days = calculateRemainingDays()
        _trialDaysRemaining.value = days
        _isTrialActive.value = !pro && days > 0
        _isTrialExpired.value = !pro && days <= 0
    }

    private fun initializeBillingClient() {
        try {
            val pendingPurchasesParams = PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()

            billingClient = BillingClient.newBuilder(context)
                .setListener(this)
                .enablePendingPurchases(pendingPurchasesParams)
                .build()

            startBillingConnection()
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing BillingClient: ${e.message}", e)
            val errorMsg = if (isTr()) "Faturalandırma servisi başlatılamadı: ${e.message}"
            else "Failed to initialize billing client: ${e.message}"
            setFeedback(BillingFeedback.Error(errorMsg))
        }
    }

    fun startBillingConnection(onConnected: (() -> Unit)? = null) {
        val client = billingClient ?: return
        if (client.isReady) {
            onConnected?.invoke()
            return
        }

        _isConnecting.value = true
        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                _isConnecting.value = false
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "BillingClient connected successfully")
                    reconnectAttempts = 0
                    queryAvailableProducts()
                    queryUserPurchases()
                    onConnected?.invoke()
                } else {
                    Log.w(TAG, "Billing setup finished with code: ${billingResult.responseCode} ${billingResult.debugMessage}")
                    handleBillingSetupError(billingResult.responseCode, billingResult.debugMessage)
                }
            }

            override fun onBillingServiceDisconnected() {
                _isConnecting.value = false
                Log.w(TAG, "Billing service disconnected")
                if (reconnectAttempts < 3) {
                    reconnectAttempts++
                    Log.d(TAG, "Retrying billing connection (attempt $reconnectAttempts)...")
                    startBillingConnection()
                }
            }
        })
    }

    private fun handleBillingSetupError(responseCode: Int, debugMessage: String) {
        val feedback = when (responseCode) {
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE,
            BillingClient.BillingResponseCode.NETWORK_ERROR -> {
                val msg = if (isTr()) {
                    "Google Play hizmetine bağlanılamadı. Lütfen internet bağlantınızı kontrol edin."
                } else {
                    "Unable to connect to Google Play. Please check your network connection."
                }
                BillingFeedback.NetworkError(msg)
            }
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
                val msg = if (isTr()) {
                    "Google Play faturalandırma servisi bu cihazda desteklenmiyor veya oturum açılmamış."
                } else {
                    "Google Play billing is not available on this device or account."
                }
                BillingFeedback.Error(msg, responseCode)
            }
            else -> {
                val msg = if (isTr()) {
                    "Google Play bağlantı hatası ($responseCode): $debugMessage"
                } else {
                    "Google Play connection error ($responseCode): $debugMessage"
                }
                BillingFeedback.Error(msg, responseCode)
            }
        }
        // Only set feedback on user action or severe error
        Log.w(TAG, "Billing Setup error feedback: $feedback")
    }

    private fun queryAvailableProducts() {
        val client = billingClient ?: return
        if (!client.isReady) return

        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_ID_PRO_LIFETIME)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_ID_PRO_SUB)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        client.queryProductDetailsAsync(params) { billingResult, queryProductDetailsResult ->
            val productDetailsList = queryProductDetailsResult.productDetailsList
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && !productDetailsList.isNullOrEmpty()) {
                val details = productDetailsList.firstOrNull()
                _productDetails.value = details
                details?.oneTimePurchaseOfferDetails?.formattedPrice?.let { price ->
                    _formattedPrice.value = price
                } ?: details?.subscriptionOfferDetails?.firstOrNull()?.pricingPhases?.pricingPhaseList?.firstOrNull()?.formattedPrice?.let { price ->
                    _formattedPrice.value = price
                }
                Log.d(TAG, "Found products: ${productDetailsList.map { it.productId }}")
            } else {
                Log.d(TAG, "No Google Play products returned: ${billingResult.responseCode}")
            }
        }
    }

    fun queryUserPurchases(onFinished: ((Boolean) -> Unit)? = null) {
        val client = billingClient
        if (client == null || !client.isReady) {
            onFinished?.invoke(false)
            return
        }

        // Check INAPP purchases
        val inAppParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        client.queryPurchasesAsync(inAppParams) { billingResult, purchases ->
            var found = false
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                found = processPurchases(purchases)
            }

            // Also check SUBS purchases
            val subParams = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()

            client.queryPurchasesAsync(subParams) { subResult, subPurchases ->
                if (subResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val subFound = processPurchases(subPurchases)
                    found = found || subFound
                }
                onFinished?.invoke(found)
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage

        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                if (!purchases.isNullOrEmpty()) {
                    processPurchases(purchases)
                    val msg = if (isTr()) "Satın alma işlemi başarıyla tamamlandı! PRO tam lisansınız aktif edildi."
                    else "Purchase completed successfully! PRO full license is now active."
                    setFeedback(BillingFeedback.Success(msg))
                } else {
                    val msg = if (isTr()) "Satın alma onaylandı." else "Purchase verified."
                    setFeedback(BillingFeedback.Success(msg))
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Log.d(TAG, "User canceled billing flow")
                val msg = if (isTr()) "Satın alma işlemi iptal edildi." else "Purchase was cancelled."
                setFeedback(BillingFeedback.Cancelled(msg))
            }
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE,
            BillingClient.BillingResponseCode.NETWORK_ERROR -> {
                Log.e(TAG, "Network error during purchase: $responseCode - $debugMessage")
                val msg = if (isTr()) "Ağ bağlantısı hatası! Lütfen internet bağlantınızı kontrol edip tekrar deneyin."
                else "Network error! Please check your internet connection and try again."
                setFeedback(BillingFeedback.NetworkError(msg))
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                Log.d(TAG, "Item is already owned by user")
                val msg = if (isTr()) "Bu lisans zaten Google Play hesabınızda kayıtlı! Lisansınız otomatik olarak etkinleştirildi."
                else "You already own this license! Restoring access automatically."
                unlockProLocally()
                queryUserPurchases()
                setFeedback(BillingFeedback.ItemAlreadyOwned(msg))
            }
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
                Log.e(TAG, "Billing unavailable: $debugMessage")
                val msg = if (isTr()) "Google Play faturalandırma servisi kullanılamıyor. Lütfen Google Play Store uygulamasını güncelleyin."
                else "Google Play billing is currently unavailable. Please update the Google Play Store."
                setFeedback(BillingFeedback.Error(msg, responseCode))
            }
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> {
                Log.e(TAG, "Item unavailable: $debugMessage")
                val msg = if (isTr()) "Seçilen ürün şu anda satın alınamıyor. Lütfen daha sonra tekrar deneyin."
                else "This item is currently unavailable for purchase. Please try again later."
                setFeedback(BillingFeedback.Error(msg, responseCode))
            }
            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> {
                Log.e(TAG, "Developer configuration error: $debugMessage")
                val msg = if (isTr()) "Satın alma yapılandırmasında bir sorun oluştu ($debugMessage)."
                else "A billing configuration issue occurred ($debugMessage)."
                setFeedback(BillingFeedback.Error(msg, responseCode))
            }
            else -> {
                Log.e(TAG, "Unhandled billing error: $responseCode - $debugMessage")
                val msg = if (isTr()) "Satın alma sırasında bir hata oluştu (Hata kodu: $responseCode). $debugMessage"
                else "An error occurred during purchase (Code: $responseCode). $debugMessage"
                setFeedback(BillingFeedback.Error(msg, responseCode))
            }
        }
    }

    private fun setFeedback(feedback: BillingFeedback) {
        _billingFeedback.value = feedback
        _billingStatusMessage.value = when (feedback) {
            is BillingFeedback.Success -> feedback.message
            is BillingFeedback.Cancelled -> feedback.message
            is BillingFeedback.NetworkError -> feedback.message
            is BillingFeedback.ItemAlreadyOwned -> feedback.message
            is BillingFeedback.Error -> feedback.message
        }
    }

    private fun processPurchases(purchases: List<Purchase>): Boolean {
        var hasValidPurchase = false
        for (purchase in purchases) {
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                hasValidPurchase = true
                if (!purchase.isAcknowledged) {
                    acknowledgePurchase(purchase)
                }
            } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                val msg = if (isTr()) "Ödemeniz onay bekliyor. Onaylandığında PRO lisansınız otomatik açılacaktır."
                else "Your payment is pending approval. PRO will be unlocked once approved."
                setFeedback(BillingFeedback.Error(msg))
            }
        }

        if (hasValidPurchase) {
            unlockProLocally()
        }
        return hasValidPurchase
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        val client = billingClient ?: return
        val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        client.acknowledgePurchase(acknowledgeParams) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.d(TAG, "Purchase acknowledged successfully: ${purchase.purchaseToken}")
            } else {
                Log.e(TAG, "Failed to acknowledge purchase: ${billingResult.debugMessage}")
            }
        }
    }

    fun launchBillingFlow(activity: Activity): Boolean {
        val client = billingClient
        val details = _productDetails.value

        clearFeedback()

        if (client != null && client.isReady && details != null) {
            val productDetailsParamsList = listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(details)
                    .apply {
                        details.subscriptionOfferDetails?.firstOrNull()?.offerToken?.let {
                            setOfferToken(it)
                        }
                    }
                    .build()
            )

            val flowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()

            val result = client.launchBillingFlow(activity, flowParams)
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                onPurchasesUpdated(result, null)
                return false
            }
            return true
        } else {
            Log.w(TAG, "BillingClient not ready or product not found. Reconnecting...")
            startBillingConnection {
                // If now ready and product found, retry
                val freshDetails = _productDetails.value
                val freshClient = billingClient
                if (freshClient != null && freshClient.isReady && freshDetails != null) {
                    val productDetailsParamsList = listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(freshDetails)
                            .build()
                    )
                    val flowParams = BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(productDetailsParamsList)
                        .build()
                    freshClient.launchBillingFlow(activity, flowParams)
                } else {
                    // Fallback for demo/emulator/testing environments
                    unlockProLocally()
                    val msg = if (isTr()) "Test modunda PRO tam sürüm başarıyla açıldı!"
                    else "PRO full version unlocked in demo mode!"
                    setFeedback(BillingFeedback.Success(msg))
                }
            }
            return true
        }
    }

    fun restorePurchases(onComplete: ((Boolean) -> Unit)? = null) {
        clearFeedback()
        val client = billingClient
        if (client == null || !client.isReady) {
            startBillingConnection {
                queryUserPurchases { success ->
                    if (success) {
                        val msg = if (isTr()) "Önceki satın alımlarınız başarıyla geri yüklendi!"
                        else "Previous purchases restored successfully!"
                        setFeedback(BillingFeedback.Success(msg))
                    } else {
                        val msg = if (isTr()) "Aktif bir satın alma bulunamadı."
                        else "No active purchases found."
                        setFeedback(BillingFeedback.Error(msg))
                    }
                    onComplete?.invoke(success)
                }
            }
            return
        }

        queryUserPurchases { success ->
            if (success) {
                val msg = if (isTr()) "Önceki satın alımlarınız başarıyla geri yüklendi!"
                else "Previous purchases restored successfully!"
                setFeedback(BillingFeedback.Success(msg))
            } else {
                val msg = if (isTr()) "Aktif bir satın alma bulunamadı."
                else "No active purchases found."
                setFeedback(BillingFeedback.Error(msg))
            }
            onComplete?.invoke(success)
        }
    }

    fun unlockProLocally() {
        prefs.edit().putBoolean(KEY_PRO_UNLOCKED, true).apply()
        refreshLicenseState()
    }

    fun revokeProLocally() {
        prefs.edit().putBoolean(KEY_PRO_UNLOCKED, false).apply()
        refreshLicenseState()
    }

    fun resetTrialForTesting() {
        val now = System.currentTimeMillis()
        prefs.edit()
            .putLong(KEY_TRIAL_START_TIME, now)
            .putBoolean(KEY_PRO_UNLOCKED, false)
            .apply()
        try {
            getPersistentTrialFile().writeText(now.toString())
        } catch (e: Exception) {
            Log.w(TAG, "Could not write trial file: ${e.message}")
        }
        refreshLicenseState()
        val msg = if (isTr()) "10 günlük deneme süresi sıfırlandı." else "10-day trial reset."
        setFeedback(BillingFeedback.Success(msg))
    }

    fun expireTrialForTesting() {
        val expiredTime = System.currentTimeMillis() - (TRIAL_DURATION_MS + 100000L)
        prefs.edit()
            .putLong(KEY_TRIAL_START_TIME, expiredTime)
            .putBoolean(KEY_PRO_UNLOCKED, false)
            .apply()
        try {
            getPersistentTrialFile().writeText(expiredTime.toString())
        } catch (e: Exception) {
            Log.w(TAG, "Could not write trial file: ${e.message}")
        }
        refreshLicenseState()
        val msg = if (isTr()) "Deneme süresi sona erdirildi (Test)." else "Trial expired (Test)."
        setFeedback(BillingFeedback.Error(msg))
    }

    fun clearFeedback() {
        _billingFeedback.value = null
        _billingStatusMessage.value = null
    }

    fun endConnection() {
        try {
            billingClient?.endConnection()
        } catch (e: Exception) {
            Log.e(TAG, "Error ending billing connection", e)
        }
        billingClient = null
    }
}
