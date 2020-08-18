package com.mason.net.aio.client

import com.mason.net.LOCAL_HOST
import com.mason.net.LOCAL_PORT
import java.util.*

/**
 * Created by mwu on 2020/8/18
 */
class AioClient(private val host: String, private val port: Int) {

    private lateinit var clientHandle: AsyncClientHandler

    fun start() {
        clientHandle = AsyncClientHandler(host, port)
        Thread(clientHandle, "AioClient").start()
    }

    fun sendMsg(msg: String) {
        if (msg == "q") return
        clientHandle.sendMsg(msg)
    }
}

fun main() {
    val aioClient = AioClient(LOCAL_HOST, LOCAL_PORT)
    aioClient.start()

    Thread.sleep(1000L)

    while (true) {
        val scanner = Scanner(System.`in`)
        println("Please input send msg:")
        val sendMsg = scanner.nextLine()
        aioClient.sendMsg(sendMsg)
    }
}