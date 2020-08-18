package com.mason.net.nio

import com.mason.net.DEFAULT_CHARSET
import com.mason.net.LOCAL_HOST
import com.mason.net.LOCAL_PORT
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SocketChannel
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.system.exitProcess


/**
 * Created by mwu on 2020/8/18
 */
class NioClient(private val host: String, private val port: Int) {

    private lateinit var selector: Selector
    private lateinit var socketChannel: SocketChannel

    private var started = AtomicBoolean(false)

    fun start() {
        try {
            if (started.compareAndSet(false, true)) {
                //  创建选择器
                selector = Selector.open()
                //  打开监听通道
                socketChannel = SocketChannel.open()
                //  开启非阻塞模式
                socketChannel.configureBlocking(false)

                startNow()
            } else {
                println("NioClient start failed")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            exitProcess(1)
        }
    }

    private fun startNow() {
        try {
            doConnect()
        } catch (e: IOException) {
            e.printStackTrace()
            exitProcess(1)
        }
        // 循环遍历selector
        while (started.get()) {
            try {
                if (selector.select() > 0) {
                    val keys = selector.selectedKeys()
                    val it = keys.iterator()
                    while (it.hasNext()) {
                        val key = it.next()
                        it.remove()
                        try {
                            handleInput(key)
                        } catch (e: Exception) {
                            key?.cancel()
                            key?.channel()?.close()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                exitProcess(1)
            }
        }
        // selector关闭后会自动释放里面管理的资源
        try {
            selector.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun handleInput(key: SelectionKey) {
        if (key.isValid) {
            val sc = key.channel() as SocketChannel
            if (key.isConnectable) {
                if (sc.finishConnect()) {
                    // do nothing
                } else {
                    exitProcess(1)
                }
            }
            // 读消息
            if (key.isReadable) {
                // 创建ByteBuffer，并开辟一个1M的缓冲区
                val buffer = ByteBuffer.allocate(1024)
                // 读取请求码流，返回读取到的字节数
                val readBytes = sc.read(buffer)
                // 读取到字节，对字节进行编解码
                if (readBytes > 0) {
                    // 将缓冲区当前的limit设置为position=0，用于后续对缓冲区的读取操作
                    buffer.flip()
                    // 根据缓冲区可读字节数创建字节数组
                    val bytes = ByteArray(buffer.remaining())
                    // 将缓冲区可读字节数组复制到新建的数组中
                    buffer[bytes]
                    val result = String(bytes, DEFAULT_CHARSET)
                    println("客户端收到消息：$result")
                } else if (readBytes < 0) {
                    key.cancel()
                    sc.close()
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun doConnect() {
        println("NioClient connect at host:$host, port:$port")
        if (socketChannel.connect(InetSocketAddress(host, port))) {
            // do noting
        } else {
            socketChannel.register(selector, SelectionKey.OP_CONNECT)
        }
    }

    @Throws(Exception::class)
    fun sendMsg(msg: String) {
//        socketChannel.register(selector, SelectionKey.OP_READ)
        doWrite(msg)
    }

    @Throws(IOException::class)
    private fun doWrite(request: String) {
        //  将消息编码为字节数组
        val bytes = request.toByteArray()
        //  根据数组容量创建ByteBuffer
        val writeBuffer: ByteBuffer = ByteBuffer.allocate(bytes.size)
        //  将字节数组复制到缓冲区
        writeBuffer.put(bytes)
        //  flip操作
        writeBuffer.flip()
        //  发送缓冲区的字节数组
        socketChannel.write(writeBuffer)
        // ****此处不含处理“写半包”的代码
    }

    fun stop() {
        if (started.get()) {
            while (!started.compareAndSet(true, false)) {
                println("Stopping NioClient......")
            }
        }
        exitProcess(1)
    }
}

fun main() {
    val nioClient = NioClient(LOCAL_HOST, LOCAL_PORT)
    Thread(Runnable {
        nioClient.start()
    }).start()

    Thread.sleep(1000L)

    Thread(Runnable {
        while (true) {
            val scanner = Scanner(System.`in`)
            println("Please input send msg:")
            val sendMsg = scanner.nextLine()
            nioClient.sendMsg(sendMsg)
        }
    }).start()
}