package dev.bonch.herehackpurify.model.pojo

import com.google.gson.annotations.SerializedName

data class Client(
    @SerializedName("id") val id : Int = 0,
    @SerializedName("firstName") val firstName : String = "Empty",
    @SerializedName("lastName") val lastName : String = "Empty",
    @SerializedName("phone") val phone : String = "Empty",
    @SerializedName("age") val age : Int = 0,
    @SerializedName("city") val city : String = "Empty",
    @SerializedName("balance") val balance : Int = 0,
    @SerializedName("trashBinId") val trashBinId : Int = 0,
    @SerializedName("trashPointId") val trashPointId : Int = 0,
    @SerializedName("subscribed") val subscribed : Boolean = false,
    @SerializedName("confirmed") val confirmed : Boolean = false
)
