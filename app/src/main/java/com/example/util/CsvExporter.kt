package com.example.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.DiecastCar
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvExporter {

    fun exportAndShareCsv(
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
            val csvContent = buildCsvString(cars, currencyCode)
            val dateStr = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = if (isTr) "Diecast_Koleksiyon_$dateStr.csv" else "Diecast_Collection_$dateStr.csv"
            
            val cacheDir = context.cacheDir
            val file = File(cacheDir, fileName)
            file.writeText(csvContent, Charsets.UTF_8)

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_SUBJECT, if (isTr) "Diecast Koleksiyon Envanteri ($fileName)" else "Diecast Collection Inventory ($fileName)")
                putExtra(Intent.EXTRA_TEXT, if (isTr) "Diecast model araba koleksiyonu envanter yedek CSV dosyası (${cars.size} model)." else "Diecast model car collection inventory backup CSV file (${cars.size} models).")
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooserTitle = if (isTr) "CSV Dosyasını Dışa Aktar / Paylaş" else "Export / Share CSV File"
            val chooser = Intent.createChooser(shareIntent, chooserTitle).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            context.startActivity(chooser)

            val successMsg = if (isTr) {
                "${cars.size} model CSV dosyası olarak dışa aktarıldı."
            } else {
                "${cars.size} models exported as CSV file."
            }
            Toast.makeText(context, successMsg, Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            val errorMsg = if (isTr) {
                "Dışa aktarma hatası: ${e.localizedMessage}"
            } else {
                "Export error: ${e.localizedMessage}"
            }
            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
        }
    }

    private fun buildCsvString(cars: List<DiecastCar>, currencyCode: String): String {
        val sb = StringBuilder()
        
        // UTF-8 BOM for Excel auto-encoding detection (Turkish characters support)
        sb.append("\uFEFF")

        // Headers
        val isTr = LocaleHelper.isTurkishStatic()
        val headers = if (isTr) {
            listOf(
                "ID",
                "Araba Markası",
                "Model",
                "Üretici Marka",
                "Seri / Koleksiyon",
                "Ölçek",
                "Model Yılı",
                "Üretim Yılı",
                "Paket & Hasar Durumu",
                "Renk",
                "Alış Fiyatı ($currencyCode)",
                "Tahmini Değer ($currencyCode)",
                "Barkod",
                "İstek Listesi mi",
                "Favori mi",
                "Notlar",
                "Etiketler / Sınıflandırma",
                "Eklenme Tarihi"
            )
        } else {
            listOf(
                "ID",
                "Car Brand",
                "Model",
                "Manufacturer",
                "Series",
                "Scale",
                "Model Year",
                "Production Year",
                "Condition",
                "Color",
                "Purchase Price ($currencyCode)",
                "Estimated Value ($currencyCode)",
                "Barcode",
                "Is Wishlist",
                "Is Favorite",
                "Notes",
                "Tags / Classification",
                "Created At"
            )
        }

        sb.append(headers.joinToString(",") { escapeCsvCell(it) }).append("\n")

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        for (car in cars) {
            val dateStr = dateFormat.format(Date(car.createdAt))
            val isWishlistStr = if (isTr) (if (car.isWishlist) "Evet" else "Hayır") else (if (car.isWishlist) "Yes" else "No")
            val isFavoriteStr = if (isTr) (if (car.isFavorite) "Evet" else "Hayır") else (if (car.isFavorite) "Yes" else "No")

            val row = listOf(
                car.id.toString(),
                car.carBrand,
                car.model,
                car.manufacturer,
                car.series,
                car.scale,
                car.modelYear,
                car.productionYear,
                car.condition,
                car.color,
                String.format(Locale.US, "%.2f", car.purchasePrice),
                String.format(Locale.US, "%.2f", car.estimatedValue),
                car.barcode,
                isWishlistStr,
                isFavoriteStr,
                car.notes,
                car.tags,
                dateStr
            )

            sb.append(row.joinToString(",") { escapeCsvCell(it) }).append("\n")
        }

        return sb.toString()
    }

    private fun escapeCsvCell(value: String): String {
        var str = value
        if (str.contains("\"") || str.contains(",") || str.contains("\n") || str.contains("\r")) {
            str = str.replace("\"", "\"\"")
            return "\"$str\""
        }
        return str
    }
}
