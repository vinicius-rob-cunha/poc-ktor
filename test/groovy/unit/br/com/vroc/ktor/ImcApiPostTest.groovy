package unit.br.com.vroc.ktor

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import spock.lang.Specification

import static groovyx.net.http.ContentType.JSON

class ImcApiPostTest extends Specification {

    RESTClient cliente
    def urlBase = 'http://localhost:8080'
    def uriEndpoint = '/imcs/calculo'

    private def criarPedido() {
        [nome:'Ze Magrinho', altura:1.75, peso: 71, sexo: 'MASCULINO']
    }

    private def criarHeader() {
        ['x-api-key' : 'chave-valida']
    }

    def setup() {
        this.cliente = new RESTClient(this.urlBase, JSON)
    }

    def 'deveria calcular e registrar IMC'() {
        given:
        def pedido = this.criarPedido()

        when:
        def response = this.cliente.post(path: this.uriEndpoint, headers: this.criarHeader(), body: pedido)

        then: 'Validando os cabeçalhos da resposta'
        response.status == HttpStatus.SC_CREATED
        response.getEntity().contentType.value.startsWith(JSON.toString())
        response.headers.Location.split("/").last().toInteger()

        and: 'Validando o corpo da resposta'
        response.data.size() == 4
        response.data.id instanceof Number
        response.data.nome == pedido.nome
        response.data.imc instanceof Number
        response.data.condicao instanceof String
    }

    def 'deveria receber 400 ao tentar criar IMC com JSON ausente ou inválido'() {
        when: 'Requisição sem JSON no corpo'
        this.cliente.post(path: this.uriEndpoint, headers: this.criarHeader())

        then:
        HttpResponseException ex = thrown(HttpResponseException)
        ex.statusCode == HttpStatus.SC_BAD_REQUEST

        when: 'Requisição com JSON inválido'
        this.cliente.post(path: this.uriEndpoint,
                          headers : this.criarHeader(),
                          body: [nome:888, altura:'toma', peso: 'olha', sexo: 1])

        then:
        ex = thrown(HttpResponseException)
        ex.statusCode == HttpStatus.SC_BAD_REQUEST
    }

    def 'deveria receber 405 ao tentar usar PUT ao invés de POST no cálculo de IMC'() {
        when:
        this.cliente.put(path: this.uriEndpoint, headers: this.criarHeader(), body: this.criarPedido())

        then:
        HttpResponseException ex = thrown(HttpResponseException)
        ex.statusCode == HttpStatus.SC_METHOD_NOT_ALLOWED
    }

}
