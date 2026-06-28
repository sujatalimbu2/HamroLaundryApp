package com.example.hamrolaundryapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hamrolaundryapp.components.BottomNavigationBar
import com.example.hamrolaundryapp.navigation.Screen
import com.example.hamrolaundryapp.repo.UserRepoImpl
import com.example.hamrolaundryapp.view.ForgetPasswordScreen
import com.example.hamrolaundryapp.view.LoginScreen
import com.example.hamrolaundryapp.view.RegistrationScreen
import com.example.hamrolaundryapp.viewmodel.UserViewModel
import com.example.hamrolaundryapp.view.AdminDashboardScreen
import com.example.hamrolaundryapp.view.BookingConfirmationScreen
import com.example.hamrolaundryapp.view.BookingScreen
import com.example.hamrolaundryapp.view.HistoryScreen
import com.example.hamrolaundryapp.view.HomeScreen
import com.example.hamrolaundryapp.view.ProfileScreen
import com.example.hamrolaundryapp.view.ServicesScreen

@Composable
fun MainScreen(startDestination: String = Screen.Home.route) {
    val navController = rememberNavController()
    val userViewModel = remember { UserViewModel(UserRepoImpl()) }
    
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Auth Routes
            composable(Screen.Login.route) {
                LoginScreen(
                    userViewModel = userViewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onRegisterClick = { navController.navigate(Screen.Register.route) },
                    onForgetPasswordClick = { navController.navigate(Screen.ForgotPassword.route) },
                    onBackClick = {
                        if (!navController.popBackStack()) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(0)
                            }
                        }
                    }
                )
            }
            composable(Screen.Register.route) {
                RegistrationScreen(
                    userViewModel = userViewModel,
                    onRegisterSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onLoginClick = { navController.navigate(Screen.Login.route) },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Screen.ForgotPassword.route) {
                ForgetPasswordScreen(
                    viewModel = userViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            // Main App Routes
            composable(Screen.Home.route) {
                HomeScreen(
                    onServiceClick = { serviceId ->
                        navController.navigate(Screen.Booking.createRoute(serviceId))
                    },
                    onViewAllClick = {
                        navController.navigate(Screen.Services.route)
                    }
                )
            }
            composable(Screen.Services.route) {
                ServicesScreen(
                    onBackClick = { navController.popBackStack() },
                    onServiceClick = { serviceId ->
                        navController.navigate(Screen.Booking.createRoute(serviceId))
                    }
                )
            }
            composable(Screen.Booking.route) { backStackEntry ->
                val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
                BookingScreen(
                    serviceId = serviceId,
                    onBackClick = { navController.popBackStack() },
                    onBookingSuccess = { bookingId ->
                        navController.navigate(Screen.Confirmation.createRoute(bookingId)) {
                            popUpTo(Screen.Home.route)
                        }
                    },
                    onLoginRequired = {
                        navController.navigate(Screen.Login.route)
                    }
                )
            }
            composable(Screen.Confirmation.route) { backStackEntry ->
                val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
                BookingConfirmationScreen(
                    bookingId = bookingId,
                    onHomeClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onTrackOrderClick = { /* TODO */ }
                )
            }
            composable(Screen.History.route) {
                HistoryScreen(
                    onBookingClick = { bookingId ->
                        navController.navigate(Screen.Tracking.createRoute(bookingId))
                    },
                    onLoginClick = { navController.navigate(Screen.Login.route) }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0)
                        }
                    },
                    onLoginClick = {
                        navController.navigate(Screen.Login.route)
                    },
                    onAdminPortalClick = {
                        navController.navigate(Screen.AdminDashboard.route)
                    }
                )
            }
            composable(Screen.AdminDashboard.route) {
                AdminDashboardScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

