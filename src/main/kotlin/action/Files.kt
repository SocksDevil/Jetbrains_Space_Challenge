package com.andrefmrocha.jetbrains.action

import com.andrefmrocha.jetbrains.API_URL
import com.andrefmrocha.jetbrains.model.File
import com.andrefmrocha.jetbrains.model.Repository
import com.andrefmrocha.jetbrains.model.Search
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.fuel.httpGet
import io.github.cdimascio.dotenv.dotenv
import tech.tablesaw.api.DoubleColumn
import tech.tablesaw.api.StringColumn
import tech.tablesaw.api.Table
import tech.tablesaw.plotly.Plot
import tech.tablesaw.plotly.api.VerticalBarPlot

fun plotGraph(fileNames: HashMap<String, Int>){
    val nameColumn = "names"
    val valuesColumn = "values"
    val tableName = "Popularity of Java class names"
    val classNames: Table = Table.create(tableName)
        .addColumns(
            StringColumn.create(nameColumn, fileNames.keys.toTypedArray()),
            DoubleColumn.create(valuesColumn, fileNames.values.toTypedArray())
        )


    Plot.show(
        VerticalBarPlot.create(tableName,
            classNames.sortDescendingOn(valuesColumn).first(20), nameColumn, valuesColumn))
}

fun getFiles(numPages: Int): HashMap<String, Int>?{
    val dotenv = dotenv()
    dotenv["USERNAME"]?.let { username ->
        dotenv["TOKEN"]?.let { token ->
            val repos = ArrayList<Repository>()
            for (i in 0 until numPages) {
                val (_, _, result) = "$API_URL/search/repositories?q=language:java&sort=stars&page=$i"
                    .httpGet()
                    .authentication()
                    .basic(username, token)
                    .responseObject<Search<Repository>>()

                val (repositorySearch, _) = result
                val repositories = repositorySearch?.items
                repositories?.forEach { repository -> repos.add(repository) }
            }

            val fileNames = HashMap<String, Int>()

            repos.forEach { repository ->
                val (_, _, result) = "$API_URL/search/code?q=language:java+repo:${repository.full_name}"
                    .httpGet()
                    .authentication()
                    .basic(username, token)
                    .responseObject<Search<File>>()

                val (files, _) = result

                files?.items?.forEach { item ->
                    val name = item.name.removeSuffix(".java")
                    val value = fileNames.getOrDefault(name, 0)
                    fileNames[name] = value + 1
                }
            }
            return fileNames
        }
    }
    return null
}
