package com.example.qrstockmateapp.navigation.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

class ScreenModel {

    sealed class HomeScreens(
        val route: String,
        val title: String,
        val icon: ImageVector
    ) {
        object Home : HomeScreens("home", "Home", Icons.Filled.Home)
        object TransactionHistory : HomeScreens("transactionHistory", "History", Icons.Filled.DateRange)
        object Scan : HomeScreens("scan", "Scan QR", Icons.Filled.AddCircle)
        object Search : HomeScreens("search", "Search", Icons.Filled.Search)
        object Profile : HomeScreens("profile", "Profile", Icons.Filled.Person)

    }

    val screensInHomeFromBottomNav = listOf(
        HomeScreens.Home, HomeScreens.TransactionHistory,  HomeScreens.Scan, HomeScreens.Search, HomeScreens.Profile
    )

}