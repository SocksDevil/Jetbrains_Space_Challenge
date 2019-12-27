package com.andrefmrocha.jetbrains.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class Repository (
    val id: Int,
    val full_name: String
){
    class Deserializer: ResponseDeserializable<Array<Repository>>{
        override fun deserialize(content: String): Array<Repository>? =
            Gson().fromJson(content, Array<Repository>::class.java)
    }
}