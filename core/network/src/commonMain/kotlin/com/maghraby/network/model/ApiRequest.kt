package com.maghraby.network.model

data class ApiRequest(
    val path: String,
    val method: HttpMethod = HttpMethod.GET,
    val queryParameters: Map<String, String> = emptyMap(),
    val headers: Map<String, String> = emptyMap(),
    val body: String? = null
)