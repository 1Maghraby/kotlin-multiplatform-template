package com.maghraby.kmp_template.feature_home

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.maghraby.news.navigation.NavigationFeature
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Serializable
data object HomeDestination : NavKey

object HomeNavigationFeature : NavigationFeature {
    override fun EntryProviderScope<NavKey>.registerEntries() {
        entry<HomeDestination> {
            HomeScreen()
        }
    }

    override fun SerializersModuleBuilder.registerSerializers() {
        polymorphic(NavKey::class) {
            subclass(HomeDestination::class)
        }
    }
}
