package me.rayll.keymanager

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import me.rayll.CarregaChavePixRequest
import me.rayll.KeymanagerCarregaGrpcServiceGrpc
import me.rayll.KeymanagerListaServiceGrpc
import me.rayll.ListaChavesPixRequest
import org.slf4j.LoggerFactory

@Controller("/api/v1/clientes/{clienteId}")
class CarregarChavePixController(
    private val carregaPix: KeymanagerCarregaGrpcServiceGrpc.KeymanagerCarregaGrpcServiceBlockingStub,
    private val listaPix: KeymanagerListaServiceGrpc.KeymanagerListaServiceBlockingStub
) {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Get("/pix/{pixId}")
    fun carrega(clienteId: String, pixId: String): HttpResponse<Any> {
        LOGGER.info("Carrega chave por pix id $pixId")

        val chaveResponse = carregaPix.carrega(
            CarregaChavePixRequest.newBuilder()
                .setPixId(
                    CarregaChavePixRequest
                        .FiltroPorPixId
                        .newBuilder()
                        .setClienteId(clienteId)
                        .setPixId(pixId).build()
                ).build()
        )

        return HttpResponse.ok(DetalheChavePixResponse(chaveResponse))
    }

    @Get("/pix")
    fun lista(clienteId: String): HttpResponse<Any> {
        LOGGER.info("Listando chaves pix")

        val listaResponse = listaPix.lista(
            ListaChavesPixRequest.newBuilder()
                .setClienteId(clienteId)
                .build())

        val lista = listaResponse.chavesList.map { ChavePixResponse(it) }
        return HttpResponse.ok(lista)
    }
}