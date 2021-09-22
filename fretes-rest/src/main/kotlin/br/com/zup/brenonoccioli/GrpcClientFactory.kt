package br.com.zup.brenonoccioli

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import jakarta.inject.Singleton

@Factory
class GrpcClientFactory {

    @Singleton
    fun fretesClientStub(@GrpcChannel("localhost:50051") channel: ManagedChannel)
    : FretesServiceGrpc.FretesServiceBlockingStub? {

        return FretesServiceGrpc.newBlockingStub(channel)
    }

}