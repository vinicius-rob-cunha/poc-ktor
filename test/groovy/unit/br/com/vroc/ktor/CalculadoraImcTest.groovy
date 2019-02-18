package unit.br.com.vroc.ktor

import br.com.vroc.ktor.imc.PedidoImc
import br.com.vroc.ktor.imc.CalculadoraImc
import spock.lang.Specification

import static br.com.vroc.ktor.enums.Sexo.*
import static br.com.vroc.ktor.enums.FaixaImc.*

class CalculadoraImcTest extends Specification {

    def 'Ex.: 1 - lancar exceção com peso invalido'() {
        when:
        boolean houveExcecao
        try {
            new CalculadoraImc().calcularImc(0, 1.70)
            houveExcecao = false
        } catch (IllegalArgumentException ignored) {
            houveExcecao = true
        }

        then:
        houveExcecao
    }

    def 'Ex.: 2 - lancar exceção com peso invalido'() {
        when:
        new CalculadoraImc().calcularImc(0, 1.70)

        then:
        thrown(IllegalArgumentException) //thrown só pode ser usado em 'then'
    }

    def 'Ex.: 3 - lancar exceção com peso invalido'() {
        when:
        new CalculadoraImc().calcularImc(0, 1.70)

        then:
        thrown(IllegalArgumentException)

        when:
        new CalculadoraImc().calcularImc(-1, 1.70)

        then:
        thrown(IllegalArgumentException)
    }

    def 'Ex.: 4 - lancar exceção c/ mensagem correta p/ peso inválido'() {
        when:
        def peso = -1
        new CalculadoraImc().calcularImc(peso, 1.70)

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == "Peso inválido: ${peso.toDouble()}"
    }

    def 'Ex.: Mock | 1 - deveria ser um homem acima do peso'() {
        given:
        def pedido = Mock(PedidoImc)
        pedido.getNome() >> {
            if (pedido.getSexo() == MASCULINO) {
                "Zé Fofinho"
            } else {
                "Maria Fofinha"
            }
        }
        pedido.getSexo() >> FEMININO
        pedido.getPeso() >> 72
        pedido.getAltura() >> 1.50

        /*
        //_ significa que pode vir qualquer valor
        pedido.getNome(_) >> "Sr. Zé Importante"
        pedido.getNome(_, _) >> "Sr. Zé Imitador, vulgo 'ator'"
        pedido.getNome(_, 'alpinista') >> "Sr. Zé Luiz, vulgo 'alpinista'"
        pedido.getNome('Sr.', _) >> "Sr. Zé Boleiro, vulgo 'messi'"
        */

        when:
        def resultado = new CalculadoraImc().verificarCondicaoImc(pedido)

        then:
        resultado.imc == 32
        resultado.condicao == ACIMA.descricao
    }

}
