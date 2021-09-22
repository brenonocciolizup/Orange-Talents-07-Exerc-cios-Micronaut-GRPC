package br.com.zup.brenonoccioli

import io.grpc.Status
import io.grpc.stub.StreamObserver
import jakarta.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class CarrosServer(val repository: CarroRepository): CarroGrpcServiceGrpc.CarroGrpcServiceImplBase() {

    override fun cadastrar(request: CarroRequest, responseObserver: StreamObserver<CarroResponse>) {

        if(repository.existsByPlaca(request.placa)){
            responseObserver.onError(Status.ALREADY_EXISTS
                                    .withDescription("placa já existe")
                                    .asRuntimeException())
            return
        }

        val carro = Carro(request.placa, request.modelo)

        try{
            repository.save(carro)
        } catch (e: ConstraintViolationException){
            responseObserver.onError(Status.INVALID_ARGUMENT
                            .withDescription("dados de entrada inválidos")
                            .asRuntimeException())
            return
        }

        responseObserver.onNext(CarroResponse.newBuilder().setId(carro.id!!).build())
        responseObserver.onCompleted()


    }
}