package br.com.vroc.ktor.persistence

import javax.persistence.Column
import javax.persistence.Entity

@Entity
data class ImcAtual (
    @Column(length = 50)
    var nome: String = "",

    @Column
    var imc: Double = 0.0
)