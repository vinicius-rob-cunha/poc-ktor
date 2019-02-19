package br.com.vroc

//import io.ktor.client.features.auth.basic.*
import br.com.vroc.ktor.imc.BasicPedidoImc
import br.com.vroc.ktor.imc.CalculadoraImc
import br.com.vroc.ktor.imc.ResultadoImc
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.basic
import io.ktor.features.*
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.path
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI
import org.slf4j.event.Level
import java.math.BigDecimal
import kotlin.reflect.KClass

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(Compression) {
        gzip {
            priority = 1.0
        }
        deflate {
            priority = 10.0
            minimumSize(1024) // condition
        }
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }

    install(Authentication) {
        basic("myBasicAuth") {
            realm = "Ktor Server"
            validate { if (it.name == "test" && it.password == "password") UserIdPrincipal(it.name) else null }
        }
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)

            val module = SimpleModule()
            module.addSerializer(Double::class.java, DoubleSerializer())
            registerModule(module)
        }
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        install(StatusPages) {
            exception<AuthenticationException> { cause ->
                call.respond(HttpStatusCode.Unauthorized)
            }
            exception<AuthorizationException> { cause ->
                call.respond(HttpStatusCode.Forbidden)
            }

        }

        authenticate("myBasicAuth") {

            post("/imcs/calculo") {
                val pedido = call.extractBody(BasicPedidoImc::class)
                val response = CalculadoraImc().verificarCondicaoImc(pedido)
                response.id = (0..100).random()

                call.response.headers.append("Location", "/imcs/calculo/${response.id}")
                call.respond(HttpStatusCode.Created, response)
            }

            get("/imcs/{id}") {
                val requestedId = call.parameters["id"]

                if(requestedId == null || requestedId.toInt() < 1)
                    throw NotFoundException("Nenhum imc encontrado com id $requestedId")

                val response = ResultadoImc(nome="Goku", imc=24.7).apply {
                    id = requestedId.toInt()
                    condicao = "Peso normal"
                }

                call.respond(response)
            }

        }

    }
}

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()


@UseExperimental(KtorExperimentalAPI::class)
suspend fun <T : Any> ApplicationCall.extractBody(kClass: KClass<T>): T =
    try {
        this.receiveOrNull(kClass) ?: throw BadRequestException("Content cannot be null")
    } catch (e: Exception) {
        throw BadRequestException("Invalid Request")
    }

/**
 * Modulo para serializar double utilizando 2 casas decimais
 */
class DoubleSerializer : JsonSerializer<Double>() {

    override fun serialize(value: Double, g: JsonGenerator, serializers: SerializerProvider) {
        val bd = BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP)
        g.writeNumber(bd)
    }

}

