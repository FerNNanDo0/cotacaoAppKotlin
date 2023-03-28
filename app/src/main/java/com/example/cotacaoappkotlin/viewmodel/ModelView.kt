package com.example.cotacaoappkotlin.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ModelView: ViewModel() {

    val currentBitcoin: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }

    val currentEthereum: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }

    fun setCurrentValueBitcoin( txt: String ){
        currentBitcoin.value = txt
    }

    fun setCurrentValueEthereum( txt: String ){
        currentEthereum.value = txt
    }
}