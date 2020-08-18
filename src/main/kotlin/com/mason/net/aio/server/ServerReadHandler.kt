package com.mason.net.aio.server

import com.mason.net.DEFAULT_CHARSET
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler


/**
 * Created by mwu on 2020/8/18
 */
class ServerReadHandler(private val channel: AsynchronousSocketChannel) : CompletionHandler<Int, ByteBuffer> {

    override fun completed(result: Int, attachment: ByteBuffer) {
        println("ServerReadHandler running at thread:${Thread.currentThread().name}")
        attachment.flip();
        val message = ByteArray(attachment.remaining())
        attachment.get(message);
        try {
            val expression = String(message, DEFAULT_CHARSET);
            println("Receive msg from:${channel.remoteAddress}, msg:$expression")
            doWrite(expression);
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace();
        }
    }

    private fun doWrite(result: String) {
        val bytes = result.toByteArray()
        val writeBuffer = ByteBuffer.allocate(bytes.size)
        writeBuffer.put(bytes)
        writeBuffer.flip()
        channel.write(
            writeBuffer,
            writeBuffer,
            object : CompletionHandler<Int, ByteBuffer> {
                override fun completed(result: Int, buffer: ByteBuffer) {
                    if (buffer.hasRemaining()) channel.write(buffer, buffer, this) else {
                        val readBuffer = ByteBuffer.allocate(1024)
                        channel.read(readBuffer, readBuffer, ServerReadHandler(channel))
                    }
                }

                override fun failed(exc: Throwable, attachment: ByteBuffer) {
                    try {
                        channel.close()
                    } catch (e: IOException) {
                    }
                }
            })
    }


    override fun failed(exc: Throwable, attachment: ByteBuffer) {
        try {
            channel.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}