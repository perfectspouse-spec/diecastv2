package com.example.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class TagColorScheme(
    val containerColor: Color,
    val contentColor: Color,
    val borderColor: Color
)

object TagColorHelper {

    @Composable
    fun getColorSchemeForTag(tag: String): TagColorScheme {
        val isDark = isSystemInDarkTheme()
        val normalized = tag.trim().lowercase()

        return when {
            // Spor / Sport
            normalized.contains("spor") || normalized.contains("sport") -> {
                if (isDark) {
                    TagColorScheme(
                        containerColor = Color(0xFF3E1A1D),
                        contentColor = Color(0xFFFF8A80),
                        borderColor = Color(0xFFE57373)
                    )
                } else {
                    TagColorScheme(
                        containerColor = Color(0xFFFFEBEE),
                        contentColor = Color(0xFFC62828),
                        borderColor = Color(0xFFEF9A9A)
                    )
                }
            }

            // Klasik / Classic
            normalized.contains("klasik") || normalized.contains("classic") || normalized.contains("vintage") -> {
                if (isDark) {
                    TagColorScheme(
                        containerColor = Color(0xFF3E2C14),
                        contentColor = Color(0xFFFFD54F),
                        borderColor = Color(0xFFFFB74D)
                    )
                } else {
                    TagColorScheme(
                        containerColor = Color(0xFFFFF8E1),
                        contentColor = Color(0xFFE65100),
                        borderColor = Color(0xFFFFE082)
                    )
                }
            }

            // Yarış / Racing
            normalized.contains("yarış") || normalized.contains("yaris") || normalized.contains("racing") || normalized.contains("track") -> {
                if (isDark) {
                    TagColorScheme(
                        containerColor = Color(0xFF3E2312),
                        contentColor = Color(0xFFFFAB40),
                        borderColor = Color(0xFFFF9800)
                    )
                } else {
                    TagColorScheme(
                        containerColor = Color(0xFFFFF3E0),
                        contentColor = Color(0xFFBF360C),
                        borderColor = Color(0xFFFFCC80)
                    )
                }
            }

            // Supercar / Hypercar
            normalized.contains("supercar") || normalized.contains("hypercar") -> {
                if (isDark) {
                    TagColorScheme(
                        containerColor = Color(0xFF2E153B),
                        contentColor = Color(0xFFEA80FC),
                        borderColor = Color(0xFFBA68C8)
                    )
                } else {
                    TagColorScheme(
                        containerColor = Color(0xFFF3E5F5),
                        contentColor = Color(0xFF6A1B9A),
                        borderColor = Color(0xFFCE93D8)
                    )
                }
            }

            // JDM
            normalized.contains("jdm") || normalized.contains("drift") -> {
                if (isDark) {
                    TagColorScheme(
                        containerColor = Color(0xFF3B1227),
                        contentColor = Color(0xFFFF80AB),
                        borderColor = Color(0xFFF06292)
                    )
                } else {
                    TagColorScheme(
                        containerColor = Color(0xFFFCE4EC),
                        contentColor = Color(0xFFAD1457),
                        borderColor = Color(0xFFF48FB1)
                    )
                }
            }

            // Muscle
            normalized.contains("muscle") -> {
                if (isDark) {
                    TagColorScheme(
                        containerColor = Color(0xFF3E1F18),
                        contentColor = Color(0xFFFF8A65),
                        borderColor = Color(0xFFFF7043)
                    )
                } else {
                    TagColorScheme(
                        containerColor = Color(0xFFFBE9E7),
                        contentColor = Color(0xFFD84315),
                        borderColor = Color(0xFFFFAB91)
                    )
                }
            }

            // Off-Road / 4x4
            normalized.contains("off-road") || normalized.contains("offroad") || normalized.contains("4x4") || normalized.contains("suv") -> {
                if (isDark) {
                    TagColorScheme(
                        containerColor = Color(0xFF15331E),
                        contentColor = Color(0xFF81C784),
                        borderColor = Color(0xFF4CAF50)
                    )
                } else {
                    TagColorScheme(
                        containerColor = Color(0xFFE8F5E9),
                        contentColor = Color(0xFF1B5E20),
                        borderColor = Color(0xFFA5D6A7)
                    )
                }
            }

            // Custom / Tuning
            normalized.contains("custom") || normalized.contains("tuning") -> {
                if (isDark) {
                    TagColorScheme(
                        containerColor = Color(0xFF103336),
                        contentColor = Color(0xFF4DD0E1),
                        borderColor = Color(0xFF00ACC1)
                    )
                } else {
                    TagColorScheme(
                        containerColor = Color(0xFFE0F7FA),
                        contentColor = Color(0xFF006064),
                        borderColor = Color(0xFF80DEEA)
                    )
                }
            }

            // Film / Dizi / Movie / TV
            normalized.contains("film") || normalized.contains("dizi") || normalized.contains("movie") || normalized.contains("tv") -> {
                if (isDark) {
                    TagColorScheme(
                        containerColor = Color(0xFF1B1E38),
                        contentColor = Color(0xFF8C9EFF),
                        borderColor = Color(0xFF5C6BC0)
                    )
                } else {
                    TagColorScheme(
                        containerColor = Color(0xFFE8EAF6),
                        contentColor = Color(0xFF1A237E),
                        borderColor = Color(0xFF9FA8DA)
                    )
                }
            }

            // Polis / Acil / Police / Rescue
            normalized.contains("polis") || normalized.contains("police") || normalized.contains("acil") || normalized.contains("rescue") -> {
                if (isDark) {
                    TagColorScheme(
                        containerColor = Color(0xFF10283B),
                        contentColor = Color(0xFF40C4FF),
                        borderColor = Color(0xFF03A9F4)
                    )
                } else {
                    TagColorScheme(
                        containerColor = Color(0xFFE1F5FE),
                        contentColor = Color(0xFF01579B),
                        borderColor = Color(0xFF81D4FA)
                    )
                }
            }

            // Konsept / Concept
            normalized.contains("konsept") || normalized.contains("concept") -> {
                if (isDark) {
                    TagColorScheme(
                        containerColor = Color(0xFF26183B),
                        contentColor = Color(0xFFB388FF),
                        borderColor = Color(0xFF7C4DFF)
                    )
                } else {
                    TagColorScheme(
                        containerColor = Color(0xFFEDE7F6),
                        contentColor = Color(0xFF311B92),
                        borderColor = Color(0xFFB39DDB)
                    )
                }
            }

            // Elektrikli / Electric
            normalized.contains("elektrik") || normalized.contains("electric") || normalized.contains("ev") -> {
                if (isDark) {
                    TagColorScheme(
                        containerColor = Color(0xFF223315),
                        contentColor = Color(0xFFAED581),
                        borderColor = Color(0xFF7CB342)
                    )
                } else {
                    TagColorScheme(
                        containerColor = Color(0xFFF1F8E9),
                        contentColor = Color(0xFF33691E),
                        borderColor = Color(0xFFC5E1A5)
                    )
                }
            }

            // Chase / STH / Nadir / Rare / Redline (Premium Gold)
            normalized.contains("chase") || normalized.contains("sth") || normalized.contains("th") || 
            normalized.contains("nadir") || normalized.contains("rare") || normalized.contains("redline") -> {
                if (isDark) {
                    TagColorScheme(
                        containerColor = Color(0xFF3D3200),
                        contentColor = Color(0xFFFFD700),
                        borderColor = Color(0xFFFFC107)
                    )
                } else {
                    TagColorScheme(
                        containerColor = Color(0xFFFFFDE7),
                        contentColor = Color(0xFFF57F17),
                        borderColor = Color(0xFFFFEE58)
                    )
                }
            }

            // Deterministic hash palette for any custom user tag
            else -> {
                val hash = kotlin.math.abs(tag.hashCode())
                val paletteIndex = hash % 6
                when (paletteIndex) {
                    0 -> if (isDark) {
                        TagColorScheme(Color(0xFF103328), Color(0xFF69F0AE), Color(0xFF00E676))
                    } else {
                        TagColorScheme(Color(0xFFE8F8F5), Color(0xFF0E6251), Color(0xFFA2D9CE))
                    }
                    1 -> if (isDark) {
                        TagColorScheme(Color(0xFF371836), Color(0xFFFF4081), Color(0xFFE040FB))
                    } else {
                        TagColorScheme(Color(0xFFFDF2E9), Color(0xFFB9770E), Color(0xFFF5CBA7))
                    }
                    2 -> if (isDark) {
                        TagColorScheme(Color(0xFF1A2A3A), Color(0xFF80D8FF), Color(0xFF00B0FF))
                    } else {
                        TagColorScheme(Color(0xFFEBF5FB), Color(0xFF1B4F72), Color(0xFFAED6F1))
                    }
                    3 -> if (isDark) {
                        TagColorScheme(Color(0xFF2E2415), Color(0xFFFFD180), Color(0xFFFFAB40))
                    } else {
                        TagColorScheme(Color(0xFFFEF9E7), Color(0xFF7D6608), Color(0xFFF9E79F))
                    }
                    4 -> if (isDark) {
                        TagColorScheme(Color(0xFF281830), Color(0xFFE040FB), Color(0xFFD500F9))
                    } else {
                        TagColorScheme(Color(0xFFF5EEF8), Color(0xFF5B2C6F), Color(0xFFD7BDE2))
                    }
                    else -> if (isDark) {
                        TagColorScheme(Color(0xFF1A3326), Color(0xFFB9F6CA), Color(0xFF00C853))
                    } else {
                        TagColorScheme(Color(0xFFEAFAF1), Color(0xFF145A32), Color(0xFFA9DFBF))
                    }
                }
            }
        }
    }
}
