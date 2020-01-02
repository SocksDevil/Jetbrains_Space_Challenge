package com.andrefmrocha.jetbrains.action

import com.andrefmrocha.jetbrains.model.File
import com.andrefmrocha.jetbrains.model.FileQuery
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


class FilesStatistics {
    private val fileURLs: Array<String>? = null
    private val classNames: HashMap<String, Int> = HashMap()
    private val API_URL = "https://api.github.com"
    private inline fun <reified T : Any> getRequest(url: String, username: String, token: String): T? {
        val (_, _, result) = url
            .httpGet()
            .authentication()
            .basic(username, token)
            .responseObject<T>()
        val (body, _) = result
        return body
    }

    private fun getFileContent(item: FileQuery, username: String, token: String): String? =
        this.getRequest<File>(item.url, username, token)
            ?.let { file ->
                return file.download_url.httpGet()
                    .authentication()
                    .basic(username, token)
                    .responseString()
                    .third.component1()
            }


    private fun getRepositoryInformation(username: String, token: String, repository: Repository) {
        val files =
            this.getRequest<Search<FileQuery>>(
                "$API_URL/search/code?q=language:java+repo:${repository.full_name}",
                username,
                token
            )

        val filesBody = files?.items?.map { item ->
            this.getFileContent(item, username, token)
        }

        val pattern = """(private|public|protected)\sclass\s\w+""".toRegex()
        filesBody?.forEach { it ->
            it?.let { body ->
                pattern.findAll(body).map { match -> match.value }.toList().forEach { match ->
                    val className = match.subSequence(match.indexOf("class") + 6, match.length).toString()
                    classNames[className] = classNames.getOrDefault(className, 0) + 1
                }
            }
        }
    }

    fun getFiles(numPages: Int): HashMap<String, Int>? {
        val dotenv = dotenv()
        dotenv["USERNAME"]?.let { username ->
            dotenv["TOKEN"]?.let { token ->
                val repos = ArrayList<Repository>()
                for (i in 0 until numPages) {
                    val repositorySearch = this.getRequest<Search<Repository>>(
                        "$API_URL/search/repositories?q=language:java&sort=stars&page=$i",
                        username,
                        token
                    )
                    val repositories = repositorySearch?.items
                    repositories?.forEach { repository -> getRepositoryInformation(username, token, repository) }
                }

                val fileNames = HashMap<String, Int>()

                repos.forEach { repository ->
                    this.getRepositoryInformation(username, token, repository)
                }
                return fileNames
            }
        }
        return null
    }

    fun plotGraph() {
        val nameColumn = "names"
        val valuesColumn = "values"
        val tableName = "Popularity of Java class names"
        val classNames: Table = Table.create(tableName)
            .addColumns(
                StringColumn.create(nameColumn, classNames.keys.toTypedArray()),
                DoubleColumn.create(valuesColumn, classNames.values.toTypedArray())
            )


        Plot.show(
            VerticalBarPlot.create(
                tableName,
                classNames.sortDescendingOn(valuesColumn).first(20), nameColumn, valuesColumn
            )
        )
    }
}



