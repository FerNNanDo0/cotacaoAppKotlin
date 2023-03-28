package com.example.cotacaoappkotlin.api

data class Ticker(
    val buy: String,
    val date: Int,
    val high: String,
    val last: String,
    val low: String,
    val `open`: String,
    val sell: String,
    val vol: String
)