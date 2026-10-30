package com.maghraby.news.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.SerializersModule

class AppNavigator(
    private val backStack: NavBackStack<NavKey>
) {
    val currentBackStack: NavBackStack<NavKey>
        get() = backStack

    fun navigateTo(destination: NavKey) {
        backStack.add(destination)
    }

    fun goBack() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }

    fun popToRoot() {
        while (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }
}

@Composable
fun rememberAppNavigator(
    startDestination: NavKey,
    features: List<NavigationFeature>
): AppNavigator {
    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                features.forEach { feature ->
                    feature.run { registerSerializers() }
                }
            }
        },
        startDestination
    )

    return remember(backStack) {
        AppNavigator(backStack)
    }
}
