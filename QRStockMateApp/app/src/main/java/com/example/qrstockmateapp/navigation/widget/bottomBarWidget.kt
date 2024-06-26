package com.example.qrstockmateapp.navigation.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.qrstockmateapp.navigation.model.ScreenModel
import com.example.qrstockmateapp.navigation.repository.DataRepository
import com.example.qrstockmateapp.ui.theme.BlueSystem
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Straight
import com.exyte.animatednavbar.animation.indendshape.StraightIndent
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.exyte.animatednavbar.utils.noRippleClickable
import kotlinx.coroutines.delay


@Composable
fun AnimatedBottomBar(
    screens: List<ScreenModel.HomeScreens>,
    navController: NavController,
) {
    var visible by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        ColorButtonNavBar(
            screens = screens,
            navController = navController,
        )
    }

    LaunchedEffect(navController) {
        delay(900)
        navController.addOnDestinationChangedListener { _, _, _ ->
            visible = true
        }
    }
}

@Composable
fun AnimatedOutBottomBar(
    screens: List<ScreenModel.HomeScreens>,
    navController: NavController,
) {
    var visible by remember { mutableStateOf(true) }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        ColorButtonNavBar(
            screens = screens,
            navController = navController,
        )
    }

    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, _, _ ->
            visible = false
        }
    }
}


@Composable
fun ColorButtonNavBar(screens: List<ScreenModel.HomeScreens>, navController: NavController) {
    var selectedIndex by remember { mutableIntStateOf(DataRepository.getCurrentScreenIndex()) }

    AnimatedNavigationBar(
        modifier = Modifier
            .shadow(5.dp, shape = RoundedCornerShape(25.dp), ambientColor = Color(0xff5a79ba), spotColor = Color(0xff5a79ba))
            .padding(6.dp) // Ajusta el relleno según tus necesidades
            .height(65.dp),
        selectedIndex = selectedIndex,
        barColor = MaterialTheme.colorScheme.secondaryContainer,
        ballColor =  BlueSystem,
        cornerRadius = shapeCornerRadius(25.dp),
        ballAnimation = Straight(
            spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessVeryLow)
        ),
        indentAnimation = StraightIndent(
            indentWidth = 56.dp,
            indentHeight = 15.dp,
            animationSpec = tween(1200)
        )
    ) {
        val backStackEntry = navController.currentBackStackEntryAsState()

        screens.forEachIndexed { index, screens ->
            val currentRoute = backStackEntry.value?.destination?.route
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .noRippleClickable {
                        selectedIndex = index
                        if (currentRoute != screens.route) {
                            navController.navigate(screens.route)
                            DataRepository.setCurrentScreenIndex(index)
                        }
                     }
                , contentAlignment = Alignment.Center
            ){
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = if(selectedIndex == index) screens.icon else screens.iconO,
                        contentDescription = "",
                        tint = if(selectedIndex == index)  BlueSystem else  BlueSystem,
                        modifier = if (selectedIndex == index) Modifier.size(22.dp) else Modifier.size(21.dp)
                    )
                    if (selectedIndex == index){
                        Text(
                            //if (selected) screens.title else "", // Label
                            screens.title,
                            fontWeight = FontWeight.SemiBold,
                            color = if(selectedIndex == index)  Color(0xff5a79ba) else Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}


