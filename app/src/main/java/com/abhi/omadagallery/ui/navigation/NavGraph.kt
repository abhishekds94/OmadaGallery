package com.abhi.omadagallery.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.abhi.omadagallery.ui.detail.PhotoDetailScreen
import com.abhi.omadagallery.ui.gallery.GalleryScreen

@Composable
fun AppNavHost() {
    val nav = rememberNavController()
    NavHost(nav, startDestination = "gallery") {
        composable("gallery") {
            GalleryScreen(
                onOpen = { p -> nav.navigate("detail/${p.id}") }
            )
        }

        composable(
            route = "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) {
            PhotoDetailScreen(
                onBack = { nav.popBackStack() }
            )
        }
    }
}
