package com.andrefmrocha.jetbrains.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class FileSearch (
    val items: Array<File>,
    val incomplete_results: Boolean
){
    class Deserializer: ResponseDeserializable<FileSearch> {
        override fun deserialize(content: String): FileSearch? =
            Gson().fromJson(content, FileSearch::class.java)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileSearch

        if (!items.contentEquals(other.items)) return false
        if (incomplete_results != other.incomplete_results) return false

        return true
    }

    override fun hashCode(): Int {
        var result = items.contentHashCode()
        result = 31 * result + incomplete_results.hashCode()
        return result
    }
}