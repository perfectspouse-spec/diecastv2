package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diecast_cars")
data class DiecastCar(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val manufacturer: String = "",  // Üretici Marka (Hot Wheels, Matchbox, Mini GT, vb.)
    val carBrand: String = "",      // Araba Markası (Porsche, Ford, BMW, Nissan, vb.)
    val model: String = "",         // Model Adı (911 GT3 RS, Skyline GT-R, vb.)
    val modelYear: String = "",     // Model Yılı (ör. 1999, 2023)
    val scale: String = "",         // Ölçek (1:64, 1:43, 1:24, 1:18, 1:32)
    val series: String = "",        // Seri Adı (Mainline, Car Culture, Boulevard, Fast & Furious)
    val productionYear: String = "",// Üretim Yılı
    val condition: String = "", // Hasar / Paket Durumu (Kutulu, Kutusuz / Açık, Hasarlı)
    val color: String = "",         // Renk
    val purchasePrice: Double = 0.0, // Alış Fiyatı (₺)
    val purchaseDate: Long = System.currentTimeMillis(), // Alış Tarihi
    val estimatedValue: Double = 0.0, // Tahmini Değer (₺)
    val photoPath: String? = null,  // Fotoğraf Yolu 1 (Ana Fotoğraf)
    val photoPath2: String? = null, // Fotoğraf Yolu 2
    val photoPath3: String? = null, // Fotoğraf Yolu 3
    val photoPath4: String? = null, // Fotoğraf Yolu 4
    val notes: String = "",         // Notlar
    val tags: String = "",          // Özel Etiketler & Sınıflandırma (Spor, Klasik, Yarış, vb.)
    val barcode: String = "",       // Barkod / QR
    val isWishlist: Boolean = false, // İstek Listesi mi Yoksa Koleksiyonda mı
    val isFavorite: Boolean = false, // Favori Model mi
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getTagList(): List<String> {
        if (tags.isBlank()) return emptyList()
        return tags.split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
    }

    fun hasTag(tag: String): Boolean {
        return getTagList().any { it.equals(tag.trim(), ignoreCase = true) }
    }

    fun getPhotoList(): List<String> {
        val list = mutableListOf<String>()
        if (!photoPath.isNullOrBlank()) {
            if (photoPath.contains(",")) {
                photoPath.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { list.add(it) }
            } else {
                list.add(photoPath)
            }
        }
        if (!photoPath2.isNullOrBlank() && !list.contains(photoPath2)) list.add(photoPath2)
        if (!photoPath3.isNullOrBlank() && !list.contains(photoPath3)) list.add(photoPath3)
        if (!photoPath4.isNullOrBlank() && !list.contains(photoPath4)) list.add(photoPath4)
        return list.take(4)
    }
}
