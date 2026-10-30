package com.maghraby.network.client

import com.maghraby.network.model.ApiRequest
import com.maghraby.network.model.ApiResponse
import com.maghraby.network.model.HttpMethod
import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.HttpMethod as KtorHttpMethod

internal class KtorApiClient(
    private val baseUrl: String,
    private val client: HttpClient = HttpClient {
        expectSuccess = false
    }
) : ApiClient {

    override suspend fun execute(request: ApiRequest): ApiResponse {
        val url = "${baseUrl.trimEnd('/')}/${request.path.trimStart('/')}"

        val response = client.request(url) {
            method = when (request.method) {
                HttpMethod.GET -> KtorHttpMethod.Get
                HttpMethod.POST -> KtorHttpMethod.Post
                HttpMethod.PUT -> KtorHttpMethod.Put
                HttpMethod.PATCH -> KtorHttpMethod.Patch
                HttpMethod.DELETE -> KtorHttpMethod.Delete
            }

            url {
                request.queryParameters.forEach { (key, value) ->
                    parameters.append(key, value)
                }
            }

            request.headers.forEach { (key, value) ->
                header(key, value)
            }

            if (request.body != null) {
                contentType(ContentType.Application.Json)
                setBody(request.body)
            }
        }

        return ApiResponse(
            statusCode = response.status.value,
            body = response.bodyAsText(),
            headers = response.headers.entries().associate { entry ->
                entry.key to entry.value
            }
        )
    }
}