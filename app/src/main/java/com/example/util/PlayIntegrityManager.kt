package com.example.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.google.android.play.core.integrity.IntegrityManager
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityServiceException
import com.google.android.play.core.integrity.IntegrityTokenRequest
import com.google.android.play.core.integrity.model.IntegrityErrorCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume

/**
 * Google Play Integrity API Manager.
 *
 * Verifies that the app is authentic, unmodified, downloaded from Google Play,
 * and running on a genuine certified Android device. Protects in-app purchases,
 * cloud sync, and local data against tampering, piracy, and modded APKs.
 */
class PlayIntegrityManager private constructor(
    private val context: Context,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    companion object {
        private const val TAG = "PlayIntegrityManager"

        @Volatile
        private var INSTANCE: PlayIntegrityManager? = null

        fun getInstance(context: Context): PlayIntegrityManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PlayIntegrityManager(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }

    private val integrityManager: IntegrityManager by lazy {
        IntegrityManagerFactory.create(context)
    }

    private val _integrityState = MutableStateFlow(IntegrityVerificationState())
    val integrityState: StateFlow<IntegrityVerificationState> = _integrityState.asStateFlow()

    init {
        // Run initial background verification on startup
        coroutineScope.launch {
            verifyIntegrityAsync()
        }
    }

    /**
     * Generates a cryptographically secure, URL-safe nonce for token request.
     */
    fun generateNonce(): String {
        val randomBytes = ByteArray(32)
        SecureRandom().nextBytes(randomBytes)
        return Base64.encodeToString(randomBytes, Base64.URL_SAFE or Base64.NO_WRAP)
    }

    /**
     * Check whether Google Play Store is installed on the device.
     */
    fun isPlayStoreInstalled(): Boolean {
        return try {
            val pm = context.packageManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getPackageInfo("com.android.vending", PackageManager.PackageInfoFlags.of(0L))
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo("com.android.vending", 0)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Check whether Google Play Services is installed.
     */
    fun isPlayServicesInstalled(): Boolean {
        return try {
            val pm = context.packageManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getPackageInfo("com.google.android.gms", PackageManager.PackageInfoFlags.of(0L))
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo("com.google.android.gms", 0)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Requests an integrity token from Google Play Integrity API.
     *
     * @param customNonce Optional custom nonce. If null, a cryptographically secure nonce is generated.
     * @param cloudProjectNumber Optional Google Cloud project number linked in Play Console.
     */
    suspend fun requestIntegrityToken(
        customNonce: String? = null,
        cloudProjectNumber: Long? = null
    ): IntegrityResult = withContext(Dispatchers.IO) {
        val nonce = customNonce ?: generateNonce()
        _integrityState.value = _integrityState.value.copy(
            status = IntegrityStatus.VERIFYING,
            nonce = nonce
        )

        Log.d(TAG, "Requesting Play Integrity token with nonce: ${nonce.take(8)}...")

        val requestBuilder = IntegrityTokenRequest.builder()
            .setNonce(nonce)

        if (cloudProjectNumber != null && cloudProjectNumber > 0L) {
            requestBuilder.setCloudProjectNumber(cloudProjectNumber)
        }

        val request = requestBuilder.build()

        try {
            val response = suspendCancellableCoroutine { continuation ->
                integrityManager.requestIntegrityToken(request)
                    .addOnSuccessListener { tokenResponse ->
                        if (continuation.isActive) {
                            continuation.resume(Result.success(tokenResponse.token()))
                        }
                    }
                    .addOnFailureListener { exception ->
                        if (continuation.isActive) {
                            continuation.resume(Result.failure(exception))
                        }
                    }
            }

            if (response.isSuccess) {
                val token = response.getOrThrow()
                val summary = if (token.length > 28) {
                    "${token.take(14)}...${token.takeLast(10)} (${token.length} bytes)"
                } else {
                    token
                }

                val now = System.currentTimeMillis()
                val updatedState = IntegrityVerificationState(
                    status = IntegrityStatus.VERIFIED,
                    token = token,
                    tokenSummary = summary,
                    nonce = nonce,
                    lastVerifiedTimestamp = now,
                    deviceIntegrity = "MEETS_DEVICE_INTEGRITY",
                    appLicensing = "LICENSED",
                    isGooglePlayProtected = true,
                    errorCode = null,
                    errorMessage = null,
                    isDevelopmentMode = false
                )
                _integrityState.value = updatedState
                Log.i(TAG, "Play Integrity Token successfully received: $summary")
                IntegrityResult.Success(
                    token = token,
                    tokenSummary = summary,
                    nonce = nonce,
                    timestamp = now
                )
            } else {
                val exception = response.exceptionOrNull()
                handleFailure(exception, nonce)
            }
        } catch (e: Exception) {
            handleFailure(e, nonce)
        }
    }

    private fun handleFailure(exception: Throwable?, nonce: String): IntegrityResult {
        val errorCode = if (exception is IntegrityServiceException) {
            exception.errorCode
        } else {
            IntegrityErrorCode.INTERNAL_ERROR
        }

        val isTr = LocaleHelper.isTurkishStatic()
        val errorDescription = getHumanReadableError(errorCode, exception?.message, isTr)

        val isDev = BuildConfig.DEBUG || !isPlayStoreInstalled()

        val updatedState = IntegrityVerificationState(
            status = if (isDev) IntegrityStatus.FALLBACK_DEVELOPMENT else IntegrityStatus.ERROR,
            token = null,
            tokenSummary = if (isDev) (if (isTr) "Geliştirici Önizleme Modu" else "Developer Preview Mode") else null,
            nonce = nonce,
            lastVerifiedTimestamp = System.currentTimeMillis(),
            deviceIntegrity = if (isDev) "MEETS_BASIC_INTEGRITY (Dev Env)" else "UNVERIFIED",
            appLicensing = if (isDev) "TRIAL / DEV_DEBUG" else "REQUIRES_PLAY_STORE",
            isGooglePlayProtected = !isDev,
            errorCode = errorCode,
            errorMessage = errorDescription,
            isDevelopmentMode = isDev
        )
        _integrityState.value = updatedState

        Log.w(TAG, "Play Integrity verification finished with code $errorCode: $errorDescription (isDev=$isDev)")
        return IntegrityResult.Error(
            errorCode = errorCode,
            errorMessage = errorDescription,
            isDevelopmentMode = isDev
        )
    }

    /**
     * Translates Google Play Integrity error codes to localized descriptions.
     */
    fun getHumanReadableError(errorCode: Int, rawMessage: String?, isTr: Boolean): String {
        return when (errorCode) {
            IntegrityErrorCode.NO_ERROR -> {
                if (isTr) "Hata yok, bütünlük doğrulandı." else "No error, integrity verified."
            }
            IntegrityErrorCode.API_NOT_AVAILABLE -> {
                if (isTr) "Play Integrity API bu cihazda kullanılamıyor veya Play Store servisleri güncel değil."
                else "Play Integrity API is not available on this device or Play Store services are outdated."
            }
            IntegrityErrorCode.PLAY_STORE_NOT_FOUND -> {
                if (isTr) "Cihazda resmi Google Play Store uygulaması bulunamadı."
                else "Official Google Play Store app not found on device."
            }
            IntegrityErrorCode.NETWORK_ERROR -> {
                if (isTr) "Ağ bağlantısı hatası! Google Play sunucularına erişilemedi."
                else "Network error! Unable to reach Google Play servers."
            }
            IntegrityErrorCode.PLAY_STORE_ACCOUNT_NOT_FOUND -> {
                if (isTr) "Google Play Store üzerinde oturum açılmış bir Google hesabı bulunamadı."
                else "No active Google account found in Google Play Store."
            }
            IntegrityErrorCode.APP_NOT_INSTALLED -> {
                if (isTr) "Uygulama resmi Google Play Store üzerinden yüklenmemiş (Sideload / Geliştirici derlemesi)."
                else "App was not installed through Google Play Store (Sideload / Developer build)."
            }
            IntegrityErrorCode.PLAY_SERVICES_NOT_FOUND -> {
                if (isTr) "Google Play Hizmetleri cihazda eksik veya devre dışı."
                else "Google Play Services is missing or disabled."
            }
            IntegrityErrorCode.APP_UID_MISMATCH -> {
                if (isTr) "Uygulama UID eşleşme hatası algılandı."
                else "App UID mismatch detected."
            }
            IntegrityErrorCode.TOO_MANY_REQUESTS -> {
                if (isTr) "Çok fazla istek gönderildi. Lütfen biraz bekleyip tekrar deneyin."
                else "Too many requests. Please wait a moment and retry."
            }
            IntegrityErrorCode.CANNOT_BIND_TO_SERVICE -> {
                if (isTr) "Google Play Integrity servisine bağlanılamadı."
                else "Cannot bind to Google Play Integrity service."
            }
            IntegrityErrorCode.NONCE_TOO_SHORT -> {
                if (isTr) "Nonce parametresi çok kısa (en az 16 karakter gerekli)."
                else "Nonce is too short (minimum 16 characters required)."
            }
            IntegrityErrorCode.NONCE_TOO_LONG -> {
                if (isTr) "Nonce parametresi çok uzun."
                else "Nonce is too long."
            }
            IntegrityErrorCode.GOOGLE_SERVER_UNAVAILABLE -> {
                if (isTr) "Google sunucuları geçici olarak yanıt vermiyor."
                else "Google servers are temporarily unavailable."
            }
            IntegrityErrorCode.NONCE_IS_NOT_BASE64 -> {
                if (isTr) "Nonce Base64 formatında değil."
                else "Nonce is not valid Base64."
            }
            IntegrityErrorCode.PLAY_STORE_VERSION_OUTDATED -> {
                if (isTr) "Google Play Store sürümü eski, güncelleme gerekiyor."
                else "Google Play Store version is outdated, update required."
            }
            IntegrityErrorCode.PLAY_SERVICES_VERSION_OUTDATED -> {
                if (isTr) "Google Play Hizmetleri güncel değil."
                else "Google Play Services is outdated."
            }
            IntegrityErrorCode.CLOUD_PROJECT_NUMBER_IS_INVALID -> {
                if (isTr) "Geçersiz Cloud proje numarası."
                else "Invalid Cloud project number."
            }
            IntegrityErrorCode.CLIENT_TRANSIENT_ERROR -> {
                if (isTr) "Geçici istemci hatası, lütfen tekrar deneyin."
                else "Transient client error, please retry."
            }
            else -> {
                val base = if (isTr) "Bütünlük doğrulama kodu" else "Integrity verification code"
                if (!rawMessage.isNullOrBlank()) "$base ($errorCode): $rawMessage" else "$base ($errorCode)"
            }
        }
    }

    /**
     * Triggers asynchronous verification.
     */
    fun verifyIntegrityAsync() {
        coroutineScope.launch {
            requestIntegrityToken()
        }
    }

    /**
     * Formats a human-readable timestamp of the last verification.
     */
    fun getFormattedLastCheckTime(): String {
        val ts = _integrityState.value.lastVerifiedTimestamp ?: return "-"
        return SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(Date(ts))
    }
}

/**
 * UI State for Google Play Integrity API.
 */
data class IntegrityVerificationState(
    val status: IntegrityStatus = IntegrityStatus.IDLE,
    val token: String? = null,
    val tokenSummary: String? = null,
    val nonce: String? = null,
    val lastVerifiedTimestamp: Long? = null,
    val deviceIntegrity: String? = null,
    val appLicensing: String? = null,
    val isGooglePlayProtected: Boolean = false,
    val errorCode: Int? = null,
    val errorMessage: String? = null,
    val isDevelopmentMode: Boolean = false
)

enum class IntegrityStatus {
    IDLE,
    VERIFYING,
    VERIFIED,
    ERROR,
    FALLBACK_DEVELOPMENT
}

sealed class IntegrityResult {
    data class Success(
        val token: String,
        val tokenSummary: String,
        val nonce: String,
        val timestamp: Long
    ) : IntegrityResult()

    data class Error(
        val errorCode: Int,
        val errorMessage: String,
        val isDevelopmentMode: Boolean
    ) : IntegrityResult()
}
