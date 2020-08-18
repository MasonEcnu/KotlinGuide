package com.mason.net.aio.server

import java.nio.ByteBuffer
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler

/**
 * Created by mwu on 2020/8/18
 */
class AcceptHandler : CompletionHandler<AsynchronousSocketChannel, AsyncServerHandler> {

    override fun completed(channel: AsynchronousSocketChannel, serverHandler: AsyncServerHandler) {
        println("AcceptHandler running at thread:${Thread.currentThread().name}")
        //继续接受其他客户端的请求
        AioServer.clientCount++
        println("Accept connect from :${channel.remoteAddress}, count:${AioServer.clientCount}")
        serverHandler.channel.accept(serverHandler, this)
        //创建新的Buffer
        val buffer: ByteBuffer = ByteBuffer.allocate(1024)
        //异步读  第三个参数为接收消息回调的业务Handler
        channel.read(buffer, buffer, ServerReadHandler(channel))
    }

    override fun failed(exc: Throwable, serverHandler: AsyncServerHandler) {
        exc.printStackTrace();
        serverHandler.latch.countDown();
    }
}