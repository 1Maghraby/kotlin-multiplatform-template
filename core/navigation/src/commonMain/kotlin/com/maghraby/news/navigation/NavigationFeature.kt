package com.maghraby.news.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.modules.SerializersModuleBuilder

interface NavigationFeature {
    fun EntryProviderScope<NavKey>.registerEntries()

    fun SerializersModuleBuilder.registerSerializers()
}
