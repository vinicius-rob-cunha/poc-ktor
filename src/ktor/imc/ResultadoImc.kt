package br.com.vroc.ktor.imc

import com.fasterxml.jackson.annotation.JsonFormat

data class ResultadoImc(var nome: String = "",
                        @JsonFormat(pattern = "")
                        var imc: Double = 0.0) {

    var id: Int = 0
    lateinit var condicao: String

}