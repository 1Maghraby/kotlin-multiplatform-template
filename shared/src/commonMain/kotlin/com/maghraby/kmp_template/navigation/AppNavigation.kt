package com.maghraby.kmp_template.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.maghraby.news.navigation.AppNavigator
import com.maghraby.news.navigation.NavigationFeature

@Composable
fun AppNavigation(
    navigator: AppNavigator,
    features: List<NavigationFeature>
) {
    NavDisplay(
        backStack = navigator.currentBackStack,
        onBack = navigator::goBack,
        entryProvider = entryProvider {
            features.forEach { feature ->
                feature.run { registerEntries() }
            }
        }
    )
}