package com.maghraby.kmp_template

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.maghraby.kmp_template.di.appModule
import com.maghraby.kmp_template.di.networkModule
import com.maghraby.kmp_template.feature_home.HomeDestination
import com.maghraby.kmp_template.feature_home.HomeNavigationFeature
import com.maghraby.kmp_template.navigation.AppNavigation
import com.maghraby.kmp_template.theme.AppTheme
import com.maghraby.news.navigation.rememberAppNavigator
import org.jetbrains.compose.resources.painterResource

import kmptemplate.shared.generated.resources.Res
import kmptemplate.shared.generated.resources.compose_multiplatform
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

private val navigationFeatures = listOf(HomeNavigationFeature)

@Composable
fun App() {
    KoinApplication(
        configuration = koinConfiguration {
            modules(appModule, networkModule)
        }
    ) {
        val navigator = rememberAppNavigator(
            startDestination = HomeDestination,
            features = navigationFeatures
        )
        AppTheme {
            AppNavigation(navigator = navigator, features = navigationFeatures)
        }
    }
}