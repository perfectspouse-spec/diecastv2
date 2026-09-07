package com.example.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.DiecastCar
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object JsonWebExporter {

    fun exportAndShareJson(
        context: Context,
        cars: List<DiecastCar>,
        currencyCode: String = "TRL"
    ) {
        val isTr = LocaleHelper.isTurkishStatic()
        if (cars.isEmpty()) {
            val emptyMsg = if (isTr) "Dışa aktarılacak model bulunamadı!" else "No models to export!"
            Toast.makeText(context, emptyMsg, Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val jsonString = buildJsonString(cars, currencyCode)
            val dateStr = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = if (isTr) "Diecast_Web_Koleksiyon_$dateStr.json" else "Diecast_Web_Collection_$dateStr.json"

            val cacheDir = context.cacheDir
            val file = File(cacheDir, fileName)
            file.writeText(jsonString, Charsets.UTF_8)

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(
                    Intent.EXTRA_SUBJECT,
                    if (isTr) "Diecast Web Koleksiyon JSON Verisi ($fileName)"
                    else "Diecast Web Collection JSON Data ($fileName)"
                )
                putExtra(
                    Intent.EXTRA_TEXT,
                    if (isTr) "Web siteleri ve veritabanları ile uyumlu Diecast Koleksiyon JSON yedeği (${cars.size} model)."
                    else "Web & database compatible Diecast Collection JSON backup (${cars.size} models)."
                )
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooserTitle = if (isTr) "Web JSON Verisini Dışa Aktar / Paylaş" else "Export / Share Web JSON"
            val chooser = Intent.createChooser(shareIntent, chooserTitle).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(chooser)

            val successMsg = if (isTr) {
                "${cars.size} model standart Web JSON formatında dışa aktarıldı."
            } else {
                "${cars.size} models exported in standard Web JSON format."
            }
            Toast.makeText(context, successMsg, Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            val errorMsg = if (isTr) {
                "JSON dışa aktarma hatası: ${e.localizedMessage}"
            } else {
                "JSON export error: ${e.localizedMessage}"
            }
            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
        }
    }

    fun buildJsonString(cars: List<DiecastCar>, currencyCode: String): String {
        val root = JSONObject()
        root.put("app", "Diecast Collection")
        root.put("version", "1.0")
        root.put("exportedAt", SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date()))
        root.put("currency", currencyCode)
        root.put("totalCount", cars.size)

        val itemsArray = JSONArray()
        for (car in cars) {
            val itemObj = JSONObject().apply {
                put("id", car.id)
                put("manufacturer", car.manufacturer)
                put("carBrand", car.carBrand)
                put("model", car.model)
                put("modelYear", car.modelYear)
                put("scale", car.scale)
                put("series", car.series)
                put("productionYear", car.productionYear)
                put("condition", car.condition)
                put("color", car.color)
                put("purchasePrice", car.purchasePrice)
                put("purchaseDate", car.purchaseDate)
                put("estimatedValue", car.estimatedValue)
                put("photoPath", car.photoPath ?: "")
                put("photoPath2", car.photoPath2 ?: "")
                put("photoPath3", car.photoPath3 ?: "")
                put("photoPath4", car.photoPath4 ?: "")
                put("notes", car.notes)
                put("tags", car.tags)
                put("barcode", car.barcode)
                put("isWishlist", car.isWishlist)
                put("isFavorite", car.isFavorite)
                put("createdAt", car.createdAt)
            }
            itemsArray.put(itemObj)
        }
        root.put("cars", itemsArray)
        return root.toString(2)
    }

    fun parseCarsFromJson(jsonStr: String): List<DiecastCar> {
        val list = mutableListOf<DiecastCar>()
        try {
            val root = JSONObject(jsonStr)
            val itemsArray = if (root.has("cars")) {
                root.getJSONArray("cars")
            } else if (root.has("items")) {
                root.getJSONArray("items")
            } else {
                JSONArray()
            }

            for (i in 0 until itemsArray.length()) {
                val obj = itemsArray.getJSONObject(i)
                val car = DiecastCar(
                    id = 0, // Reset ID for new import so DB assigns new autoincrement ID
                    manufacturer = obj.optString("manufacturer", ""),
                    carBrand = obj.optString("carBrand", ""),
                    model = obj.optString("model", ""),
                    modelYear = obj.optString("modelYear", ""),
                    scale = obj.optString("scale", "1:64"),
                    series = obj.optString("series", ""),
                    productionYear = obj.optString("productionYear", ""),
                    condition = obj.optString("condition", ""),
                    color = obj.optString("color", ""),
                    purchasePrice = obj.optDouble("purchasePrice", 0.0),
                    purchaseDate = obj.optLong("purchaseDate", System.currentTimeMillis()),
                    estimatedValue = obj.optDouble("estimatedValue", 0.0),
                    photoPath = obj.optString("photoPath").takeIf { it.isNotBlank() },
                    photoPath2 = obj.optString("photoPath2").takeIf { it.isNotBlank() },
                    photoPath3 = obj.optString("photoPath3").takeIf { it.isNotBlank() },
                    photoPath4 = obj.optString("photoPath4").takeIf { it.isNotBlank() },
                    notes = obj.optString("notes", ""),
                    tags = obj.optString("tags", ""),
                    barcode = obj.optString("barcode", ""),
                    isWishlist = obj.optBoolean("isWishlist", false),
                    isFavorite = obj.optBoolean("isFavorite", false),
                    createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                )
                list.add(car)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }
}
