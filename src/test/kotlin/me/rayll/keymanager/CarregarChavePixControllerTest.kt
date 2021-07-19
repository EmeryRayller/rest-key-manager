package me.rayll.keymanager

import com.google.protobuf.Timestamp
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import me.rayll.*
import me.rayll.keymanager.shared.grpc.ClientFactory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.mockito.Mockito
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class CarregarChavePixControllerTest() {

    @field:Inject
    lateinit var carregaGrpc: KeymanagerCarregaGrpcServiceGrpc.KeymanagerCarregaGrpcServiceBlockingStub

    @field:Inject
    lateinit var listaGrpc: KeymanagerListaServiceGrpc.KeymanagerListaServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    fun `deve carregar a chave pix`() {

        //cenario
        val clienteId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()
        BDDMockito.given(carregaGrpc.carrega(Mockito.any())).willReturn(carregaChavePixResponse(clienteId, pixId))

        //ação
        val request = HttpRequest.GET<Any>("/api/v1/clientes/$clienteId/pix/$pixId")
        val response = client.toBlocking().exchange(request, Any::class.java)

        //validação
        val body = response.body() as Map<String, Any>

        assertEquals(body.get("pixId"), pixId)
        assertNotNull(response)
        assertEquals(HttpStatus.OK.code, response.status.code)


    }

    @Test
    fun `deve listar as chaves pix`() {

        //cenario
        val clienteId = UUID.randomUUID().toString()
        BDDMockito.given(listaGrpc.lista(Mockito.any())).willReturn(listaChavePixResponse(clienteId))

        //ação
        val request = HttpRequest.GET<Any>("/api/v1/clientes/$clienteId/pix")
        val response = client.toBlocking().exchange(request, Any::class.java)

        //validação
        val body = response.body() as
                kotlin.collections.ArrayList<kotlin.collections.LinkedHashMap<String, Any>>

        assertEquals(CHAVE_EMAIL, body[0].get("chave"))
        assertEquals(CHAVE_CELULAR, body[1].get("chave"))
        assertNotNull(response)
        assertEquals(HttpStatus.OK.code, response.status.code)

    }

    val CHAVE_EMAIL = "teste@teste.com.br"
    val CHAVE_CELULAR = "+5511912345678"
    val CONTA_CORRENTE = TipoDeConta.CONTA_CORRENTE
    val TIPO_DE_CHAVE_EMAIL = TipoDeChave.EMAIL
    val TIPO_DE_CHAVE_CELULAR = TipoDeChave.CELULAR
    val INSTITUICAO = "Itau"
    val TITULAR = "Woody"
    val DOCUMENTO_DO_TITULAR = "34597563067"
    val AGENCIA = "0001"
    val NUMERO_DA_CONTA = "1010-1"
    val CHAVE_CRIADA_EM = LocalDateTime.now()

    private fun listaChavePixResponse(clienteId: String): ListaChavesPixResponse {
        val chaveEmail = ListaChavesPixResponse.ChavePix.newBuilder()
            .setPixId(UUID.randomUUID().toString())
            .setTipo(TIPO_DE_CHAVE_EMAIL)
            .setChave(CHAVE_EMAIL)
            .setTipoDeConta(CONTA_CORRENTE)
            .setCriadaEm(CHAVE_CRIADA_EM.let {
                val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                Timestamp.newBuilder()
                    .setSeconds(createdAt.epochSecond)
                    .setNanos(createdAt.nano)
                    .build()
            })
            .build()

        val chaveCelular = ListaChavesPixResponse.ChavePix.newBuilder()
            .setPixId(UUID.randomUUID().toString())
            .setTipo(TIPO_DE_CHAVE_CELULAR)
            .setChave(CHAVE_CELULAR)
            .setTipoDeConta(CONTA_CORRENTE)
            .setCriadaEm(CHAVE_CRIADA_EM.let {
                val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                Timestamp.newBuilder()
                    .setSeconds(createdAt.epochSecond)
                    .setNanos(createdAt.nano)
                    .build()
            })
            .build()


        return ListaChavesPixResponse.newBuilder()
            .setClienteId(clienteId)
            .addAllChaves(listOf(chaveEmail, chaveCelular))
            .build()

    }

    private fun carregaChavePixResponse(clienteId: String, pixId: String) =
        CarregaChavePixResponse.newBuilder()
            .setClienteId(clienteId)
            .setPixId(pixId)
            .setChave(CarregaChavePixResponse.ChavePix
                .newBuilder()
                .setTipo(TIPO_DE_CHAVE_EMAIL)
                .setChave(CHAVE_EMAIL)
                .setConta(CarregaChavePixResponse.ChavePix.ContaInfo.newBuilder()
                    .setTipo(CONTA_CORRENTE)
                    .setInstituicao(INSTITUICAO)
                    .setNomeDoTitular(TITULAR)
                    .setCpfDoTitular(DOCUMENTO_DO_TITULAR)
                    .setAgencia(AGENCIA)
                    .setNumeroDaConta(NUMERO_DA_CONTA)
                    .build()
                )
                .setCriadaEm(CHAVE_CRIADA_EM.let {
                    val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                    Timestamp.newBuilder()
                        .setSeconds(createdAt.epochSecond)
                        .setNanos(createdAt.nano)
                        .build()
                })).build()

    @Factory
    @Replaces(factory = ClientFactory::class)
    internal class MockitoStubFactory {

        @Singleton
        fun stubCarregaMock() = Mockito.mock(KeymanagerCarregaGrpcServiceGrpc.KeymanagerCarregaGrpcServiceBlockingStub::class.java)

        @Singleton
        fun stubListaMock() = Mockito.mock(KeymanagerListaServiceGrpc.KeymanagerListaServiceBlockingStub::class.java)


    }

}