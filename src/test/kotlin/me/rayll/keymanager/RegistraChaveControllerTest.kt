package me.rayll.keymanager

import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import me.rayll.KeyManagerServiceGrpc
import me.rayll.RegistraChavePixResponse
import me.rayll.keymanager.shared.grpc.ClientFactory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class RegistraChaveControllerTest{

    @field:Inject
    lateinit var registraStub: KeyManagerServiceGrpc.KeyManagerServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    fun `deve registrar uma chave pix`() {

        //cenário
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        val respostaEsperada = RegistraChavePixResponse.newBuilder()
            .setClienteId(clientId)
            .setPixId(pixId)
            .build()

        BDDMockito.given(registraStub.registra(Mockito.any())).willReturn(respostaEsperada)

        val novaChavePix = NovaChavePixRequest(
            tipoDeConta = TipoDeContaRequest.CONTA_CORRENTE,
            chave = "algumachave@com.br",
            tipoDeChave = TipoDeChaveRequest.EMAIL)

        //ação
        val request = HttpRequest.POST("/api/v1/clientes/$clientId/pix", novaChavePix)
        val response = client.toBlocking().exchange(request, NovaChavePixRequest::class.java)

        //validação
        Assertions.assertEquals(HttpStatus.CREATED, response.status)
        assertTrue(response.headers.contains("Location"))
        assertTrue(response.header("Location")!!.contains(pixId))
    }

    @Factory
    @Replaces(factory = ClientFactory::class)
    internal class MockitoStubFactory {

        @Singleton
        fun stubMock() = mock(KeyManagerServiceGrpc.KeyManagerServiceBlockingStub::class.java)
    }
}