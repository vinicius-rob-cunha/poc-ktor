package br.com.vroc.ktor.service

import org.apache.commons.mail.Email

class EnvioEmailService(val email: Email) {

    private var enviados: Int = 0

    fun enviarEmails(assunto: String, conteudo: String, destinatarios: List<String>) {
        this.email.subject = assunto
        this.email.setMsg(conteudo)
        this.email.addTo(*destinatarios.toTypedArray())
        this.email.send()

        this.enviados++
    }

}