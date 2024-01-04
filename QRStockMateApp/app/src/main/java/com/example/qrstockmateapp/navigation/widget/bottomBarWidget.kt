package com.example.qrstockmateapp.navigation.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.qrstockmateapp.navigation.model.ScreenModel

@Composable

fun BottomBar(
    modifier: Modifier = Modifier,
    screens: List<ScreenModel.HomeScreens>,
    navController: NavController,

    ) {
    BottomNavigation(
        modifier=Modifier
            .clip(RoundedCornerShape(18.dp))
            .padding(4.dp) // Ajusta el relleno según tus necesidades
            .fillMaxWidth()
            .shadow(4.dp, shape = RoundedCornerShape(18.dp))
            .border(0.5.dp, Color(0xff5a79ba), shape = RoundedCornerShape(18.dp)),

    ) {
        val backStackEntry = navController.currentBackStackEntryAsState()
        screens.forEach {
                screens ->
            val currentRoute = backStackEntry.value?.destination?.route;
            val selected = currentRoute == screens.route

            BottomNavigationItem(
                modifier = Modifier.background(Color.White),
                icon = {
                    Icon(
                        imageVector = screens.icon,
                        contentDescription = "",
                        tint = if (selected)  Color(0xff5a79ba) else Color.DarkGray,
                        modifier = Modifier
                            .size(25.dp)
                            .align(Alignment.CenterVertically) // Centro vertical
                    )

                },
                selected = selected,
                label = {
                    Text(
                        //if (selected) screens.title else "", // Label
                        screens.title,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selected)  Color(0xff5a79ba) else Color.DarkGray
                    )
                },

                onClick = {
                    if (currentRoute != screens.route) {
                        navController.navigate(screens.route)

                    }

                }

            )
        }

    }

}