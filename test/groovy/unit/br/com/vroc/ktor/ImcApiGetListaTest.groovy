package unit.br.com.vroc.ktor

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import spock.lang.Specification

import static groovyx.net.http.ContentType.JSON

class ImcApiGetListaTest extends Specification {

    RESTClient cliente
    def urlBase = 'http://localhost:8080'
    def uriEndpoint = '/imcs/'

    def setup() {
        this.cliente = new RESTClient(this.urlBase, JSON)
        this.cliente.auth.basic"test", "password"
    }

    def 'deveria recuperar lista de todos os IMCs'() {
        when:
        def response = this.cliente.get(path: this.uriEndpoint)

        then: 'Validando os cabeçalhos da resposta'
        response.status == HttpStatus.SC_OK
        response.getEntity().contentType.value.startsWith(JSON.toString())

        and: 'Validando o corpo da resposta'
        response.data.size() > 0
        for (item in response.data) {
            item.id instanceof Number
            item.nome instanceof String
            item.imc instanceof Number
            item.condicao instanceof String
        }
    }

    def 'deveria filtrar corretamente os IMCs'() {

        when: 'Somente parâmetro "apartir" impossível de existir'
        def response = this.cliente.get(path: this.uriEndpoint, query: [apartir: Integer.MAX_VALUE])

        then: 'Validando os headers da resposta'
        response.status == HttpStatus.SC_NO_CONTENT

        and: 'Validando o corpo da resposta'
        response.data == null

        when: 'Somente parâmetro "ate" impossível de existir'
        response = this.cliente.get(path: this.uriEndpoint, query: [ate: Integer.MIN_VALUE])

        then: 'Validando os headers da resposta'
        response.status == HttpStatus.SC_NO_CONTENT

        and: 'Validando o corpo da resposta'
        response.data == null

        when: 'Parâmetros "apartir" e "ate" com valores extremos possíveis'
        response = this.cliente.get(path: this.uriEndpoint, query: [apartir: Integer.MIN_VALUE, ate: Integer.MAX_VALUE])

        then: 'Validando os headers da resposta'
        response.status == HttpStatus.SC_OK
        response.getEntity().contentType.value.startsWith(JSON.toString());

        and: 'Validando o corpo da resposta'
        response.data.size() > 0 // número de itens na lista de JSONs
        for (item in response.data) {
            item.id instanceof Number
            item.nome instanceof String
            item.imc instanceof Number
            item.condicao instanceof String
        }

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
