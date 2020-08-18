package com.mason.net.aio.server

import java.net.InetSocketAddress
import java.nio.channels.AsynchronousServerSocketChannel

import java.util.concurrent.CountDownLatch


/**
 * Created by mwu on 2020/8/18
 */
class AsyncServerHandler(private val port: Int) : Runnable {

    lateinit var latch: CountDownLatch
    lateinit var channel: AsynchronousServerSocketChannel

    init {
        try {
            channel = AsynchronousServerSocketChannel.open()
            // 绑定端口
            channel.bind(InetSocketAddress(port))
            println("AsyncServer start at port:$port")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun run() {
        println("AioServer running at thread:${Thread.currentThread().name}")
        // CountDownLatch初始化
        // 它的作用：在完成一组正在执行的操作之前，允许当前的现场一直阻塞
        // 此处，让现场在此阻塞，防止服务端执行完成后退出
        // 也可以使用while(true)+sleep 
        // 生成环境就不需要担心这个问题，以为服务端是不会退出的

        // CountDownLatch初始化
        // 它的作用：在完成一组正在执行的操作之前，允许当前的现场一直阻塞
        // 此处，让现场在此阻塞，防止服务端执行完成后退出
        // 也可以使用while(true)+sleep 
        // 生成环境就不需要担心这个问题，以为服务端是不会退出的
        latch = CountDownLatch(1)
        // 用于接收客户端的连接
        // 用于接收客户端的连接
        channel.accept(this, AcceptHandler())
        try {
            latch.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}