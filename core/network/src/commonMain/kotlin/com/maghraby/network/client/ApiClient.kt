package com.maghraby.network.client

import com.maghraby.network.model.ApiRequest
import com.maghraby.network.model.ApiResponse

interface ApiClient {
    suspend fun execute(request: ApiRequest): ApiResponse
}