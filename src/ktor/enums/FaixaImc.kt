package br.com.vroc.ktor.enums

enum class FaixaImc(val descricao: String,
                    val limiteMaximoFeminino: Int,
                    val limiteMaximoMasculino: Int) {

    ABAIXO("Abaixo do peso", 19, 21),
    NORMAL("Peso normal", 26, 27),
    ACIMA("Acima do peso", 100, 100);

    companion object {

        @JvmStatic
        fun getFaixa(imc: Double, sexo: Sexo): FaixaImc {
            var retorno = values()[0]

            for(i in 1 until values().size) {
                val atual = values()[i]
                val anterior = values()[i-1]

                val limiteInferior =
                    if(sexo == Sexo.FEMININO)
                        anterior.limiteMaximoFeminino
                    else
                        anterior.limiteMaximoMasculino

                val limiteSuperior =
                    if(sexo == Sexo.FEMININO)
                        atual.limiteMaximoFeminino
                    else
                        atual.limiteMaximoMasculino

                if(imc >= limiteInferior && imc < limiteSuperior)
                    retorno = atual
            }

            return retorno
        }

    }

}