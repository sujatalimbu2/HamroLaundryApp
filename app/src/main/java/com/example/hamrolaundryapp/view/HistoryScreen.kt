package com.example.hamrolaundryapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.hamrolaundryapp.ui.theme.HamrolaundryAppTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hamrolaundryapp.model.Booking
import com.example.hamrolaundryapp.utils.Resource
import com.example.hamrolaundryapp.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBookingClick: (String) -> Unit,
    onLoginClick: () -> Unit,
    viewModel: HistoryViewModel = viewModel()
) {
    val historyResource by viewModel.history.collectAsState()
    
    HistoryScreenContent(
        historyResource = historyResource,
        onBookingClick = onBookingClick,
        onLoginClick = onLoginClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreenContent(
    historyResource: Resource<List<Booking>>,
    onBookingClick: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Booking History", fontWeight = FontWeight.Bold) })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (historyResource) {
                is Resource.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is Resource.Success -> {
                    val history = historyResource.data ?: emptyList()
                    if (history.isEmpty()) {
                        EmptyHistoryState()
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(history) { booking ->
                                HistoryItem(booking, dateFormatter, onBookingClick)
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    val message = historyResource.message ?: "Error"
                    if (message == "Login required to view history") {
                        LoginPromptState(onLoginClick)
                    } else {
                        Text(text = message, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItem(booking: Booking, formatter: SimpleDateFormat, onClick: (String) -> Unit) {
    Card(
        onClick = { onClick(booking.id) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = booking.serviceName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                StatusBadge(status = booking.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Total: Rs. ${booking.totalPrice.toInt()}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                Text(text = booking.createdAt.toDate().let { formatter.format(it) }, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val color = when (status) {
        "Completed" -> Color(0xFF4CAF50)
        "Pending" -> Color(0xFFFF9800)
        "Cancelled" -> Color.Red
        else -> MaterialTheme.colorScheme.primary
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EmptyHistoryState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color.LightGray)
        Spacer(modifier = Modifier.height(16.dp))
        Text("No bookings yet", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("Your laundry history will appear here", color = Color.Gray)
    }
}

@Composable
fun LoginPromptState(onLoginClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color.LightGray)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Login Required", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Please login to view your booking history", color = Color.Gray, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onLoginClick, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(12.dp)) {
            Text("Login Now")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    HamrolaundryAppTheme {
        HistoryScreenContent(
            historyResource = Resource.Success(
                listOf(
                    Booking(
                        id = "1",
                        serviceName = "Wash & Fold",
                        totalPrice = 500.0,
                        status = "Completed",
                        createdAt = com.google.firebase.Timestamp.now()
                    ),
                    Booking(
                        id = "2",
                        serviceName = "Dry Cleaning",
                        totalPrice = 1200.0,
                        status = "Pending",
                        createdAt = com.google.firebase.Timestamp.now()
                    )
                )
            ),
            onBookingClick = {},
            onLoginClick = {}
        )
    }
}
