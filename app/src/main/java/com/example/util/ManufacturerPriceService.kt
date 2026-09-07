package com.example.util

import com.example.data.DiecastCar
import java.util.Locale

data class PriceEstimateResult(
    val estimatedValue: Double,
    val currencyCode: String,
    val baseMSRP: Double,
    val seriesMultiplier: Double,
    val scaleMultiplier: Double,
    val conditionMultiplier: Double,
    val rarityMultiplier: Double,
    val priceRangeMin: Double,
    val priceRangeMax: Double,
    val marketNotes: String,
    val manufacturerName: String,
    val confidence: String
)

data class CarPriceDiff(
    val carId: Int,
    val modelName: String,
    val brand: String,
    val manufacturer: String,
    val oldValue: Double,
    val newValue: Double,
    val photoPath: String?
)

data class BatchPriceUpdateSummary(
    val totalCarsCount: Int,
    val updatedCarsCount: Int,
    val oldTotalCollectionValue: Double,
    val newTotalCollectionValue: Double,
    val valueDifference: Double,
    val percentageChange: Double,
    val currencyCode: String,
    val updatedCars: List<DiecastCar>,
    val itemDiffs: List<CarPriceDiff>
)

object ManufacturerPriceService {

    // Exchange rates normalized to TRY (TRL)
    private const val USD_TO_TRY = 36.5
    private const val EUR_TO_TRY = 38.2

    /**
     * Estimates market price for a given diecast car based on manufacturer,
     * series, scale, condition, brand, and casting popularity.
     */
    fun estimateCarPrice(car: DiecastCar, targetCurrency: String = "TRL"): PriceEstimateResult {
        val mfgLower = car.manufacturer.trim().lowercase(Locale.ROOT)
        val seriesLower = car.series.trim().lowercase(Locale.ROOT)
        val scaleLower = car.scale.trim().lowercase(Locale.ROOT)
        val condLower = car.condition.trim().lowercase(Locale.ROOT)
        val brandLower = car.carBrand.trim().lowercase(Locale.ROOT)
        val modelLower = car.model.trim().lowercase(Locale.ROOT)

        // 1. Base Manufacturer Benchmark Price (in TRL standard baseline)
        var basePriceInTry = when {
            mfgLower.contains("hot wheels") || mfgLower.contains("hotwheels") -> 140.0
            mfgLower.contains("matchbox") -> 130.0
            mfgLower.contains("mini gt") || mfgLower.contains("minigt") -> 750.0
            mfgLower.contains("kaido house") || mfgLower.contains("kaidohouse") -> 1150.0
            mfgLower.contains("inno64") || mfgLower.contains("inno 64") -> 1100.0
            mfgLower.contains("tarmac") -> 950.0
            mfgLower.contains("pop race") || mfgLower.contains("poprace") -> 900.0
            mfgLower.contains("tomica") -> 320.0
            mfgLower.contains("majorette") -> 180.0
            mfgLower.contains("greenlight") -> 450.0
            mfgLower.contains("autoart") -> 6800.0
            mfgLower.contains("kyosho") -> 1200.0
            mfgLower.contains("spark") -> 2200.0
            mfgLower.contains("solido") -> 950.0
            mfgLower.contains("schuco") -> 650.0
            mfgLower.contains("bburago") || mfgLower.contains("burago") -> 480.0
            mfgLower.contains("maisto") -> 420.0
            mfgLower.contains("jada") -> 520.0
            mfgLower.contains("m2") -> 580.0
            mfgLower.contains("johnny lightning") -> 480.0
            mfgLower.contains("norev") -> 1400.0
            mfgLower.contains("welly") -> 350.0
            else -> 250.0
        }

        // 2. Series / Tier Multiplier
        var seriesMultiplier = 1.0
        when {
            // Hot Wheels / Matchbox Premium tiers
            seriesLower.contains("super treasure") || seriesLower.contains("sth") || modelLower.contains("sth") -> {
                seriesMultiplier = 14.0
            }
            seriesLower.contains("treasure hunt") || seriesLower.contains("th") -> {
                seriesMultiplier = 3.2
            }
            seriesLower.contains("red line club") || seriesLower.contains("rlc") || seriesLower.contains("elite 64") -> {
                seriesMultiplier = 8.5
            }
            seriesLower.contains("chase") || modelLower.contains("chase") -> {
                seriesMultiplier = 5.0
            }
            seriesLower.contains("boulevard") || seriesLower.contains("car culture") || 
            seriesLower.contains("team transport") || seriesLower.contains("real riders") ||
            seriesLower.contains("premium") || seriesLower.contains("modern classics") ||
            seriesLower.contains("japanese historics") || seriesLower.contains("exotics") -> {
                seriesMultiplier = if (mfgLower.contains("hot wheels") || mfgLower.contains("matchbox")) 3.6 else 1.5
            }
            seriesLower.contains("moving parts") || seriesLower.contains("collectors") || seriesLower.contains("deluxe") -> {
                seriesMultiplier = 2.4
            }
            seriesLower.contains("silver") || seriesLower.contains("themed") || seriesLower.contains("pop culture") -> {
                seriesMultiplier = 1.8
            }
            seriesLower.contains("vintage neo") || seriesLower.contains("limited vintage") || seriesLower.contains("tlv") -> {
                seriesMultiplier = 4.2
            }
            seriesLower.contains("kaido") || seriesLower.contains("pro street") -> {
                seriesMultiplier = 1.3
            }
            seriesLower.contains("diorama") || seriesLower.contains("special edition") || seriesLower.contains("signature") -> {
                seriesMultiplier = 2.2
            }
        }

        // 3. Scale Multiplier (normalized to 1:64)
        var scaleMultiplier = when {
            scaleLower.contains("1:18") -> if (mfgLower.contains("autoart")) 1.0 else 5.8
            scaleLower.contains("1:12") -> 10.5
            scaleLower.contains("1:24") -> 2.8
            scaleLower.contains("1:32") -> 1.7
            scaleLower.contains("1:43") -> 2.4
            scaleLower.contains("1:87") -> 0.9
            else -> 1.0 // default 1:64
        }

        // 4. Condition / Package Multiplier
        var conditionMultiplier = when {
            condLower.contains("akrilik") || condLower.contains("display") -> 1.35
            condLower.contains("kutu") || condLower.contains("mint") || condLower.contains("blister") -> 1.20
            condLower.contains("açık") || condLower.contains("kutusuz") || condLower.contains("loose") -> 0.78
            condLower.contains("hasar") || condLower.contains("yıpranmış") || condLower.contains("tamirli") -> 0.45
            else -> 1.0
        }

        // 5. Brand & Model Popularity / Rarity Factor
        var rarityMultiplier = 1.0
        when {
            brandLower.contains("ferrari") -> rarityMultiplier = 1.65
            brandLower.contains("porsche") -> rarityMultiplier = 1.45
            brandLower.contains("nissan") && (modelLower.contains("skyline") || modelLower.contains("gt-r") || modelLower.contains("silvia") || modelLower.contains("180sx") || modelLower.contains("240z")) -> rarityMultiplier = 1.60
            brandLower.contains("lamborghini") -> rarityMultiplier = 1.40
            brandLower.contains("koenigsegg") || brandLower.contains("pagani") || brandLower.contains("bugatti") -> rarityMultiplier = 1.75
            brandLower.contains("toyota") && (modelLower.contains("supra") || modelLower.contains("ae86") || modelLower.contains("chaser") || modelLower.contains("mr2")) -> rarityMultiplier = 1.50
            brandLower.contains("honda") && (modelLower.contains("nsx") || modelLower.contains("s2000") || modelLower.contains("civic type r")) -> rarityMultiplier = 1.40
            brandLower.contains("bmw") && (modelLower.contains("m3") || modelLower.contains("m4") || modelLower.contains("m5") || modelLower.contains("csl")) -> rarityMultiplier = 1.35
            brandLower.contains("mercedes") && (modelLower.contains("amg") || modelLower.contains("evolution") || modelLower.contains("gullwing") || modelLower.contains("clk gtr")) -> rarityMultiplier = 1.40
            brandLower.contains("ford") && (modelLower.contains("gt40") || modelLower.contains("ford gt") || modelLower.contains("mustang shelby")) -> rarityMultiplier = 1.30
            brandLower.contains("mazda") && (modelLower.contains("rx-7") || modelLower.contains("787b") || modelLower.contains("miata")) -> rarityMultiplier = 1.30
            brandLower.contains("shelby") || brandLower.contains("lancia") || brandLower.contains("mclaren") -> rarityMultiplier = 1.35
            else -> rarityMultiplier = 1.05
        }

        // 6. Vintage / Release Year Age appreciation factor
        val modelYearNum = car.modelYear.toIntOrNull()
        val prodYearNum = car.productionYear.toIntOrNull()
        val effectiveYear = prodYearNum ?: modelYearNum
        if (effectiveYear != null && effectiveYear < 2015) {
            val yearsOld = maxOf(0, 2026 - effectiveYear)
            rarityMultiplier *= (1.0 + (yearsOld * 0.02).coerceAtMost(0.60)) // up to +60% for older items
        }

        // Raw Value in TRY
        val finalCalculatedTry = (basePriceInTry * seriesMultiplier * scaleMultiplier * conditionMultiplier * rarityMultiplier)
        
        // Currency Conversion
        val exchangeFactor = when (targetCurrency.uppercase(Locale.ROOT)) {
            "USD" -> 1.0 / USD_TO_TRY
            "EUR" -> 1.0 / EUR_TO_TRY
            else -> 1.0
        }

        val convertedValue = Math.round((finalCalculatedTry * exchangeFactor) / 5.0) * 5.0 // Round to nearest 5
        val minRange = Math.round(convertedValue * 0.88 / 5.0) * 5.0
        val maxRange = Math.round(convertedValue * 1.18 / 5.0) * 5.0

        val notes = buildString {
            append("${car.manufacturer} ${car.scale}")
            if (car.series.isNotBlank()) append(" (${car.series})")
            append(" üretici liste ve piyasa rayici")
        }

        val confidence = when {
            seriesMultiplier > 3.0 || rarityMultiplier > 1.5 -> "Yüksek Koleksiyon Değeri"
            else -> "Standart Üretici Rayici"
        }

        return PriceEstimateResult(
            estimatedValue = maxOf(10.0, convertedValue),
            currencyCode = targetCurrency,
            baseMSRP = basePriceInTry * exchangeFactor,
            seriesMultiplier = seriesMultiplier,
            scaleMultiplier = scaleMultiplier,
            conditionMultiplier = conditionMultiplier,
            rarityMultiplier = rarityMultiplier,
            priceRangeMin = maxOf(10.0, minRange),
            priceRangeMax = maxOf(10.0, maxRange),
            marketNotes = notes,
            manufacturerName = car.manufacturer.ifBlank { "Genel Diecast" },
            confidence = confidence
        )
    }

    /**
     * Batch updates prices for all cars in the collection and produces a comprehensive summary.
     */
    fun calculateBatchUpdate(
        allCars: List<DiecastCar>,
        includeWishlist: Boolean = true,
        targetCurrency: String = "TRL",
        marketAdjustmentFactor: Double = 1.0 // 1.0 = Standard, 1.15 = Collector Market Premium, 0.90 = Fast Sale / MSRP
    ): BatchPriceUpdateSummary {
        val targetCars = if (includeWishlist) allCars else allCars.filter { !it.isWishlist }
        val oldTotal = allCars.filter { !it.isWishlist }.sumOf { it.estimatedValue }

        val diffs = mutableListOf<CarPriceDiff>()
        val updatedList = allCars.map { car ->
            if (includeWishlist || !car.isWishlist) {
                val estimate = estimateCarPrice(car, targetCurrency)
                val adjustedValue = Math.round((estimate.estimatedValue * marketAdjustmentFactor) / 5.0) * 5.0
                
                if (adjustedValue != car.estimatedValue) {
                    diffs.add(
                        CarPriceDiff(
                            carId = car.id,
                            modelName = "${car.carBrand} ${car.model}".trim(),
                            brand = car.carBrand,
                            manufacturer = car.manufacturer,
                            oldValue = car.estimatedValue,
                            newValue = adjustedValue,
                            photoPath = car.getPhotoList().firstOrNull() ?: car.photoPath
                        )
                    )
                }
                car.copy(estimatedValue = adjustedValue)
            } else {
                car
            }
        }

        val newTotal = updatedList.filter { !it.isWishlist }.sumOf { it.estimatedValue }
        val diff = newTotal - oldTotal
        val percent = if (oldTotal > 0) (diff / oldTotal) * 100.0 else 0.0

        return BatchPriceUpdateSummary(
            totalCarsCount = allCars.size,
            updatedCarsCount = diffs.size,
            oldTotalCollectionValue = oldTotal,
            newTotalCollectionValue = newTotal,
            valueDifference = diff,
            percentageChange = percent,
            currencyCode = targetCurrency,
            updatedCars = updatedList,
            itemDiffs = diffs
        )
    }
}
