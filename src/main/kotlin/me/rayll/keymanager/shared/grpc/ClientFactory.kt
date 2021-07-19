package me.rayll.keymanager.shared.grpc

import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import me.rayll.KeyManagerRemoveServiceGrpc
import me.rayll.KeyManagerServiceGrpc
import javax.inject.Singleton

@Factory
class ClientFactory(@GrpcChannel("keyManager") val channel: ManagedChannel) {

    @Singleton
    fun registraChave() = KeyManagerServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun removeChave() = KeyManagerRemoveServiceGrpc.newBlockingStub(channel)
}