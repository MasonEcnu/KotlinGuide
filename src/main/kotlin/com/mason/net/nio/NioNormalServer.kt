package com.mason.net.nio

import com.mason.net.LOCAL_PORT


/**
 * Created by mwu on 2020/8/10
 * NIO服务器
 */
class NioNormalServer(private val port: Int) {

    fun start() {

    }
}

fun main() {
    val nioNormalServer = NioNormalServer(LOCAL_PORT)
    nioNormalServer.start()
}