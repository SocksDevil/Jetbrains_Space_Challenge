@file:JvmName("App")
package com.andrefmrocha.jetbrains
import com.andrefmrocha.jetbrains.action.getFiles
import com.andrefmrocha.jetbrains.action.plotGraph


const val API_URL = "https://api.github.com"

fun main() {
    val fileNames = getFiles(11)
    fileNames?.let {files -> plotGraph(files) }
}