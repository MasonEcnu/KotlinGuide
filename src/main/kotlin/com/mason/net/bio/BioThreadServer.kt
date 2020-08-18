package com.mason.net.bio

import com.mason.net.LOCAL_PORT
import java.net.ServerSocket

/**
 * Created by mwu on 2020/8/10
 * 多线程BIO服务器
 */
class BioThreadServer(private val port: Int) {

    fun start() {
        println("BioThreadServer start at port:$port")
        val serverSocket = ServerSocket(port)
        try {
            while (true) {
                val clientSocket = serverSocket.accept()
                Thread(BioRunnableServer(clientSocket)).start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            serverSocket.close()
        }
    }
}

fun main() {
    val threadServer = BioThreadServer(LOCAL_PORT)
    threadServer.start()
}