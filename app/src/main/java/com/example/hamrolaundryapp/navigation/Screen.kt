package com.example.hamrolaundryapp.navigation

sealed class Screen(val route: String) {
    // Auth Screens
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    
    // Main App Screens
    object Home : Screen("home")
    object Services : Screen("services")
    object Booking : Screen("booking/{serviceId}") {
        fun createRoute(serviceId: String) = "booking/$serviceId"
    }
    object Confirmation : Screen("confirmation/{bookingId}") {
        fun createRoute(bookingId: String) = "confirmation/$bookingId"
    }
    object Tracking : Screen("tracking/{bookingId}") {
        fun createRoute(bookingId: String) = "tracking/$bookingId"
    }
    object History : Screen("history")
    object Profile : Screen("profile")
    object Payment : Screen("payment/{bookingId}") {
        fun createRoute(bookingId: String) = "payment/$bookingId"
    }
    object AdminDashboard : Screen("admin_dashboard")
}
