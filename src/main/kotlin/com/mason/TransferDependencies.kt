package com.mason

import java.io.File
import kotlin.system.exitProcess

/**
 * Created by mwu on 2020/4/17
 */

private const val FILE_NAME = "dependencies.txt"
private const val COMPILE_PATTERN = "implementation(\"%s:%s:%s\")\n"
private val EXCLUDE_PARAMS = setOf("name", "version")

fun main() {
    val fileUrl = Thread.currentThread().contextClassLoader.getResource(FILE_NAME)
    if (fileUrl == null) {
        println("$FILE_NAME 加载资源失败")
        exitProcess(0)
    }
    val dependFile = File(fileUrl.file)
    if (!dependFile.exists()) {
        println("$FILE_NAME 文件不存在")
        exitProcess(0)
    }

    val reg = "(?<=\').*?(?=\')".toRegex()

    val params = arrayListOf<String>()
    dependFile.forEachLine { line ->
        if (line.startsWith("//")) return@forEachLine
        reg.findAll(line).forEach { matchResult ->
            val groups = matchResult.groupValues
            if (groups.isNotEmpty() && !EXCLUDE_PARAMS.any { groups.any { group -> group.contains(it) } }) {
                params.add(groups[0])
            }
        }
    }
    if (params.size == 3) {
        println(String.format(COMPILE_PATTERN, params[0], params[1], params[2]))
    } else {
        println(params)
    }
}