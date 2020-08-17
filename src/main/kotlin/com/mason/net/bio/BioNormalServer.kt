package com.mason.net.bio

import com.mason.net.LOCAL_PORT
import java.net.ServerSocket

/**
 * Created by mwu on 2020/8/10
 * 一般的BIO服务器
 */
class BioNormalServer(private val port: Int) {

    fun start() {
        println("BioNormalServer start at port:$port")
        try {
            ServerSocket(port).use { serverSocket ->
                while (true) {
                    serverSocket.accept().use { clientSocket ->
                        val data = ByteArray(1024)
                        println("Accept connect from:${clientSocket.remoteSocketAddress}")
                        clientSocket.getInputStream().use { inputStream ->
                            inputStream.read(data)
                        }
                        println("Receive msg:${String(data)}")
                    }
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun main() {
    val normalServer = BioNormalServer(LOCAL_PORT)
    normalServer.start()
}