package com.mason.net.aio.client

import java.nio.ByteBuffer
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
import java.util.concurrent.CountDownLatch


/**
 * Created by mwu on 2020/8/18
 */
class ClientWriteHandler(private val clientChannel: AsynchronousSocketChannel, private val latch: CountDownLatch) :
    CompletionHandler<Int, ByteBuffer> {

    override fun completed(result: Int, buffer: ByteBuffer) {
        println("ClientWriteHandler running at thread:${Thread.currentThread().name}")
        //完成全部数据的写入
        if (buffer.hasRemaining()) {
            clientChannel.write(buffer, buffer, this)
        } else {
            //读取数据
            val readBuffer = ByteBuffer.allocate(1024)
            clientChannel.read(readBuffer, readBuffer, ClientReadHandler(clientChannel, latch))
        }
    }

    override fun failed(exc: Throwable, attachment: ByteBuffer) {
        TODO("Not yet implemented")
    }
}