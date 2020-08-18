package com.mason.net.aio.server

import com.mason.net.LOCAL_PORT

/**
 * Created by mwu on 2020/8/18
 */
class AioServer(private val port: Int) {

    companion object {
        @Volatile
        var clientCount: Int = 0
    }

    fun start() {
        Thread(AsyncServerHandler(port), "AioServer").start()
    }
}

fun main() {
    val aioServer = AioServer(LOCAL_PORT)
    aioServer.start()
}