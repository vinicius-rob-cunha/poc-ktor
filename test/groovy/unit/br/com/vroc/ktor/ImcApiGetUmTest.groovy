package unit.br.com.vroc.ktor

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import spock.lang.Specification

import static groovyx.net.http.ContentType.JSON

class ImcApiGetUmTest extends Specification {

    RESTClient cliente
    def urlBase = 'http://localhost:8080'
    def uriEndpoint = '/imcs/1'

    private def criarPedido() {
        [nome:'Ze Magrinho', altura:1.75, peso: 71, sexo: 'MASCULINO']
    }

    def setup() {
        this.cliente = new RESTClient(this.urlBase, JSON)
        this.cliente.auth.basic"test", "password"
    }

    def 'deveria recuperar um IMC'() {
        when:
        def response = this.cliente.get(path: this.uriEndpoint)

        then: 'Validando os cabeçalhos da resposta'
        response.status == HttpStatus.SC_OK
        response.getEntity().contentType.value.startsWith(JSON.toString())

        and: 'Validando o corpo da resposta'
        response.data.size() == 4
        response.data.id == this.uriEndpoint.split('/').last().toInteger()
        response.data.nome instanceof String
        response.data.imc instanceof Number
        response.data.condicao instanceof String
    }

    def 'deveria receber 404 ao tentar recuperar um IMC informando um identificador inexistente'() {
        when:
        this.cliente.get(path: '/imcs/-19')

        then:
        HttpResponseException ex = thrown(HttpResponseException)
        ex.statusCode == HttpStatus.SC_NOT_FOUND
    }

    def 'deveria receber 401 ao tentar recuperar um IMC sem informar a chave da API ou com credenciais invalidas'() {
        given:
        def clienWithoutAuth = new RESTClient(this.urlBase, JSON)

        when:
        clienWithoutAuth.get(path: this.uriEndpoint)

        then:
        HttpResponseException ex = thrown(HttpResponseException)
        ex.statusCode == HttpStatus.SC_UNAUTHORIZED

        when:
        clienWithoutAuth.auth.basic "wrongUser", "wrong"
        clienWithoutAuth.get(path: this.uriEndpoint)

        then:
        ex = thrown(HttpResponseException)
        ex.statusCode == HttpStatus.SC_UNAUTHORIZED
    }

}
