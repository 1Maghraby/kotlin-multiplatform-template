package com.maghraby.kmp_template.di

import com.maghraby.network.client.ApiClient
import com.maghraby.network.factory.createApiClient
import org.koin.dsl.module

val networkModule = module {
    single<ApiClient> {
        createApiClient(NETWORK_BASE_URL)
    }
}
