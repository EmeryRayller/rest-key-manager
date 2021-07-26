package me.rayll.keymanager

import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import me.rayll.KeyManagerServiceGrpc
import org.slf4j.LoggerFactory
import javax.validation.Valid

@Validated
@Controller("/api/v1/clientes/{clientId}")
class RegistraChaveController(
    private val registraChavePixClient: KeyManagerServiceGrpc.KeyManagerServiceBlockingStub
) {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Post("/pix")
    fun create(clientId: String,
               @Valid @Body request: NovaChavePixRequest): HttpResponse<Any> {

        LOGGER.info("[$clientId] criando uma chave pix com $request")

        val grpcResponse = registraChavePixClient.registra(request.paraModeloGrpc(clientId))

        return HttpResponse
            .created(location(clientId, grpcResponse.pixId))
    }

    private fun location(clientId: String, pixId: String) = HttpResponse
        .uri("/api/v1/clientes/${pixId}")
}