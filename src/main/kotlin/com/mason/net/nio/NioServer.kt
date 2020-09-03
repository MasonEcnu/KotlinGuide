package com.mason.net.nio

import com.mason.net.DEFAULT_CHARSET
import com.mason.net.LOCAL_PORT
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.system.exitProcess


/**
 * Created by mwu on 2020/8/10
 * NIO服务器
 */
class NioServer(private val port: Int) {

    private lateinit var selector: Selector
    private lateinit var serverChannel: ServerSocketChannel

    private val started = AtomicBoolean(false)

    fun start() {
        try {
            if (started.compareAndSet(false, true)) {
                selector = Selector.open()
                serverChannel = ServerSocketChannel.open()
                serverChannel.configureBlocking(false)
                serverChannel.bind(InetSocketAddress(port))
                serverChannel.register(selector, SelectionKey.OP_ACCEPT)

                println("NioServer start at port:$port")

                startNow()
            } else {
                println("NioServer start failed")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            exitProcess(1)
        }
    }

    private fun startNow() {
        while (started.get()) {
            try {
                if (selector.selectNow() > 0) {
                    val keys = selector.selectedKeys()
                    val keyIterator = keys.iterator()
                    if (keyIterator.hasNext()) {
                        val key = keyIterator.next()
                        keyIterator.remove()
                        try {
                            handleInput(key)
                        } catch (e: Exception) {
                            key?.cancel()
                            key?.channel()?.close()
                        }
                    }
                } else {
                    println("tick...")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                stop()
            }
        }
        try {
            selector.close()
        } catch (e: Exception) {
            e.printStackTrace()
            stop()
        }
    }

    private fun handleInput(key: SelectionKey) {
        if (key.isValid) {
            if (key.isAcceptable) {
                val ssc = key.channel() as ServerSocketChannel
                val socketChannel = ssc.accept()
                println("Accept connect from :${socketChannel.remoteAddress}")
                socketChannel.configureBlocking(false)
                socketChannel.register(selector, SelectionKey.OP_READ)
            }

            if (key.isReadable) {
                val socketChannel = key.channel() as SocketChannel
                val buffer = ByteBuffer.allocate(1024)
                val readBytes: Int = socketChannel.read(buffer)
                if (readBytes > 0) {
                    buffer.flip()
                    val bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes)

                    val expression = String(bytes, DEFAULT_CHARSET)
                    println("Receive msg from:${socketChannel.remoteAddress} msg:$expression")
                    doWrite(socketChannel, expression)
                }
            }
        }
    }

    private fun doWrite(socketChannel: SocketChannel, expression: String) {
        val bytes = expression.toByteArray(DEFAULT_CHARSET)
        val writeBuffer = ByteBuffer.allocate(bytes.size)
        writeBuffer.put(bytes)
        writeBuffer.flip()
        socketChannel.write(writeBuffer)
    }

    fun stop() {
        if (started.get()) {
            while (!started.compareAndSet(true, false)) {
                println("Stopping NioServer......")
            }
        }
        exitProcess(1)
    }
}

fun main() {
    val nioNormalServer = NioServer(LOCAL_PORT)
    nioNormalServer.start()
}