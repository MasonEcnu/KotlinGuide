package com.mason.net.bio

import com.mason.net.LOCAL_PORT
import java.net.ServerSocket
import java.util.concurrent.Executors

/**
 * Created by mwu on 2020/8/10
 * 线程池化的BIO服务器
 */
class BioPooledServer(private val port: Int) {

    private val pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2)

    fun start() {
        println("BioPooledServer start at port:$port")
        val serverSocket = ServerSocket(port)
        try {
            while (true) {
                val clientSocket = serverSocket.accept()
                pool.submit(Thread(RunnableServer(clientSocket)))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            serverSocket.close()
        }
    }
}

fun main() {
    val pooledServer = BioPooledServer(LOCAL_PORT)
    pooledServer.start()
}