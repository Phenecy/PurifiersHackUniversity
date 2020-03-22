package dev.bonch.herehackpurify.model.network

import dev.bonch.herehackpurify.model.pojo.Client
import retrofit2.Call
import retrofit2.http.*


interface JsonApi {
    @GET("/client")
    fun getAllClients(): Call<ArrayList<Client>>

    @GET("/client/{id}")
    fun getClient(@Path("id") id: Long): Call<ArrayList<Client>>

    @GET("/client/phone")
    fun getClientByPhone(@Query("phone") phone: String): Call<Client>

    @POST("/client")
    fun createClient(
            @Body body: Client
    ): Call<Client>


}
