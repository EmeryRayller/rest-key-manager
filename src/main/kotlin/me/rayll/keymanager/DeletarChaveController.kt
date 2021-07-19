package me.rayll.keymanager

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import me.rayll.KeyManagerRemoveServiceGrpc
import me.rayll.RemoveChavePixRequest
import org.slf4j.LoggerFactory

@Controller("/api/v1/clientes/{clienteId}")
class DeletarChaveController(
    private val grpcRemove: KeyManagerRemoveServiceGrpc.KeyManagerRemoveServiceBlockingStub
){

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Delete("/pix/{pixId}")
    fun remover(clienteId: String, pixId: String): HttpResponse<Any> {

        LOGGER.info("Removendo chave pix com $pixId")
        grpcRemove.remove(
            RemoveChavePixRequest.newBuilder()
                .setPixId(pixId)
                .setClientId(clienteId)
                .build()
        )

        return HttpResponse.ok()
    }
}