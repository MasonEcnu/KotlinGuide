package com.mason.guide.coroutines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

/**
 * Created by mwu on 2020/4/17
 * Kotlin协程基础
 */
fun main() {
    // 第一个协程
    println("第一个协程demo")
    GlobalScope.launch {
        // 在后台启动一个新的协程并继续

        // delay只能在协程中使用
        delay(1000L) // 非阻塞的等待 1 秒钟（默认时间单位是毫秒）
        println("World!") // 在延迟后打印输出
    }
    println("Hello,") // 协程已在等待时主线程还在继续
    Thread.sleep(2000L) // 阻塞主线程 2 秒钟来保证 JVM 存活

    // 本质上，协程是轻量级的线程
    // 效果相同啊。。。
    println("替换GlobalScope->thread，delay->Thread.sleep")
    thread {
        // 在后台启动一个新的协程并继续
        Thread.sleep(1000L) // 阻塞等待 1 秒钟（默认时间单位是毫秒）
        println("World!") // 在延迟后打印输出
    }
    println("Hello,") // 协程已在等待时主线程还在继续
    Thread.sleep(2000L) // 阻塞主线程 2 秒钟来保证 JVM 存活

    // 桥接
    println("桥接阻塞")
    GlobalScope.launch {
        delay(1000L)
        println("World!")
    }
    println("Hello,")
    // 调用runBlocking的主线程
    // 会一直等待内部的协程执行完毕
    runBlocking {
        delay(2000L)
    }

    // 重写桥接阻塞
    println("重写桥接阻塞")
    bridgeBlocking()
}

fun bridgeBlocking() = runBlocking {
    GlobalScope.launch {
        delay(1000L)
        println("World!")
    }
    println("Hello,") // 主协程在这里会立即执行
    delay(2000L)      // 延迟 2 秒来保证 JVM 存活
}