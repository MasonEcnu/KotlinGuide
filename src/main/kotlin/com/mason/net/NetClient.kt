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
            Socket(host, port).use { socket ->
                socket.getOutputStream().use { outputStream ->
                    val scanner = Scanner(System.`in`)
                    println("Please input send msg:")
                    val sendMsg = scanner.nextLine()
                    outputStream.write(sendMsg.toByteArray())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}

fun main() {
    val client = NetClient(LOCAL_HOST, LOCAL_PORT)
    client.start()
}