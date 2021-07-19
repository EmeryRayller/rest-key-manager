package me.rayll.keymanager

import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import me.rayll.KeyManagerRemoveServiceGrpc
import me.rayll.RemoveChavePixResponse
import me.rayll.keymanager.shared.grpc.ClientFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class DeletarChaveControllerTest{

    @field:Inject
    lateinit var removeStub: KeyManagerRemoveServiceGrpc.KeyManagerRemoveServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    fun `deve remover a chave pix`() {

        //cenario
        val clienteId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        val chaveRespose = RemoveChavePixResponse.newBuilder()
            .setClientId(clienteId)
            .setPixId(pixId)
            .build()

        BDDMockito.given(removeStub.remove(Mockito.any()))
            .willReturn(chaveRespose)

        //ação
        val request = HttpRequest.DELETE<Any>("/api/v1/clientes/$clienteId/pix/$pixId")
        val response = client.toBlocking().exchange(request, Any::class.java)

        //validação
        assertEquals(HttpStatus.OK.code, response.status.code)

    }

    @Factory
    @Replaces(factory = ClientFactory::class)
    internal class MockitoRemoveStubFactory {

        @Singleton
        fun stubRemoveMock() = Mockito.mock(KeyManagerRemoveServiceGrpc.KeyManagerRemoveServiceBlockingStub::class.java)
    }
}




