package br.com.zup.brenonoccioli

import java.io.FileInputStream
import java.io.FileOutputStream

fun main() {
    /* para instanciarmos nossas classes geradas pelo protobuf utilizamos o padrão
    * builder; dessa forma chamamos um newBuilder e setamos suas propriedades
    * (que são as mesmas que definimos em nosso arquivo .proto).
    */
    val request = FuncionarioRequest.newBuilder()
        .setNome("Yuri Matheus")
        .setCpf("000.000.000-00")
        .setSalario(2000.20)
        .setAtivo(true)
        .setCargo(Cargo.DEV)
        .addEnderecos(FuncionarioRequest.Endereco.newBuilder()
                            .setLogradouro("Rua das Tabajaras")
                            .setCep("00000-000")
                            .setComplemento("casa 20")
                            .build())
        .build()

    println(request)
    //escrevendo objeto em disco ou trafegando em rede
    request.writeTo(FileOutputStream("funcionario-request.bin"))

    //lendo o objeto em disco ou trafegado em rede
    val request2 = FuncionarioRequest.newBuilder()
        .mergeFrom(FileInputStream("funcionario-request.bin"))

    request2.setCargo(Cargo.GERENTE).build()
    println(request2)


}