package com.mason.net.bio

import java.net.Socket

/**
 * Created by mwu on 2020/8/17
 */
class BioRunnableServer(private val clientSocket: Socket) : Runnable {

    override fun run() {
        println("Accept connect from:${clientSocket.remoteSocketAddress}")
        val data = ByteArray(1024)
        var realReadSize = 0
        clientSocket.use {
            it.getInputStream().use { inputStream ->
                realReadSize = inputStream.read(data)
            }
        }
        if (realReadSize > 0) {
            val realReceiveData = ByteArray(realReadSize)
            data.copyInto(realReceiveData, startIndex = 0, endIndex = realReadSize)
            println("Receive msg:${String(realReceiveData)}")
        }
    }
}