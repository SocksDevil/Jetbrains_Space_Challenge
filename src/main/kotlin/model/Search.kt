package com.andrefmrocha.jetbrains.model

data class Search<T> (
    val items: Array<T>,
    val incomplete_results: Boolean
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Search<*>

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