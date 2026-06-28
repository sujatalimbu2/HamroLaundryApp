package com.example.hamrolaundryapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hamrolaundryapp.model.Booking
import com.example.hamrolaundryapp.utils.Resource
import com.example.hamrolaundryapp.viewmodel.BookingConfirmationViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingConfirmationScreen(
    bookingId: String,
    onHomeClick: () -> Unit,
    onTrackOrderClick: (String) -> Unit,
    viewModel: BookingConfirmationViewModel = viewModel()
) {
    val bookingResource by viewModel.booking.collectAsState()
    val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())

    LaunchedEffect(bookingId) {
        viewModel.fetchBooking(bookingId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmation") },
                navigationIcon = {
                    IconButton(onClick = onHomeClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (bookingResource) {
                is Resource.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is Resource.Success -> {
                    val booking = (bookingResource as Resource.Success<Booking>).data!!
                    
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(40.dp))
                        
                        // Success Icon
                        Surface(
                            modifier = Modifier.size(100.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(60.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Booking Confirmed!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Text(
                            text = "Your laundry request has been successfully placed.",
                            textAlign = TextAlign.Center,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Booking Details Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                DetailRow("Booking ID", booking.id)
                                DetailRow("Service", booking.serviceName)
                                DetailRow("Quantity", "${booking.quantity} Items")
                                DetailRow("Total Price", "Rs. ${booking.totalPrice.toInt()}")
                                DetailRow("Pickup Date", booking.pickupDate?.toDate()?.let { dateFormatter.format(it) } ?: "-")
                                DetailRow("Delivery Date", booking.deliveryDate?.toDate()?.let { dateFormatter.format(it) } ?: "-")
                                DetailRow("Status", booking.status)
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        Button(
                            onClick = { onTrackOrderClick(booking.id) },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Track My Order", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = onHomeClick,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Back to Home", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                is Resource.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Error: ${(bookingResource as Resource.Error).message}", color = Color.Red)
                        Button(onClick = onHomeClick, modifier = Modifier.padding(top = 16.dp)) {
                            Text("Go Back")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp)
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
    }
}
