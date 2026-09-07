package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DiecastDao {

    @Query("SELECT * FROM diecast_cars ORDER BY createdAt DESC")
    fun getAllCars(): Flow<List<DiecastCar>>

    @Query("SELECT * FROM diecast_cars WHERE isWishlist = 0 ORDER BY createdAt DESC")
    fun getCollectionCars(): Flow<List<DiecastCar>>

    @Query("SELECT * FROM diecast_cars WHERE isWishlist = 1 ORDER BY createdAt DESC")
    fun getWishlistCars(): Flow<List<DiecastCar>>

    @Query("SELECT * FROM diecast_cars WHERE id = :id LIMIT 1")
    fun getCarById(id: Int): Flow<DiecastCar?>

    @Query("SELECT * FROM diecast_cars WHERE id = :id LIMIT 1")
    suspend fun getCarByIdDirect(id: Int): DiecastCar?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car: DiecastCar): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCars(cars: List<DiecastCar>)

    @Update
    suspend fun updateCar(car: DiecastCar)

    @Delete
    suspend fun deleteCar(car: DiecastCar)

    @Query("DELETE FROM diecast_cars WHERE id = :id")
    suspend fun deleteCarById(id: Int)

    @Query("""
        SELECT * FROM diecast_cars 
        WHERE manufacturer LIKE '%' || :query || '%' 
           OR carBrand LIKE '%' || :query || '%' 
           OR model LIKE '%' || :query || '%' 
           OR series LIKE '%' || :query || '%' 
           OR color LIKE '%' || :query || '%' 
           OR barcode LIKE '%' || :query || '%'
           OR notes LIKE '%' || :query || '%'
           OR tags LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    fun searchCars(query: String): Flow<List<DiecastCar>>

    @Query("SELECT COUNT(*) FROM diecast_cars")
    suspend fun getCarCount(): Int

    @Query("DELETE FROM diecast_cars")
    suspend fun deleteAllCars()
}
