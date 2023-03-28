package com.example.cotacaoappkotlin.api

import retrofit2.Response
import retrofit2.http.GET

interface CotacaoAPI {

    //  url base  https://www.mercadobitcoin.net/api/
    @GET("BTC/ticker")
    suspend fun recuperarCotacaoBitcoin() : Response<Cotacao>


    // base url   https://www.mercadobitcoin.net/api/
    @GET("ETH/ticker")
    suspend fun recuperarCotacaoEthereum() : Response<Cotacao>


}