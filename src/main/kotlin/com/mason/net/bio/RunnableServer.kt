package com.mason.net.bio

import java.net.Socket

/**
 * Created by mwu on 2020/8/17
 */
class RunnableServer(private val clientSocket: Socket) : Runnable {

    override fun run() {
        println("Accept connect from:${clientSocket.remoteSocketAddress}")
        val data = ByteArray(1024)
        clientSocket.use {
            it.getInputStream().use { inputStream ->
                inputStream.read(data)
            }
        }
        println("Receive msg:${String(data)}")
    }
}