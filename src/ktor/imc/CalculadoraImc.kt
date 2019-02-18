package br.com.vroc.ktor.imc

import br.com.vroc.ktor.enums.FaixaImc.Companion.getFaixa

class CalculadoraImc {

    fun calcularImc(peso: Double, altura: Double): Double =
        when {
            peso <= 0 -> throw IllegalArgumentException("Peso inválido: $peso")
            altura <= 0 -> throw IllegalArgumentException("Altura inválida: $altura")
            else -> peso / (altura * altura)
        }

    fun verificarCondicaoImc(pedido: PedidoImc): ResultadoImc {
        val resultado = ResultadoImc(pedido.nome)

        try {
            val imc = calcularImc(pedido.peso, pedido.altura)
            resultado.imc = imc
            resultado.condicao = getFaixa(imc, pedido.sexo).descricao
        } catch (e: IllegalArgumentException) {
            resultado.condicao = "Impossível calcular IMC: ${e.message}"
        }

        return resultado
    }

}