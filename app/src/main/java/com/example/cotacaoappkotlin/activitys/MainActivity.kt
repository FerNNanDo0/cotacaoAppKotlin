package com.example.cotacaoappkotlin.activitys

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.cotacaoappkotlin.R
import com.example.cotacaoappkotlin.api.Cotacao
import com.example.cotacaoappkotlin.api.CotacaoAPI
import com.example.cotacaoappkotlin.viewmodel.ModelView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    lateinit var textViewBitcoin: TextView
    lateinit var textViewEthereum: TextView
    lateinit var btnView: Button
    lateinit var progressBar: ProgressBar

    private val owner = this

    var oldBitcoin: String = ""
    var oldEthereum: String = ""

    /*
    https://www.mercadobitcoin.net/api/BTC/ticker/
    https://www.mercadobitcoin.net/api/ETH/ticker/
     */

    // uzando retrofit para requests
    val cotacaoAPI: CotacaoAPI by lazy{
        Retrofit.Builder()
            .baseUrl("https://www.mercadobitcoin.net/api/")
            .addConverterFactory( GsonConverterFactory.create() )
            .build()
            .create( CotacaoAPI::class.java )
    }

    fun buscarCripto(resposta: Response<Cotacao>): Cotacao? {

        // verificando se foi possivel recuperar dados do bitcoin
        if (resposta.isSuccessful) {

            // recuperando o corpo da cotacao
            val cotacao = resposta.body()

            // verifica se a cotacao nao esta null
            if (cotacao != null) {
                return cotacao
            }
        }
        return null
    }

    @SuppressLint("ResourceAsColor")
    fun iniciarComponentes(){
        btnView = findViewById(R.id.btnRecuperar)

        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.GONE

        textViewEthereum = findViewById(R.id.TextViewEthereum)
        textViewBitcoin = findViewById(R.id.TextViewBitcoin)
    }

    fun buscqueCriptos(modelBitcoin: ModelView, modelEthereum: ModelView){
        // ativar progress
        progressBar.visibility = View.VISIBLE

        // iniciando corrotina e usando THREAD IO
        CoroutineScope( Dispatchers.IO ).launch {
            /// RECUPERA DADOS BITCOIN
            val cotacaoBTC = buscarCripto( cotacaoAPI.recuperarCotacaoBitcoin() )
            //oldBitcoin = "${cotacaoBTC?.ticker?.last}"

            /// RECUPERA DADOS DO ETHEREUM
            val cotacaoETH = buscarCripto( cotacaoAPI.recuperarCotacaoEthereum() )
            //oldEthereum = "${cotacaoETH?.ticker?.last}"

            // trocando de THREAD, saindo da IO para a MAIN
            withContext(Dispatchers.Main) {

                val criptBitcoin = DecimalFormat("#.##").format( "${cotacaoBTC?.ticker?.last}".toDouble() )
                //String.format( "%.2f", "${cotacaoBTC?.ticker?.last}".toDouble() )
                oldEthereum = criptBitcoin

                // model view 1 Bitcoin
                modelBitcoin.setCurrentValueBitcoin( criptBitcoin )

                val criptEthereum = DecimalFormat("#.##").format( "${cotacaoETH?.ticker?.last}".toDouble() )
                    //String.format( "%.2f", "${cotacaoETH?.ticker?.last}".toDouble() )
                oldEthereum = criptEthereum

                // model view 2 Ethereum
                modelEthereum.setCurrentValueEthereum( criptEthereum )
                progressBar.visibility = View.INVISIBLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        iniciarComponentes()

        // init viewModel
        val modelBitcoin : ModelView = ViewModelProvider(this)[ModelView::class.java]
        val modelEthereum : ModelView  = ViewModelProvider(this)[ModelView::class.java]

        // click do button
        btnView.setOnClickListener {

            // buscar cript pela primeira vez
            buscqueCriptos(modelBitcoin, modelEthereum)

            // precisa de permissao para acesso a internet
            // iniciando corrotina e usando THREAD IO
            CoroutineScope( Dispatchers.IO ).launch {

                while(true){
                    /// RECUPERA DADOS BITCOIN
                    val cotacaoBTC = buscarCripto( cotacaoAPI.recuperarCotacaoBitcoin() )

                    /// RECUPERA DADOS DO ETHEREUM
                    val cotacaoETH = buscarCripto( cotacaoAPI.recuperarCotacaoEthereum() )

                    // verificar se mudou os valores dos cripto
                    if( !oldBitcoin.equals( "${cotacaoBTC?.ticker?.last}" )
                        || !oldEthereum.equals(  "${cotacaoETH?.ticker?.last}" ) ){

                        // trocando de THREAD, saindo da IO para a MAIN
                        withContext(Dispatchers.Main) {
                            progressBar.visibility = View.VISIBLE

                            val criptBitcoin = DecimalFormat("#.##").format( "${cotacaoBTC?.ticker?.last}".toDouble() )
                            //String.format( "%.2f", "${cotacaoBTC?.ticker?.last}".toDouble() )

                            // model view 1 Bitcoin
                            modelBitcoin.setCurrentValueBitcoin( criptBitcoin.toString() )


                            val criptEthereum = DecimalFormat("#.##").format( "${cotacaoETH?.ticker?.last}".toDouble() )
                            // String.format( "%.2f", "${cotacaoETH?.ticker?.last}".toDouble() )
                            // model view 2 Ethereum
                            modelEthereum.setCurrentValueEthereum( criptEthereum )
                        }
                    }
                    // pause de 5 segundos
                    //sleep( 5000)
                }
                /////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                }// fim da corrotina

            }// fim button

        // modelView1 Bitcoin
        val criptBitcoinObserver = Observer<String>{ criptBtc ->

            oldBitcoin = criptBtc
            textViewBitcoin.text = "R$ ${criptBtc}"
        }
        modelBitcoin.currentBitcoin.observe(owner, criptBitcoinObserver)

        // modelView2 Ethereum
        val criptEthereumbserver = Observer<String> { criptEth ->

            oldEthereum = criptEth
            textViewEthereum.text = "R$ ${criptEth}"

            // ativar progress
            progressBar.visibility = View.INVISIBLE

            // desativar btn
            btnView.isClickable = false
            btnView.visibility = View.GONE
        }
        modelEthereum.currentEthereum.observe(owner, criptEthereumbserver)


    }




//                // chamando o metodo recuperar bitcoin
//                val responstaBTC = CotacaoAPI.recuperarCotacaoBitcoin()
//
//                // verificando se foi possivel recuperar dados do bitcoin
//                if ( responstaBTC.isSuccessful ){
//
//                    // recuperando o corpo da cotacao
//                    val cotacao = responstaBTC.body()
//
//                    // verifica se a cotacao nao esta null
//                    if ( cotacao != null ){
//
//                        // trocando de THREAD, saindo da IO para a MAIN
//                        withContext( Dispatchers.Main ){
//                            textViewBitcoin.text = "Bitcoin R$ ${cotacao.ticker.last}"
//                        }
//
//                    }
//
//                }

    }