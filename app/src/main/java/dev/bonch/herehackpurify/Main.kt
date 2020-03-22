package dev.bonch.herehackpurify

import dev.bonch.herehackpurify.model.pojo.*

object Main {
    lateinit var binAddressText: String
    lateinit var pointAddressText: String
    lateinit var client: Client
    lateinit var cleaner: Cleaner
    lateinit var bin: Bin
    lateinit var point: Point
    var isSecond = false
    lateinit var order: Order
    var isSetPoint = false
}