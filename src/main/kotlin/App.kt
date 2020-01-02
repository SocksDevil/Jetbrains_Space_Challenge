@file:JvmName("App")
package com.andrefmrocha.jetbrains
import com.andrefmrocha.jetbrains.action.FilesStatistics



fun main() {
    val statistics = FilesStatistics()
    statistics.getFiles(10)
    statistics.plotGraph()
}