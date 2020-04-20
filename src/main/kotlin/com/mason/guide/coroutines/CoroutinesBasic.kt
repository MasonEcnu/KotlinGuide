package com.mason.guide.coroutines

import kotlinx.coroutines.*
import kotlin.concurrent.thread

/**
 * Created by mwu on 2020/4/17
 * Kotlin协程基础
 */
fun main() {
    // 第一个协程
    firstCoroutines()
    // thread替换
    runWithThread()
    // 桥接
    bridgeLink()
    // 重写桥接阻塞
    bridgeBlocking()
    // 等待一个作业
    waitOneJob()
    // 结构化并发
    structuredConcurrency()
    // 作用域构建器
    scopeBuilder()
    // 协程很轻量
//    lightCoroutines()
    // 启动10万个线程试试
    // 内存太多了，并不会有问题
//    heavyThread()
    // 全局协程像守护线程
    globalCoroutines()
}

fun globalCoroutines() = runBlocking {
    println("全局协程像守护线程")
    // 在GlobalScope全局作用域中启动的协程
    // 不会使进程保持活跃
    // 更像是守护线程
    // 在主线程结束时，强制退出
    GlobalScope.launch {
        repeat(1000) { i ->
            println("I'm sleeping $i ...")
            delay(500L)
        }
    }
    delay(1300L) // 在延迟后退出
}

fun heavyThread() {
    println("重量级线程")
    repeat(100_000) { count ->
        Thread(
            Runnable {
                val current = Thread.currentThread()
                Thread.sleep(10L)
                if (count != 0 && count % 100 == 0) {
                    println()
                }
                print(current.name + ". ")
            }
        ).start()
    }
}

fun lightCoroutines() = runBlocking {
    println("协程很轻量")
    repeat(100_000) {
        launch {
            delay(10L)
            if (it != 0 && it % 100 == 0) {
                println()
            }
            print(". ")
        }
    }
    println()
}

// 挂起函数，suspend
suspend fun printWorld() {
    delay(1000L)
    println("World!")
}

fun scopeBuilder() = runBlocking {// this: CoroutineScope
    println("作用域构建器")
    launch {
        delay(1000L)
        println("Task from runBlocking")
    }

    // 创建一个协程作用域
    coroutineScope {
        launch {
            delay(500L)
            println("Task from nested launch")
        }

        delay(500L)
        println("Task from coroutine scope")
    }

    println("Coroutine scope is over")
}

fun structuredConcurrency() = runBlocking {
    println("结构化并发")
    launch {
        printWorld()
    }
    println("Hello,")
}

fun waitOneJob() = runBlocking {
    println("等待一个作业")
    val job = GlobalScope.launch {
        printWorld()
    }
    println("Hello,")
    job.join()
}

fun bridgeLink() {
    println("桥接阻塞")
    GlobalScope.launch {
        printWorld()
    }
    println("Hello,")
    // 调用runBlocking的主线程
    // 会一直等待内部的协程执行完毕
    runBlocking {
        delay(2000L)
    }
}

fun runWithThread() {
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
}

fun firstCoroutines() {
    println("第一个协程demo")
    GlobalScope.launch {
        // 在后台启动一个新的协程并继续

        // delay只能在协程中使用
        delay(1000L) // 非阻塞的等待 1 秒钟（默认时间单位是毫秒）
        println("World!") // 在延迟后打印输出
    }
    println("Hello,") // 协程已在等待时主线程还在继续
    Thread.sleep(2000L) // 阻塞主线程 2 秒钟来保证 JVM 存活
}

fun bridgeBlocking() = runBlocking {
    println("重写桥接阻塞")
    GlobalScope.launch {
        printWorld()
    }
    println("Hello,") // 主协程在这里会立即执行
    delay(2000L)      // 延迟 2 秒来保证 JVM 存活
}