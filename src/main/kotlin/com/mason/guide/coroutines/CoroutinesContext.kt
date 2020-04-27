package com.mason.guide.coroutines

import kotlinx.coroutines.*

/**
 * Created by mwu on 2020/4/21
 * 协程上下文与调度器
 *
 * 协程总是运行在一些以 CoroutineContext 类型为代表的上下文中
 */
@ObsoleteCoroutinesApi
fun main() {
    // 调度器与线程
    // 协程调度器--CoroutineDispatcher
    // 它确定了哪些线程或与线程相对应的协程执行
    assignContextDispatcher()
    // 非受限调度器 vs 受限调度器
    limitedContextDispatcher()
    // 调试协程与线程
    debugCoroutines()
    // 在不同线程间跳转
    jumpBetweenThreads()
    // 上下文中的作业
    jobInContext()
    // 子协程
    // 当一个父协程被取消的时候，它的所有子协程也会被取消
    subCoroutines()
    // 父协程的职责--父协程总是等待所有子协程执行完毕
    fatherCoroutines()
    // 命名协程以用于调试
    namedCoroutines()
    // 组合上下文中的元素
    combineCoroutines()
    // 协程作用域
    coroutinesActionScope()
    // 线程局部数据
    threadLocalData()
}

private val threadLocal = ThreadLocal<String>()

fun threadLocalData() = runBlocking {
    println("线程局部数据")
    threadLocal.set("main")
    println("Pre-main, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
    val job = launch(Dispatchers.Default + threadLocal.asContextElement(value = "launch")) {
        println("Launch start, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
        yield()
        println("After yield, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
    }
    job.join()
    println("Post-main, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
}

class Activity : CoroutineScope by CoroutineScope(Dispatchers.Default) {

//    private val mainScope = MainScope()
//
//    fun destroy() {
//        mainScope.cancel();
//    }

    fun destroy() {
        cancel();
    }

    fun doSomething() {
        // 启动10个协程，且每个工作的时长不同
        repeat(10) { i ->
            launch {
                delay((i + 1) * 200L)   // 延迟200、400、800...毫秒
                println("Coroutine $i is done.")
            }
        }
    }
}

fun coroutinesActionScope() = runBlocking {
    println("协程作用域")
    val activity = Activity()
    activity.doSomething()
    println("Launched coroutines")
    delay(500L)
    println("Destroy activity!")
    activity.destroy()
    delay(1000L)
}

fun combineCoroutines() = runBlocking {
    println("组合上下文中的元素")
    launch(Dispatchers.Default + CoroutineName("test")) {
        println("I's working in thread ${Thread.currentThread().name}")
    }
}

fun namedCoroutines() = runBlocking {
    println("命名协程以用于调试")
    log("Started main coroutine")
    // 运行两个后台值计算
    val v1 = async(CoroutineName("v1coroutine")) {
        delay(500L)
        log("Computing v1")
        252
    }
    val v2 = async(CoroutineName("v2coroutine")) {
        delay(1000L)
        log("Computing v2")
        110
    }
    log("The answer for v1/v2 = ${v1.await() / v2.await()}")
}

fun fatherCoroutines() = runBlocking {
    println("父协程的职责")
    val request = launch {
        repeat(3) { i ->
            launch {
                delay((i + 1) * 200L)
                println("Coroutine $i is done.")
            }
        }
        println("Request: I'm done and I don't explicitly join my children that are still active")
    }
    request.join()  // 等待请求完成，包括其所有的子协程
    println("Now processing of the request is complete")
}

// 启动一个协程来处理某种传入请求，request
fun subCoroutines() = runBlocking {
    println("子协程")
    val request = launch {
        // 孵化了两个子任务，其中一个通过GlobalScope启动
        GlobalScope.launch {
            println("Job1：I run in GlobalScope and execute independently.")
            delay(1000L)
            println("Job1：I am not affected by cancellation of the request.")
        }

        launch {
            delay(100L)
            println("Job2：I am a child of the request coroutine.")
            delay(1000L)
            println("Job2：I will not execute this line if my parent request is cancelled.")
        }
    }
    delay(500L)
    request.cancel()    // 取消请求request执行
    delay(1000L)
    println("Main：Who has survived request cancellation?")
}

fun jobInContext() = runBlocking {
    println("上下文中的作业")
    println("My job is ${coroutineContext[Job]}")
}

@ObsoleteCoroutinesApi
fun jumpBetweenThreads() {
    println("在不同线程间跳转")
    // newSingleThreadContext：创建新的协程上下文，相当于重新启动一个线程
    // use：用于可关闭资源，使用完毕后自动关闭
    newSingleThreadContext("Ctx1").use { ctx1 ->
        newSingleThreadContext("Ctx2").use { ctx2 ->
            // 同一个协程
            runBlocking(ctx1) {
                log("Started in ctx1")
                withContext(ctx2) {
                    log("Working in ctx2")
                }
                log("Back to ctx1")
            }
        }
    }
}

fun debugCoroutines() = runBlocking {
    println("调试协程")
    val a = async {
        log("I'm computing a piece of the answer")
        6
    }
    val b = async {
        log("I'm computing another piece of the answer")
        7
    }
    log("The answer is ${a.await() * b.await()}")
}

fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

fun limitedContextDispatcher() = runBlocking {
    println("受限制的协程上下文调度器")
    launch(Dispatchers.Unconfined) { // 非受限的——将和主线程一起工作
        println("Unconfined      : I'm working in thread ${Thread.currentThread().name}")
        delay(500)
        // 当 delay 函数调用的时候，非受限的那个协程在默认的执行者线程中恢复执行
        println("Unconfined      : After delay in thread ${Thread.currentThread().name}")
    }
    launch { // 父协程的上下文，主 runBlocking 协程
        println("Main runBlocking: I'm working in thread ${Thread.currentThread().name}")
        delay(1000)
        println("Main runBlocking: After delay in thread ${Thread.currentThread().name}")
    }
}

@ObsoleteCoroutinesApi
fun assignContextDispatcher() = runBlocking {
    println("协程上下文调度器")
    // 默认：Dispatchers.Main
    // 不传参数，它从启动了它的 CoroutineScope 中承袭了上下文（以及调度器）
    launch {
        println("Main runBlocking:          I'm working in thread ${Thread.currentThread().name}")
    }

    launch(Dispatchers.Unconfined) {
        println("Unconfined:                I'm working in thread ${Thread.currentThread().name}")
    }

    launch(Dispatchers.Default) {
        println("Default:                   I'm working in thread ${Thread.currentThread().name}")
    }

    launch(newSingleThreadContext("MyOwnThread")) {
        println("NewSingleThreadContext:    I'm working in thread ${Thread.currentThread().name}")
    }
}
