package com.mason.net.aio.client

import com.mason.net.DEFAULT_CHARSET
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
import java.util.concurrent.CountDownLatch


/**
 * Created by mwu on 2020/8/18
 */
class ClientReadHandler(private val clientChannel: AsynchronousSocketChannel, private val latch: CountDownLatch) :
    CompletionHandler<Int, ByteBuffer> {

    override fun completed(result: Int, buffer: ByteBuffer) {
        println("ClientReadHandler running at thread:${Thread.currentThread().name}")
        buffer.flip()
        val bytes = ByteArray(buffer.remaining())
        buffer[bytes]
        try {
            val expression = String(bytes, DEFAULT_CHARSET)
            println("Receive msg from:${clientChannel.remoteAddress}, msg:$expression")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    override fun failed(exc: Throwable, attachment: ByteBuffer) {
        println("Read msg failed")
        try {
            clientChannel.close()
            latch.countDown()
        } catch (e: IOException) {
        }
    }
}