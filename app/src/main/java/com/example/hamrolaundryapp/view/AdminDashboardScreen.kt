package com.example.hamrolaundryapp.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hamrolaundryapp.model.Booking
import com.example.hamrolaundryapp.model.LaundryService
import com.example.hamrolaundryapp.utils.Resource
import com.example.hamrolaundryapp.viewmodel.AdminViewModel
import com.example.hamrolaundryapp.viewmodel.ServiceViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onBackClick: () -> Unit,
    adminViewModel: AdminViewModel = viewModel(),
    serviceViewModel: ServiceViewModel = viewModel()
) {
    val bookingsResource by adminViewModel.allBookings.collectAsState()
    val servicesResource by serviceViewModel.services.collectAsState()
    val context = LocalContext.current
    val dateFormatter = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Orders", "Services")

    var showStatusDialog by remember { mutableStateOf<Booking?>(null) }
    var showServiceDialog by remember { mutableStateOf<LaundryService?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Panel", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTab == 1) {
                FloatingActionButton(onClick = { showServiceDialog = LaundryService() }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Service")
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (selectedTab == 0) {
                    // Orders Tab
                    when (bookingsResource) {
                        is Resource.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        is Resource.Success -> {
                            val bookings = (bookingsResource as Resource.Success<List<Booking>>).data ?: emptyList()
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(bookings) { booking ->
                                    AdminBookingItem(
                                        booking = booking,
                                        date = dateFormatter.format(booking.createdAt.toDate()),
                                        onStatusClick = { showStatusDialog = booking },
                                        onDeleteClick = { adminViewModel.deleteBooking(booking.id) }
                                    )
                                }
                            }
                        }
                        is Resource.Error -> Text(text = (bookingsResource as Resource.Error).message ?: "Error", color = Color.Red, modifier = Modifier.align(Alignment.Center))
                    }
                } else {
                    // Services Tab
                    when (servicesResource) {
                        is Resource.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        is Resource.Success -> {
                            val services = (servicesResource as Resource.Success<List<LaundryService>>).data ?: emptyList()
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(services) { service ->
                                    AdminServiceItem(
                                        service = service,
                                        onEditClick = { showServiceDialog = service },
                                        onDeleteClick = { adminViewModel.deleteService(service.id) }
                                    )
                                }
                            }
                        }
                        is Resource.Error -> Text(text = (servicesResource as Resource.Error).message ?: "Error", color = Color.Red, modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        }
    }

    if (showStatusDialog != null) {
        StatusUpdateDialog(
            currentStatus = showStatusDialog!!.status,
            onDismiss = { showStatusDialog = null },
            onStatusSelected = { newStatus ->
                adminViewModel.updateStatus(showStatusDialog!!.id, newStatus)
                showStatusDialog = null
                Toast.makeText(context, "Status updated", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showServiceDialog != null) {
        ServiceEditDialog(
            service = showServiceDialog!!,
            onDismiss = { showServiceDialog = null },
            onSave = { updatedService ->
                adminViewModel.saveService(updatedService)
                showServiceDialog = null
                Toast.makeText(context, "Service saved", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun AdminServiceItem(service: LaundryService, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = service.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "Rs. ${service.price.toInt()}", color = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onEditClick) { Icon(Icons.Default.Edit, contentDescription = "Edit") }
            IconButton(onClick = onDeleteClick) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceEditDialog(service: LaundryService, onDismiss: () -> Unit, onSave: (LaundryService) -> Unit) {
    var name by remember { mutableStateOf(service.name) }
    var price by remember { mutableStateOf(service.price.toString()) }
    var description by remember { mutableStateOf(service.description) }
    var time by remember { mutableStateOf(service.completionTime) }
    var category by remember { mutableStateOf(service.category) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (service.id.isEmpty()) "Add Service" else "Edit Service") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
                OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Completion Time") })
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") })
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(service.copy(name = name, price = price.toDoubleOrNull() ?: 0.0, description = description, completionTime = time, category = category))
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AdminBookingItem(
    booking: Booking,
    date: String,
    onStatusClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = booking.serviceName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "User ID: ${booking.userId.take(8)}...", fontSize = 12.sp, color = Color.Gray)
                }
                StatusBadge(status = booking.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Address: ${booking.pickupAddress}", fontSize = 14.sp)
            Text(text = "Price: Rs. ${booking.totalPrice.toInt()}", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
                Button(onClick = onStatusClick, contentPadding = PaddingValues(horizontal = 12.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Update Status", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun StatusUpdateDialog(
    currentStatus: String,
    onDismiss: () -> Unit,
    onStatusSelected: (String) -> Unit
) {
    val statuses = listOf(
        "Pending", "Pickup Scheduled", "Collected", 
        "Washing", "Ironing", "Out for Delivery", "Completed"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Status") },
        text = {
            Column {
                statuses.forEach { status ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (status == currentStatus),
                            onClick = { onStatusSelected(status) }
                        )
                        Text(
                            text = status,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
