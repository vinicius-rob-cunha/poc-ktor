package br.com.vroc.ktor.service

import br.com.vroc.ktor.imc.ResultadoImc
import br.com.vroc.ktor.persistence.ImcAtual
import javax.persistence.EntityManager
import javax.persistence.NoResultException

class ImcAtualService {

    companion object {
        const val CONSULTA_IMC = "SELECT i FROM ImcAtual i WHERE nome = ?0"
    }

    private lateinit var entityManager: EntityManager

    private fun imcExistente(nome: String): ImcAtual? =
        try {
            this.entityManager
                .createQuery(CONSULTA_IMC, ImcAtual::class.java)
                .setParameter(0, nome)
                .singleResult
        } catch (e: NoResultException) {
            null
        }

    fun registrarImc(resultados: List<ResultadoImc>): Int {
        var inseridos = 0

        for (resultado in resultados) {
            val imcExistente = this.imcExistente(resultado.nome)

            if (imcExistente != null) {
                imcExistente.imc = resultado.imc
                continue
            }

            val imcAtual = ImcAtual(
                resultado.nome,
                resultado.imc
            )

            this.entityManager.persist(imcAtual)
            inseridos++
        }

        return inseridos
    }

}