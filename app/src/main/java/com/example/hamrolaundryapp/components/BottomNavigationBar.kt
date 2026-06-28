package com.example.hamrolaundryapp.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.hamrolaundryapp.navigation.Screen

data class NavigationItem(val title: String, val route: String, val icon: ImageVector)

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        NavigationItem("Home", Screen.Home.route, Icons.Default.Home),
        NavigationItem("Services", Screen.Services.route, Icons.Default.LocalLaundryService),
        NavigationItem("History", Screen.History.route, Icons.Default.History),
        NavigationItem("Profile", Screen.Profile.route, Icons.Default.Person),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Show bottom bar only on main screens
    val showBottomBar = items.any { it.route == currentDestination?.route }

    if (showBottomBar) {
        NavigationBar {
            items.forEach { item ->
                NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = item.title) },
                    label = { Text(item.title) },
                    selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}
