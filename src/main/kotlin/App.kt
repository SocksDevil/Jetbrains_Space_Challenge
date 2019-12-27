@file:JvmName("App")
package com.andrefmrocha.jetbrains
import com.andrefmrocha.jetbrains.model.FileSearch
import com.andrefmrocha.jetbrains.model.Repository
import com.andrefmrocha.jetbrains.model.RepositorySearch
import com.github.kittinunf.fuel.httpGet

const val API_URL = "https://api.github.com"

fun main(args: Array<String>) {

    val repos = ArrayList<Repository>();
    for (i in 0..1){
        val (_, _, result) = "$API_URL/search/repositories?q=language:java&sort=stars&page=$i"
            .httpGet()
            .responseObject(RepositorySearch.Deserializer())

        val (repositorySearch, _) = result
        val repositories = repositorySearch?.items
        repositories?.forEach { repository ->  repos.add(repository)}
    }

    val fileNames = HashMap<String, Int>()

    repos.forEach { repository ->
        val (_, _, result) = "$API_URL/search/code?q=language:java+repo:${repository.full_name}"
            .httpGet()
            .responseObject(FileSearch.Deserializer())

        val (files, _) = result

        files?.items?.forEach { item ->
            val name = item.name.removeSuffix(".java")
            val value = fileNames.getOrDefault(name, 0);
            fileNames[name] = value + 1
        }
    }

    println(fileNames)
}