package ru.bz.warehousemobileapplication.data.api


import retrofit2.Response
import retrofit2.http.GET
import ru.bz.warehousemobileapplication.data.dto.WarehouseDto


interface ApiService {
    @GET("warehouse/")
    suspend fun getRandomUsefulActivity() : Response<List<WarehouseDto>>
}