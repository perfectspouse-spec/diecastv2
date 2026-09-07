package com.example.data

import kotlinx.coroutines.flow.Flow

class DiecastRepository(private val dao: DiecastDao) {

    val allCars: Flow<List<DiecastCar>> = dao.getAllCars()
    val collectionCars: Flow<List<DiecastCar>> = dao.getCollectionCars()
    val wishlistCars: Flow<List<DiecastCar>> = dao.getWishlistCars()

    fun getCarById(id: Int): Flow<DiecastCar?> = dao.getCarById(id)

    suspend fun getCarByIdDirect(id: Int): DiecastCar? = dao.getCarByIdDirect(id)

    suspend fun insertCar(car: DiecastCar): Long = dao.insertCar(car)

    suspend fun insertCars(cars: List<DiecastCar>) = dao.insertCars(cars)

    suspend fun updateCar(car: DiecastCar) = dao.updateCar(car)

    suspend fun deleteCar(car: DiecastCar) = dao.deleteCar(car)

    suspend fun deleteCarById(id: Int) = dao.deleteCarById(id)

    fun searchCars(query: String): Flow<List<DiecastCar>> = dao.searchCars(query)

    suspend fun getCarCount(): Int = dao.getCarCount()

    suspend fun deleteAllCars() = dao.deleteAllCars()
}
