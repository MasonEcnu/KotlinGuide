package com.mason.net

import java.net.Socket
import java.util.*

/**
 * Created by mwu on 2020/8/10
 */
class NetClient(private val host: String, private val port: Int) {

    fun start() {
        println("NetClient connect at host:$host, port:$port")
        try {
            val socket = Socket(host, port)
            Thread(Runnable {
                while (true) {
                    val outputStream = socket.getOutputStream()
                    val scanner = Scanner(System.`in`)
                    println("Please input send msg:")
                    val sendMsg = scanner.nextLine()
                    outputStream.write(sendMsg.toByteArray())
                }
            }).start()

            Thread(Runnable {
                while (true) {
                    val inputStream = socket.getInputStream()
                    val size = inputStream.available()
                    if (size > 0) {
                        val byteArray = ByteArray(inputStream.available())
                        inputStream.read(byteArray)
                        val expression = String(byteArray, DEFAULT_CHARSET)
                        println("Receive msg from:${socket.remoteSocketAddress}, msg:$expression")
                    }
                }
            }).start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}

fun main() {
    val client = NetClient(LOCAL_HOST, LOCAL_PORT)
    client.start()
}