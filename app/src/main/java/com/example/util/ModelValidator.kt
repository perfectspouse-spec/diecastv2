package com.example.util

import com.example.data.DiecastCar

data class ValidationResult(
    val needsReview: Boolean,
    val issues: List<String>
) {
    fun getFormattedReason(isTr: Boolean): String {
        return issues.joinToString("\n• ")
    }
}

object ModelValidator {

    fun validate(car: DiecastCar): ValidationResult {
        val issues = mutableListOf<String>()
        val isTr = LocaleHelper.isTurkishStatic()

        val brand = car.carBrand.trim()
        val model = car.model.trim()
        val manufacturer = car.manufacturer.trim()
        val series = car.series.trim()
        val scale = car.scale.trim()
        val modelYearStr = car.modelYear.trim()
        val prodYearStr = car.productionYear.trim()
        val pPrice = car.purchasePrice
        val eVal = car.estimatedValue

        // 1. Eksik / Boş Zorunlu Alan Kontrolü
        if (brand.isBlank()) {
            issues.add(if (isTr) "Araba markası eksik." else "Car brand is missing.")
        }
        if (model.isBlank()) {
            issues.add(if (isTr) "Model adı eksik." else "Model name is missing.")
        }

        // 2. Marka - Model Uyuşmazlığı Kontrolü
        if (brand.isNotBlank() && model.isNotBlank()) {
            val brandLower = brand.lowercase()
            val modelLower = model.lowercase()

            val brandMismatch = checkBrandModelMismatch(brandLower, modelLower)
            if (brandMismatch != null) {
                issues.add(brandMismatch)
            }
        }

        // 3. Üretici Marka - Seri Uyuşmazlığı Kontrolü
        if (series.isNotBlank() && manufacturer.isNotBlank()) {
            val seriesLower = series.lowercase()
            val mfgLower = manufacturer.lowercase()

            val hwSeriesKeywords = listOf("hw screen time", "hw drift", "car culture", "boulevard", "team transport", "track stars", "retro racers")
            val isHwSeries = hwSeriesKeywords.any { seriesLower.contains(it) }

            if (isHwSeries && !mfgLower.contains("hot wheels") && !mfgLower.contains("hw")) {
                issues.add(
                    if (isTr) "Seri '$series' genelde Hot Wheels üreticisine aittir, ancak '$manufacturer' seçilmiş."
                    else "Series '$series' usually belongs to Hot Wheels, but manufacturer is '$manufacturer'."
                )
            }

            if (seriesLower.contains("moving parts") && !mfgLower.contains("matchbox")) {
                issues.add(
                    if (isTr) "'Moving Parts' serisi Matchbox üreticisine aittir."
                    else "'Moving Parts' series belongs to Matchbox manufacturer."
                )
            }
        }

        // 4. Model Yılı ve Üretim Yılı Format & Mantık Kontrolü
        val modelYear = modelYearStr.toIntOrNull()
        if (modelYearStr.isNotBlank()) {
            if (modelYear == null || modelYear !in 1880..2030) {
                issues.add(
                    if (isTr) "Model Yılı ($modelYearStr) gerçekçi bir yıl aralığında değil (1880-2030)."
                    else "Model Year ($modelYearStr) is out of realistic range (1880-2030)."
                )
            }
        }

        val prodYear = prodYearStr.toIntOrNull()
        if (prodYearStr.isNotBlank()) {
            if (prodYear == null || prodYear !in 1968..2030) {
                issues.add(
                    if (isTr) "Diecast Çıkış Yılı ($prodYearStr) geçerli yıl aralığında değil (1968-2030)."
                    else "Release Year ($prodYearStr) is out of range (1968-2030)."
                )
            }
        }

        if (modelYear != null && prodYear != null) {
            if (prodYear < modelYear - 2) {
                issues.add(
                    if (isTr) "Model Yılı ($modelYear) diecast Çıkış Yılından ($prodYear) daha yeni olamaz."
                    else "Model Year ($modelYear) cannot be newer than diecast Release Year ($prodYear)."
                )
            }
        }

        // 5. Ölçek Format Kontrolü
        if (scale.isNotBlank()) {
            val scalePattern = Regex("^1:\\d+$")
            if (!scalePattern.matches(scale)) {
                issues.add(
                    if (isTr) "Ölçek formatı ('$scale') standart ölçek formatına (ör. 1:64, 1:24) uymuyor."
                    else "Scale format ('$scale') does not match standard pattern (e.g. 1:64, 1:24)."
                )
            }
        }

        // 6. Fiyat ve Değer Anormallik Kontrolü
        if (pPrice < 0 || eVal < 0) {
            issues.add(if (isTr) "Fiyat veya tahmini değer negatif olamaz." else "Price or estimated value cannot be negative.")
        } else if (pPrice > 0 && eVal > 0) {
            val ratio = eVal / pPrice
            if (ratio > 100.0) {
                issues.add(
                    if (isTr) "Tahmini değer ($eVal) alış fiyatından ($pPrice) 100 kat fazla (Yazım hatası olabilir)."
                    else "Estimated value ($eVal) is over 100x purchase price ($pPrice) (Potential typo)."
                )
            } else if (ratio < 0.01) {
                issues.add(
                    if (isTr) "Alış fiyatı ($pPrice) tahmini değerden ($eVal) 100 kat fazla (Yazım hatası olabilir)."
                    else "Purchase price ($pPrice) is over 100x estimated value ($eVal) (Potential typo)."
                )
            }
        }

        return ValidationResult(
            needsReview = issues.isNotEmpty(),
            issues = issues
        )
    }

    private fun checkBrandModelMismatch(brand: String, model: String): String? {
        val isTr = LocaleHelper.isTurkishStatic()

        val brandRestrictions = mapOf(
            "porsche" to listOf("mustang", "civic", "skyline", "corvette", "camaro", "golf", "gtr", "supra", "challenger", "charger", "ferrari", "lamborghini"),
            "ford" to listOf("porsche", "911", "civic", "skyline", "ferrari", "lamborghini", "supra"),
            "bmw" to listOf("mustang", "porsche", "911", "civic", "corvette", "ferrari", "lamborghini", "nissan", "supra"),
            "nissan" to listOf("mustang", "911", "porsche", "corvette", "ferrari", "lamborghini", "bmw"),
            "honda" to listOf("mustang", "911", "porsche", "corvette", "ferrari", "lamborghini", "bmw", "skyline"),
            "toyota" to listOf("mustang", "porsche", "911", "bmw", "corvette", "ferrari", "lamborghini", "skyline"),
            "ferrari" to listOf("porsche", "lamborghini", "mustang", "bmw", "nissan", "toyota", "audi", "corvette"),
            "lamborghini" to listOf("ferrari", "porsche", "mustang", "bmw", "nissan", "toyota", "audi", "corvette"),
            "audi" to listOf("porsche", "ferrari", "lamborghini", "mustang", "civic", "skyline", "supra"),
            "chevrolet" to listOf("porsche", "911", "ferrari", "lamborghini", "civic", "skyline", "supra", "bmw")
        )

        for ((b, forbiddenList) in brandRestrictions) {
            if (brand.contains(b)) {
                for (forbidden in forbiddenList) {
                    if (model.contains(forbidden)) {
                        return if (isTr) {
                            "Marka ($brand) ile Model Adı ($model) uyuşmuyor ('$forbidden' terimi tespit edildi)."
                        } else {
                            "Brand ($brand) and Model Name ($model) mismatch ('$forbidden' keyword detected)."
                        }
                    }
                }
            }
        }
        return null
    }
}
