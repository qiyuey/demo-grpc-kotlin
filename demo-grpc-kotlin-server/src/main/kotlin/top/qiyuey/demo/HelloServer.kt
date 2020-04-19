package top.qiyuey.demo

import io.grpc.Server
import io.grpc.ServerBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import top.qiyuey.demo.hello.GreeterGrpcKt
import top.qiyuey.demo.hello.HelloReply
import top.qiyuey.demo.hello.HelloRequest

class HelloServer(
        private val port: Int
) {
    private val server: Server = ServerBuilder
            .forPort(port)
            .addService(HelloService())
            .build()

    fun start() {
        server.start()
        println("Server started, listening on $port")
        Runtime.getRuntime().addShutdownHook(
                Thread {
                    println("*** shutting down gRPC server since JVM is shutting down")
                    this@HelloServer.stop()
                    println("*** server shut down")
                }
        )
    }

    private fun stop() {
        server.shutdown()
    }

    fun blockUntilShutdown() {
        server.awaitTermination()
    }

    private class HelloService : GreeterGrpcKt.GreeterCoroutineImplBase() {
        override suspend fun sayHello(request: HelloRequest): HelloReply = HelloReply
                .newBuilder()
                .setMessage("Hello ${request.name}")
                .build()

        override fun sayHellos(requests: Flow<HelloRequest>): Flow<HelloReply> = flow {
            requests.collect {
                delay(1000)
                emit(sayHello(it))
            }
        }
    }
}

fun main() {
    val port = 50051
    val server = HelloServer(port)
    server.start()
    server.blockUntilShutdown()
}
