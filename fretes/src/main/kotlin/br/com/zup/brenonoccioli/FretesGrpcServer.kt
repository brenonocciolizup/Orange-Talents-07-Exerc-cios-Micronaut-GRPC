package br.com.zup.brenonoccioli

import com.google.protobuf.Any
import com.google.rpc.Code
import io.grpc.Status
import io.grpc.protobuf.*
import io.grpc.protobuf.StatusProto.*
import io.grpc.stub.StreamObserver
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import kotlin.random.Random

@Singleton
class FretesGrpcServer: FretesServiceGrpc.FretesServiceImplBase() {

    private val log = LoggerFactory.getLogger(FretesServiceGrpc::class.java)

    override fun calculaFrete(request: CalculaFreteRequest?, responseObserver: StreamObserver<CalculaFreteResponse>?) {
        log.info("Calcula Frete para request: $request")
        //valida cep nulo ou vazio
        val cep = request?.cep
        if(cep == null || cep.isBlank()){
            val e = Status.INVALID_ARGUMENT
                    .withDescription("o cep deve ser informado")
                    .asRuntimeException()
            responseObserver?.onError(e)
        }
        //valida formato de cep
        if(!cep!!.matches("[0-9]{5}-[0-9]{3}".toRegex())){
            val e = Status.INVALID_ARGUMENT
                    .withDescription("o cep inválido")
                    .augmentDescription("formato esperado deve ser 99999-999")
                    .asRuntimeException()
            responseObserver?.onError(e)
        }

        //simulando verificação de segurança
        if(cep.endsWith("333")){
            val statusProto = com.google.rpc.Status.newBuilder()
                                .setCode(Code.PERMISSION_DENIED.number)
                                .setMessage("Usuário não pode acessar esse recurso")
                                .addDetails(Any.pack(ErrorDetails.newBuilder()
                                    .setCode(401)
                                    .setMessage("token expirado")
                                    .build()))
                                .build()

            val e = StatusProto.toStatusRuntimeException(statusProto)
            responseObserver?.onError(e)
        }

        //simula possível erro de lógica de negócio
        var valor = 0.0
        try{
            valor = Random.nextDouble(from = 0.0, until = 140.0)
            if(valor > 100.0){
                throw IllegalStateException("Erro inesperado ao executar lógica de negócio!")
            }
        }catch (e: Exception){
            responseObserver?.onError(Status.INTERNAL
                            .withDescription(e.message)
                            .withCause(e) //anexado ao Status, mas não é enviado ao cliente
                            .asRuntimeException())
        }

        val response = CalculaFreteResponse.newBuilder()
            .setCep(request!!.cep)
            .setValor(valor)
            .build()

        log.info("Frete calculado: $response")

        responseObserver!!.onNext(response)
        responseObserver.onCompleted()
    }
}