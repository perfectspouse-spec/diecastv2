package com.example.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object LocaleHelper {
    // Language options: "EN", "TR", "AUTO"
    val appLanguage = MutableStateFlow("EN")

    fun isTurkishStatic(): Boolean {
        return when (appLanguage.value) {
            "TR" -> true
            "EN" -> false
            else -> {
                val current = Locale.getDefault()
                current.language.equals("tr", ignoreCase = true) || current.country.equals("TR", ignoreCase = true)
            }
        }
    }

    fun setLanguage(langCode: String) {
        appLanguage.value = langCode
    }

    fun isTurkish(): Boolean = isTurkishStatic()
}

@Composable
fun isTurkishLocale(): Boolean {
    val lang by LocaleHelper.appLanguage.collectAsState()
    return when (lang) {
        "TR" -> true
        "EN" -> false
        else -> {
            val current = Locale.getDefault()
            current.language.equals("tr", ignoreCase = true) || current.country.equals("TR", ignoreCase = true)
        }
    }
}

fun formatAmount(amount: Double): String {
    val symbols = DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = ','
        decimalSeparator = '.'
    }
    val formatter = DecimalFormat("#,##0.00", symbols)
    return formatter.format(amount)
}

fun parseAmount(input: String): Double {
    if (input.isBlank()) return 0.0
    val cleaned = input.trim()
    return try {
        if (cleaned.contains(",") && cleaned.contains(".")) {
            cleaned.replace(",", "").toDoubleOrNull() ?: 0.0
        } else if (cleaned.contains(",")) {
            val parts = cleaned.split(",")
            if (parts.size == 2 && parts[1].length <= 2) {
                cleaned.replace(",", ".").toDoubleOrNull() ?: 0.0
            } else {
                cleaned.replace(",", "").toDoubleOrNull() ?: 0.0
            }
        } else {
            cleaned.toDoubleOrNull() ?: 0.0
        }
    } catch (e: Exception) {
        0.0
    }
}
