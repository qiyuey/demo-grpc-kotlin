package top.qiyuey.demo

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import top.qiyuey.demo.hello.GreeterGrpcKt
import top.qiyuey.demo.hello.HelloRequest
import java.io.Closeable
import java.util.concurrent.TimeUnit

class HelloClient(
        private val channel: ManagedChannel
) : Closeable {
    private val stub: GreeterGrpcKt.GreeterCoroutineStub = GreeterGrpcKt.GreeterCoroutineStub(channel)

    suspend fun greet(name: String) = coroutineScope {
        val request = HelloRequest.newBuilder().setName(name).build()!!
        val response = stub.sayHello(request)
        println("Received: ${response.message}")
    }

    suspend fun greets(names: Collection<String>) = coroutineScope {
        val requests = names.asFlow().map { HelloRequest.newBuilder().setName(it).build()!! }
        stub.sayHellos(requests).collect {
            println("Received: ${it.message}")
        }
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}


/**
 * Greeter, uses first argument as name to greet if present;
 * greets "world" otherwise.
 */
fun main() = runBlocking {
    val port = 50051

    val client = HelloClient(
            ManagedChannelBuilder.forAddress("localhost", port)
                    .usePlaintext()
                    .executor(Dispatchers.Default.asExecutor())
                    .build())

    client.greet("world")

    val users = (0..6).map { it.toString() }
    client.greets(users)
}
