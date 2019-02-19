package unit.br.com.vroc.ktor

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import spock.lang.Specification

import static groovyx.net.http.ContentType.JSON

class ImcApiDeleteTest extends Specification {

    RESTClient cliente
    def urlBase = 'http://localhost:8080'
    def uriEndpoint = '/imcs/1'

    def setup() {
        this.cliente = new RESTClient(this.urlBase, JSON)
        this.cliente.auth.basic"test", "password"
    }

    def 'deveria excluir um registro de IMC'() {
        when:
        def response = this.cliente.delete(path: this.uriEndpoint)

        then: 'Validando os cabeçalhos da resposta'
        response.status == HttpStatus.SC_OK
    }

    def 'deveria receber 404 ao tentar recuperar um IMC informando um identificador inexistente'() {
        when:
        this.cliente.delete(path: '/imcs/-19')

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
