package com.mason.guide.coroutines

import kotlinx.coroutines.*
import java.util.*
import kotlin.system.measureTimeMillis

/**
 * Created by mwu on 2020/4/20
 * 协程-组合挂起函数
 */
fun main() {
    // 默认顺序调用
    defaultSequenceCall()
    // 使用async并发--使用协程进行并发总是显式的
    doSomethingWithAsync()
    // 惰性启动的 async
    doSomethingWithLazyAsync()
    // async 风格的函数
    doSomethingWithAsyncStyle()
    // 使用 async 的结构化并发
    doConcurrentSum()
    doFailedConcurrentSum()
}

fun doFailedConcurrentSum() = runBlocking {
    println("使用 async 的结构化并发--failed")
    try {
        failedConcurrentSum()
    } catch (e: ArithmeticException) {
        println("Computation failed with ArithmeticException")
    }
}

suspend fun failedConcurrentSum(): Int = coroutineScope {
    val one = async {
        try {
            delay(Long.MAX_VALUE) // 模拟一个长时间的运算
            42
        } finally {
            println("First child was cancelled")
        }
    }
    // 如果第二个协程two抛出异常，则同一个父协程下的子协程one会取消
    val two = async<Int> {
        println("Second child throws an exception")
        throw ArithmeticException()
    }
    one.await() + two.await()
}

fun doConcurrentSum() = runBlocking {
    println("使用 async 的结构化并发--success")
    val time = measureTimeMillis {
        println("The answer is ${concurrentSum()}")
    }
    println("Completed in $time ms")
}

suspend fun concurrentSum(): Int = coroutineScope {
    val one = async { doSomethingUsefulOne() }
    val two = async { doSomethingUsefulTwo() }
    one.await() + two.await()
}

fun doSomethingWithAsyncStyle() {
    println("async 风格的函数")
    val time = measureTimeMillis {
        // 在协程外调用GlobalScope.async
        val one = somethingUsefulOneAsync()
        val two = somethingUsefulTwoAsync()
        // 但是等待结果必须调用其它的挂起或者阻塞
        runBlocking {
            println("The answer-- one is ${one.await()}, two is ${two.await()}")
        }
    }
    println("Completed in $time ms")
}

fun somethingUsefulOneAsync() = GlobalScope.async {
    doSomethingUsefulOne()
}

fun somethingUsefulTwoAsync() = GlobalScope.async {
    doSomethingUsefulTwo()
}

fun doSomethingWithLazyAsync() = runBlocking {
    println("使用lazy-async并发")
    val time = measureTimeMillis {
        val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
        val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }
        // 不调用start，就是非并发，顺序执行
        one.start()
        two.start()
        println("The answer-- one is ${one.await()}, two is ${two.await()}")
    }
    println("Completed in $time ms")
}

fun doSomethingWithAsync() = runBlocking {
    println("使用async并发")
    val time = measureTimeMillis {
        val one = async { doSomethingUsefulOne() }
        val two = async { doSomethingUsefulTwo() }
        println("The answer-- one is ${one.await()}, two is ${two.await()}")
    }
    println("Completed in $time ms")
}

fun defaultSequenceCall() = runBlocking {
    println("默认顺序调用")
    val time = measureTimeMillis {
        val one = doSomethingUsefulOne()
        val two = if (one % 2 == 0) {
            doSomethingUsefulTwo()
        } else {
            doSomethingUsefulThree()
        }
        println("The answer-- one is $one, two is $two")
    }
    println("Completed is $time ms.")
}

suspend fun doSomethingUsefulOne(): Int {
    // 假设我们在这里做了一些有用的事
    delay(1000L)
    return Random().nextInt(100)
}

suspend fun doSomethingUsefulTwo(): Int {
    // 假设我们在这里做了一些有用的事
    delay(2000L)
    return 2
}

suspend fun doSomethingUsefulThree(): Int {
    // 假设我们在这里做了一些有用的事
    delay(3000L)
    return 3
}