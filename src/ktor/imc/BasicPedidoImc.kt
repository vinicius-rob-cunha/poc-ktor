package br.com.vroc.ktor.imc

import br.com.vroc.ktor.enums.Sexo

data class BasicPedidoImc(override var nome: String,
                          override var altura: Double,
                          override var peso: Double,
                          override var sexo: Sexo) : PedidoImc