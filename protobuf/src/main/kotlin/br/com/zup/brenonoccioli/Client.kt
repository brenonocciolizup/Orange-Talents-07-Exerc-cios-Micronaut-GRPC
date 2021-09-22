package br.com.zup.brenonoccioli

import io.grpc.ManagedChannelBuilder

fun main() {
    //definindo o canal a ser chamado
    val channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext() //localmente apenas
                .build()

    val request = FuncionarioRequest.newBuilder()
        .setNome("Yuri Matheus")
        .setCpf("000.000.000-00")
        .setIdade(22)
        .setSalario(2000.20)
        .setAtivo(true)
        .setCargo(Cargo.DEV)
        .addEnderecos(FuncionarioRequest.Endereco.newBuilder()
            .setLogradouro("Rua das Tabajaras")
            .setCep("00000-000")
            .setComplemento("casa 20")
            .build())
        .build()

    val client = FuncionarioServiceGrpc.newBlockingStub(channel)
    val response = client.cadastrar(request)

    println(response)


}