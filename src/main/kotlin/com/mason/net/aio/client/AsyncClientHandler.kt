package com.mason.net.aio.client

import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
import java.util.concurrent.CountDownLatch


/**
 * Created by mwu on 2020/8/18
 */
class AsyncClientHandler(private val host: String, private val port: Int) :
    CompletionHandler<Void?, AsyncClientHandler>,
    Runnable {

    private lateinit var clientChannel: AsynchronousSocketChannel
    private lateinit var latch: CountDownLatch

    init {
        try {
            //创建异步的客户端通道
            clientChannel = AsynchronousSocketChannel.open()
            latch = CountDownLatch(1);
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun run() {
        println("AioClient running at thread:${Thread.currentThread().name}")

        clientChannel.connect(InetSocketAddress(host, port), this, this)
        try {
            latch.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        try {
            clientChannel.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun completed(result: Void?, attachment: AsyncClientHandler) {
        println("AsyncClient connect at host:$host, port:$port")
    }

    override fun failed(exc: Throwable, attachment: AsyncClientHandler) {
        println("AsyncClient connect failed")
        exc.printStackTrace()
        try {
            clientChannel.close()
            latch.countDown()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun sendMsg(msg: String) {
        val req = msg.toByteArray()
        val writeBuffer: ByteBuffer = ByteBuffer.allocate(req.size)
        writeBuffer.put(req)
        writeBuffer.flip()
        //异步写
        clientChannel.write(writeBuffer, writeBuffer, ClientWriteHandler(clientChannel, latch))
    }
}