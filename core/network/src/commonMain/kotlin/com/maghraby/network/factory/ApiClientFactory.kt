package com.maghraby.network.factory

import com.maghraby.network.client.ApiClient
import com.maghraby.network.client.KtorApiClient

fun createApiClient(baseUrl: String): ApiClient =
    KtorApiClient(baseUrl = baseUrl)