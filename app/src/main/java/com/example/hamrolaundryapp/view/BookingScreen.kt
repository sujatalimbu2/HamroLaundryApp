package com.example.hamrolaundryapp.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.hamrolaundryapp.ui.theme.HamrolaundryAppTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hamrolaundryapp.model.LaundryService
import com.example.hamrolaundryapp.utils.Resource
import com.example.hamrolaundryapp.viewmodel.BookingViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BookingScreen(
    serviceId: String,
    onBackClick: () -> Unit,
    onBookingSuccess: (String) -> Unit,
    onLoginRequired: () -> Unit,
    viewModel: BookingViewModel = viewModel()
) {
    val serviceResource by viewModel.service.collectAsState()
    val bookingResult by viewModel.bookingResult.collectAsState()
    val quantity by viewModel.quantity.collectAsState()
    val totalPrice by viewModel.totalPrice.collectAsState()

    LaunchedEffect(serviceId) {
        viewModel.getService(serviceId)
    }

    BookingScreenContent(
        serviceResource = serviceResource,
        bookingResult = bookingResult,
        quantity = quantity,
        totalPrice = totalPrice,
        onBackClick = onBackClick,
        onBookingSuccess = onBookingSuccess,
        onLoginRequired = onLoginRequired,
        onUpdateQuantity = { viewModel.updateQuantity(it) },
        onBookNow = { clothingType, pickupDate, deliveryDate, address, instructions ->
            viewModel.bookLaundry(clothingType, pickupDate, deliveryDate, address, instructions)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreenContent(
    serviceResource: Resource<LaundryService>,
    bookingResult: Resource<String>?,
    quantity: Int,
    totalPrice: Double,
    onBackClick: () -> Unit,
    onBookingSuccess: (String) -> Unit,
    onLoginRequired: () -> Unit,
    onUpdateQuantity: (Int) -> Unit,
    onBookNow: (String, Date, Date, String, String) -> Unit
) {
    val context = LocalContext.current
    
    var clothingType by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    
    var pickupDate by remember { mutableStateOf(Date()) }
    var deliveryDate by remember { mutableStateOf(Date(System.currentTimeMillis() + 86400000 * 2)) }

    var showPickupPicker by remember { mutableStateOf(false) }
    var showDeliveryPicker by remember { mutableStateOf(false) }

    val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())

    LaunchedEffect(bookingResult) {
        if (bookingResult is Resource.Success) {
            val bookingId = bookingResult.data ?: ""
            onBookingSuccess(bookingId)
        } else if (bookingResult is Resource.Error) {
            val msg = bookingResult.message
            if (msg == "Login Required") {
                Toast.makeText(context, "Login first to book", Toast.LENGTH_SHORT).show()
                onLoginRequired()
            } else {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Service") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
            when (serviceResource) {
                is Resource.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is Resource.Success -> {
                    val service = serviceResource.data!!
                    
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(service.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text(service.description, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Price: Rs. ${service.price.toInt()} / item", fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text("Quantity", fontWeight = FontWeight.Bold)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            IconButton(onClick = { if (quantity > 1) onUpdateQuantity(quantity - 1) }) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease")
                            }
                            Text(quantity.toString(), fontSize = 18.sp, modifier = Modifier.padding(horizontal = 16.dp))
                            IconButton(onClick = { onUpdateQuantity(quantity + 1) }) {
                                Icon(Icons.Default.Add, contentDescription = "Increase")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = clothingType,
                            onValueChange = { clothingType = it },
                            label = { Text("Clothing Type (e.g. Shirts, Pants, Suit)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedCard(
                                onClick = { showPickupPicker = true },
                                modifier = Modifier.weight(1f).padding(end = 8.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Pickup Date", fontSize = 12.sp, color = Color.Gray)
                                    Text(dateFormatter.format(pickupDate), fontWeight = FontWeight.Bold)
                                }
                            }
                            OutlinedCard(
                                onClick = { showDeliveryPicker = true },
                                modifier = Modifier.weight(1f).padding(start = 8.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Delivery Date", fontSize = 12.sp, color = Color.Gray)
                                    Text(dateFormatter.format(deliveryDate), fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Pickup Address") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = instructions,
                            onValueChange = { instructions = it },
                            label = { Text("Special Instructions (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 3
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Total Price", fontSize = 14.sp, color = Color.Gray)
                                Text("Rs. ${totalPrice.toInt()}", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                            }
                            Button(
                                onClick = {
                                    if (clothingType.isBlank() || address.isBlank()) {
                                        Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show()
                                    } else {
                                        onBookNow(clothingType, pickupDate, deliveryDate, address, instructions)
                                    }
                                },
                                modifier = Modifier.height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                enabled = bookingResult !is Resource.Loading
                            ) {
                                if (bookingResult is Resource.Loading) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                } else {
                                    Text("Book Now", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    Text(
                        text = serviceResource.message ?: "Error",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    if (showPickupPicker) {
        MyDatePickerDialog(
            onDateSelected = { 
                pickupDate = it
                showPickupPicker = false 
            },
            onDismiss = { showPickupPicker = false }
        )
    }

    if (showDeliveryPicker) {
        MyDatePickerDialog(
            onDateSelected = { 
                deliveryDate = it
                showDeliveryPicker = false 
            },
            onDismiss = { showDeliveryPicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let {
                    onDateSelected(Date(it))
                }
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Preview(showBackground = true)
@Composable
fun BookingScreenPreview() {
    val mockService = LaundryService(
        id = "1",
        name = "Premium Wash",
        description = "Full wash and dry for all your delicate clothes with fabric softening.",
        price = 120.0,
        completionTime = "24 Hours"
    )

    HamrolaundryAppTheme {
        BookingScreenContent(
            serviceResource = Resource.Success(mockService),
            bookingResult = null,
            quantity = 2,
            totalPrice = 240.0,
            onBackClick = {},
            onBookingSuccess = {},
            onLoginRequired = {},
            onUpdateQuantity = {},
            onBookNow = { _, _, _, _, _ -> }
        )
    }
}
