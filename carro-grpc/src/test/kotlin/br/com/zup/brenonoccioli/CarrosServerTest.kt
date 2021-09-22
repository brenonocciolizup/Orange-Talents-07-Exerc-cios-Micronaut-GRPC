package br.com.zup.brenonoccioli

import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Singleton
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@MicronautTest(transactional = false) //levanta o cenário para os testes
internal class CarrosServerTest(val repository: CarroRepository,
                                val grpcClient: CarroGrpcServiceGrpc.CarroGrpcServiceBlockingStub){
    /*
     *  CENÁRIOS POSSÍVEIS
     * 1. "caminho feliz"
     * 2. quando já existe a placa
     * 3. quando os dados de entrada são inválidos
     */
    @Test
    fun `deve cadastrar um novo carro`(){
        //cenário
        repository.deleteAll()

        //ação
        val response = grpcClient.cadastrar(CarroRequest.newBuilder()
                                .setModelo("gol")
                                .setPlaca("HPX-1234")
                                .build())
        //validação
        with(response){
            assertNotNull(this.id) //valida se o id retornado não é nulo
            assertTrue(repository.existsById(id)) //valida se o id retornado realmente existe no banco
        }
    }

    @Test
    fun `nao deve adicionar novo carro quando a placa ja existir`(){
        //cenário
        repository.deleteAll()
        val carroExistente = repository.save(Carro(modelo="Palio", placa="OIP-9876"))
        //ação
        val error = assertThrows<StatusRuntimeException>{
            grpcClient.cadastrar(CarroRequest.newBuilder()
                .setModelo("Ferrari")
                .setPlaca(carroExistente.placa)
                .build())
        }
        //validação
        with(error){
            assertEquals(Status.ALREADY_EXISTS.code, this.status.code)
            assertEquals("placa já existe", this.status.description)
        }
    }

    @Test
    fun `nao deve cadastrar novo carro quando dados de entrada forem inválidos`(){
        //cenário
        repository.deleteAll()
        //ação
        val error = assertThrows<StatusRuntimeException>{
            grpcClient.cadastrar(CarroRequest.newBuilder()
                .setModelo("")
                .setPlaca("")
                .build())
        }
        //validação
        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertEquals("dados de entrada inválidos", this.status.description)
        }
    }

    @Factory
    class Clients {
        //pegamos o channel que o micronaut sobe para os testes
        @Singleton
        fun blokingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): CarroGrpcServiceGrpc.CarroGrpcServiceBlockingStub? {
            return CarroGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

}