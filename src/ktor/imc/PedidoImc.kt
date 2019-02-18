package br.com.vroc.ktor.imc

import br.com.vroc.ktor.enums.Sexo

interface PedidoImc {

    var nome: String
    var altura: Double
    var peso: Double
    var sexo: Sexo

//    fun getNome(pronomeTratamento: String): String
//    fun getNome(pronomeTratamento: String, apelido: String): String

}