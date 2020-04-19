package top.qiyuey.demo

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import top.qiyuey.demo.hello.GreeterGrpcKt
import top.qiyuey.demo.hello.HelloRequest
import java.io.Closeable
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class HelloClient private constructor(
        private val channel: ManagedChannel
) : Closeable {

    private val stub: GreeterGrpcKt.GreeterCoroutineStub = GreeterGrpcKt.GreeterCoroutineStub(channel)

    constructor(
            channelBuilder: ManagedChannelBuilder<*>,
            dispatcher: CoroutineDispatcher
    ) : this(channelBuilder.executor(dispatcher.asExecutor()).build())

    suspend fun greet(name: String) = coroutineScope {
        val request = HelloRequest.newBuilder().setName(name).build()!!
        val response = stub.sayHello(request)
        println("Received[${LocalTime.now().format(DateTimeFormatter.ISO_TIME)}]: ${response.message}")
    }

    suspend fun greets(names: Collection<String>) = coroutineScope {
        val requests = names.map { HelloRequest.newBuilder().setName(it).build()!! }.asFlow()
        stub.sayHellos(requests).collect {
            println("Received[${LocalTime.now().format(DateTimeFormatter.ISO_TIME)}]: ${it.message}")
        }
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}

fun main(): Unit = runBlocking {
    val port = 50051
    val users = (1..6).map { it.toString() }
    val channel = ManagedChannelBuilder.forAddress("localhost", port).usePlaintext()
    HelloClient(channel, Dispatchers.Default).use {
        coroutineScope {
            launch { it.greets(users) }
            delay(4000)
            launch { it.greet("world") }
        }
    }
    return@runBlocking
}
