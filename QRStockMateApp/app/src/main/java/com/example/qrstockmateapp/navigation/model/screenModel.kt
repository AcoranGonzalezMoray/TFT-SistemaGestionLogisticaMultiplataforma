package com.example.qrstockmateapp.navigation.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.qrstockmateapp.R
import com.example.qrstockmateapp.navigation.repository.DataRepository

class ScreenModel {

    sealed class HomeScreens(
        val route: String,
        val title: String,
        val icon: ImageVector
    ) {
        object Home : HomeScreens("home", "Home", Icons.Filled.Home)
        object TransactionHistory : HomeScreens("transactionHistory", "History", Icons.Filled.DateRange)
        object Scan : HomeScreens("scan", "Scan QR", Icons.Filled.AddCircle)
        object Carrier : HomeScreens("carrier", "Carrier", Icons.Filled.LocationOn)
        object Search : HomeScreens("search", "Search", Icons.Filled.Search)
        object Profile : HomeScreens("profile", "Profile", Icons.Filled.Person)

    }


    val screensInHomeFromBottomNavFun: () -> List<ScreenModel.HomeScreens> = {
        if (DataRepository.getUser()?.role == 4) {
            listOf(
                ScreenModel.HomeScreens.Home,
                ScreenModel.HomeScreens.TransactionHistory,
                ScreenModel.HomeScreens.Carrier,
                ScreenModel.HomeScreens.Search,
                ScreenModel.HomeScreens.Profile
            )
        } else {
            listOf(
                ScreenModel.HomeScreens.Home,
                ScreenModel.HomeScreens.TransactionHistory,
                ScreenModel.HomeScreens.Scan,
                ScreenModel.HomeScreens.Search,
                ScreenModel.HomeScreens.Profile
            )
        }
    }

    val screensInHomeFromBottomNav = screensInHomeFromBottomNavFun()

}
