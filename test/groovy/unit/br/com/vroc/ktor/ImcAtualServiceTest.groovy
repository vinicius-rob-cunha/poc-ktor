package unit.br.com.vroc.ktor

import br.com.vroc.ktor.imc.ResultadoImc
import br.com.vroc.ktor.persistence.ImcAtual
import br.com.vroc.ktor.service.ImcAtualService
import spock.lang.Specification

import javax.persistence.EntityManager
import javax.persistence.TypedQuery

import static br.com.vroc.ktor.service.ImcAtualService.*

class ImcAtualServiceTest extends Specification {

    String nomeEncontrado = 'Zé Cadastrado'
    String nomeNaoEncontrado1 = 'João Não Cadastrado'
    String nomeNaoEncontrado2 = 'Maria Não Cadastrada'

    EntityManager entityManager
    ImcAtualService service

    def setup() {
        this.entityManager = Mock(EntityManager)

        def resultadoEncontrado = Mock(TypedQuery)
        def resultadoNaoEncontrado = Mock(TypedQuery)
        def instrucaoConsulta = Mock(TypedQuery)

        resultadoEncontrado.getSingleResult() >> new ImcAtual(nome: this.nomeEncontrado)
        instrucaoConsulta.setParameter(0, this.nomeEncontrado) >> resultadoEncontrado
        instrucaoConsulta.setParameter(0, this.nomeNaoEncontrado1) >> resultadoNaoEncontrado
        instrucaoConsulta.setParameter(0, this.nomeNaoEncontrado2) >> resultadoNaoEncontrado

        this.entityManager.createQuery(CONSULTA_IMC, ImcAtual.class) >> instrucaoConsulta
        this.service = new ImcAtualService(entityManager: this.entityManager)
    }

    def 'deveria retornar null ou o ImcAtual por nome'() {
        expect:
        !this.service.imcExistente(this.nomeNaoEncontrado1)
        !this.service.imcExistente(this.nomeNaoEncontrado2)
        this.service.imcExistente(this.nomeEncontrado)?.getNome() == this.nomeEncontrado
    }

    def 'deveria inserir somente se ainda não existe'() {
        when:
        def resultadosE2 = [
            new ResultadoImc(nome:nomeEncontrado, imc: 25.0),
            new ResultadoImc(nome:nomeNaoEncontrado1, imc: 29.0),
            new ResultadoImc(nome:nomeNaoEncontrado2, imc: 35.0)
        ]
        def resultadosE1 = [
            new ResultadoImc(nome:nomeEncontrado, imc: 25.0),
            new ResultadoImc(nome:nomeNaoEncontrado1, imc: 29.0)
        ]
        def resultadosE0 = [
            new ResultadoImc(nome:nomeEncontrado, imc: 25.0)
        ]

        then:
        this.service.registrarImc(resultadosE2) == 2
        this.service.registrarImc(resultadosE1) == 1
        this.service.registrarImc(resultadosE0) == 0
    }

    def 'deveria criar somente como novo por nome'() {
        given:
        def resultados = [
            new ResultadoImc(nome:nomeEncontrado, imc: 25),
            new ResultadoImc(nome:nomeNaoEncontrado1, imc: 29),
            new ResultadoImc(nome:nomeNaoEncontrado2, imc: 35)
        ]

        when:
        this.service.registrarImc(resultados)

        then:
        2 * this.entityManager.persist(_)
    }

}
