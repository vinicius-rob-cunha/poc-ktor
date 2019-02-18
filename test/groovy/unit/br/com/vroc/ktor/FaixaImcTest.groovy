package unit.br.com.vroc.ktor

import br.com.vroc.ktor.enums.FaixaImc
import spock.lang.Specification

import static br.com.vroc.ktor.enums.FaixaImc.*
import static br.com.vroc.ktor.enums.Sexo.FEMININO
import static br.com.vroc.ktor.enums.Sexo.MASCULINO

class FaixaImcTest extends Specification {

    def 'Ex.: 1 - IMC deve estar na faixa correta.'() {
        expect:
        getFaixa(18, FEMININO) == ABAIXO
        getFaixa(21, FEMININO) == NORMAL
        getFaixa(27, FEMININO) == ACIMA
        getFaixa(20, MASCULINO) == ABAIXO
        getFaixa(23, MASCULINO) == NORMAL
        getFaixa(28, MASCULINO) == ACIMA
    }

    def 'Ex.: 2 - IMC deve estar na faixa correta.'() {
        setup:
        def cenarios = [
            [imc: 18, sexo: FEMININO, condicao: ABAIXO],
            [imc: 21, sexo: FEMININO, condicao: NORMAL],
            [imc: 27, sexo: FEMININO, condicao: ACIMA],
            [imc: 20, sexo: MASCULINO, condicao: ABAIXO],
            [imc: 23, sexo: MASCULINO, condicao: NORMAL],
            [imc: 28, sexo: MASCULINO, condicao: ACIMA]
        ]

        expect:
        cenarios.each{
            assert getFaixa(it.imc, it.sexo) == it.condicao
        }
    }

    def 'Ex.: 3 - IMC deve estar na faixa correta'() {
        expect:
        getFaixa(imc, sexo) == resultado

        where:
        imc | sexo      || resultado
        18  | FEMININO  || ABAIXO
        21  | FEMININO  || NORMAL
        27  | FEMININO  || ACIMA
        20  | MASCULINO || ABAIXO
        23  | MASCULINO || NORMAL
        28  | MASCULINO || ACIMA
    }

//    @Unroll Com essa anotação poderíamos escrever o nome do teste como "IMC #imc deve estar na faixa #resultado para o sexo #sexo"
//    Com isso, caso o teste falhe, a variaveis com # será substituida pelo respectivo valor
    def 'Ex.: 4 - IMC deve estar na faixa correta'() {
        expect:
        getFaixa(imc, sexo) == resultado

        where:
        imc   | sexo      || resultado
        18    | FEMININO  || ABAIXO
        21    | FEMININO  || NORMAL
        27    | FEMININO  || ACIMA
        20    | MASCULINO || ABAIXO
        23    | MASCULINO || NORMAL
        28    | MASCULINO || ACIMA
        18.99 | FEMININO  || ABAIXO
        19    | FEMININO  || NORMAL
        25.99 | FEMININO  || NORMAL
        26    | FEMININO  || ACIMA
        20.99 | MASCULINO || ABAIXO
        21    | MASCULINO || NORMAL
        26.99 | MASCULINO || NORMAL
        27    | MASCULINO || ACIMA
    }

    def 'Ex.: 5 - GWT - IMC deve estar na faixa correta'() {
        when:
        def calculo = new Object() {
            def getFaixa(imc, sexo) {
                FaixaImc.getFaixa(imc, sexo)
            }
        }

        then:
        calculo.getFaixa(imc, sexo) == resultado

        where:
        imc | sexo     || resultado
        18  | FEMININO || ABAIXO
        21  | FEMININO || NORMAL
        27  | FEMININO || ACIMA
    }

}