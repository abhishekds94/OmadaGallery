package com.abhi.omadagallery.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavHost() {
    val nav = rememberNavController()
    NavHost(nav, startDestination = "gallery") {
        composable("gallery") {
            nav.navigate("detail/${it.id}")
        }
        composable("detail/{id}") {
            // Todo
        }
    }
}