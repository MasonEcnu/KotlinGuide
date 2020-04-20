package com.mason.guide.coroutines

import kotlinx.coroutines.*

/**
 * Created by mwu on 2020/4/20
 * 协程的取消与超时
 */
fun main() {
    // 取消协程的执行
    cancelCoroutines()
    // 协程的取消是协作的
    // 所有 kotlinx.coroutines 中的挂起函数都是 可被取消的
    checkCancelCoroutines()
    // 使计算代码可取消
    cancelCalculateCoroutinesWithActiveCheck()
    // 常用
    cancelCalculateCoroutinesWithFinally()
    // 运行不能取消的代码块
    runNonCancellable()
    // 超时
    // 在被取消的协程中 CancellationException 被认为是协程执行结束的正常原因
    timeoutCoroutines()
    timeoutWithNullCoroutines()
}

fun timeoutWithNullCoroutines() = runBlocking {
    println("超时-withTimeoutOrNull")
    val result = withTimeoutOrNull(1300L) {
        repeat(1000) { i ->
            println("Job: I'm sleeping $i ...")
            delay(500L)
        }
        "Done"  // 在它运行得到结果之前取消它
    }
    println("Result is $result")
}

fun timeoutCoroutines() = runBlocking {
    println("超时-withTimeout")
    try {
        withTimeout(1300L) {
            repeat(1000) { i ->
                println("Job: I'm sleeping $i ...")
                delay(500L)
            }
        }
    } catch (e: TimeoutCancellationException) {
        println(e.message)
    }
}

fun runNonCancellable() = runBlocking {
    println("运行不能取消的代码块")
    val job = launch {
        try {
            repeat(1000) { i ->
                println("Job: I'm sleeping $i ...")
                delay(500L)
            }
        } finally {
            withContext(NonCancellable) {
                println("Job:: I'm running finally")
                delay(1000L)
                println("Job:: And I've just delayed for 1 sec because I'm non-cancellable")
            }
        }
    }
    delay(2000L)    // 等待一段时间
    println("Main: I'm tired of waiting!")
    job.cancelAndJoin()    // 取消一个作业并等待他结束
    println("Main: Now I can quit.")
}

fun cancelCalculateCoroutinesWithFinally() = runBlocking {
    println("使计算代码可取消--Finally")
    val job = launch {
        try {
            repeat(1000) { i ->
                println("Job: I'm sleeping $i ...")
                delay(500L)
            }
        } finally {
            try {
                // 任何在finally中调用的挂起函数
                // 都会抛出CancellationException异常
                delay(500L)
                println("Job:: I'm running finally")
            } catch (e: CancellationException) {
                println(e.message)
            }
        }
    }
    delay(2000L)    // 等待一段时间
    println("Main: I'm tired of waiting!")
    job.cancelAndJoin()    // 取消一个作业并等待他结束
    println("Main: Now I can quit.")
}

fun cancelCalculateCoroutinesWithActiveCheck() = runBlocking {
    println("使计算代码可取消--Active")
    val startTime = System.currentTimeMillis()
    val job = launch(Dispatchers.Default) {
        var nextPrintTime = startTime
        var i = 0
        while (isActive) { // 一个执行计算的循环，只是为了占用CPU
            // 每秒打印两次消息
            if (System.currentTimeMillis() >= nextPrintTime) {
                println("Job: I'm sleeping ${i++}")
                nextPrintTime += 500L
            }
        }
    }
    delay(1300L)    // 等待一段时间
    println("Main: I'm tired of waiting!")
    job.cancelAndJoin()    // 取消一个作业并等待他结束
    println("Main: Now I can quit.")
}

fun checkCancelCoroutines() = runBlocking {
    // 如果协程正在执行计算任务，并且没有检查取消的话，那么它是不能被取消的
    println("取消协程是协作的")
    val startTime = System.currentTimeMillis()
    // Dispatchers.Default: 用于处理异常
    val job = launch(Dispatchers.Default) {
        var nextPrintTime = startTime
        var i = 0
        while (i < 5) { // 一个执行计算的循环，只是为了占用CPU
            // 每秒打印两次消息
            if (System.currentTimeMillis() >= nextPrintTime) {
                println("Job: I'm sleeping ${i++}")
                nextPrintTime += 500L
            }
        }
    }
    delay(1300L)    // 等待一段时间
    println("Main: I'm tired of waiting!")
    job.cancelAndJoin()    // 取消一个作业并等待他结束
    println("Main: Now I can quit.")
}

fun cancelCoroutines() = runBlocking {
    println("取消一个协程")
    val job = launch {
        repeat(1000) { count ->
            println("Job: I'm sleeping $count...")
            delay(500L)
        }
    }

    delay(1300L) // 延迟一段时间
    println("Main: I'm tired of waiting!")
    job.cancel()    // 取消该作业
    job.join()  // 等待作业结束
    println("Main: Now I can quit.")
}
