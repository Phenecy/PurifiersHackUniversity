package dev.bonch.herehackpurify.model.pojo

data class Order(
    val accept: Boolean,
    val cleanerId: Int,
    val clientId: Int,
    val id: Int,
    val price: Int
)