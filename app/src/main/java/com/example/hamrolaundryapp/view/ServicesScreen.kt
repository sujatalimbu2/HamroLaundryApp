package com.example.hamrolaundryapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.hamrolaundryapp.ui.theme.HamrolaundryAppTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.hamrolaundryapp.model.LaundryService
import com.example.hamrolaundryapp.utils.Resource
import com.example.hamrolaundryapp.viewmodel.ServiceViewModel

@Composable
fun ServicesScreen(
    onBackClick: () -> Unit,
    onServiceClick: (String) -> Unit,
    viewModel: ServiceViewModel = viewModel()
) {
    val servicesResource by viewModel.services.collectAsState()

    ServicesScreenContent(
        servicesResource = servicesResource,
        onBackClick = onBackClick,
        onServiceClick = onServiceClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreenContent(
    servicesResource: Resource<List<LaundryService>>,
    onBackClick: () -> Unit,
    onServiceClick: (String) -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Laundry Services", fontWeight = FontWeight.Bold) },
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
            when (servicesResource) {
                is Resource.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is Resource.Success -> {
                    val services = servicesResource.data ?: emptyList()
                    if (services.isEmpty()) {
                        Text(
                            text = "No services available",
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.Gray
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(services) { service ->
                                ServiceListItem(service, onServiceClick)
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = servicesResource.message ?: "An error occurred",
                            color = Color.Red,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceListItem(service: LaundryService, onClick: (String) -> Unit) {
    Card(
        onClick = { onClick(service.id) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Icon/Image with background
            Surface(
                modifier = Modifier
                    .size(85.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (service.imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = service.imageUrl,
                            contentDescription = service.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.LocalLaundryService,
                            contentDescription = null,
                            modifier = Modifier.size(42.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    // Small Badge for Category
                    if (service.category.isNotEmpty()) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(4.dp),
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.primary,
                        ) {
                            Text(
                                text = service.category,
                                fontSize = 8.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Middle: Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = service.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1D2939),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = service.description,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(10.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = service.completionTime,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right: Price & Action
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Rs. ${service.price.toInt()}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "per item",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = { onClick(service.id) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        "Add", 
                        fontSize = 13.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ServicesScreenPreview() {
    val mockServices = listOf(
        LaundryService(
            id = "1",
            name = "Wash & Fold",
            description = "Standard laundry service including washing, drying, and neat folding.",
            price = 50.0,
            completionTime = "24 Hours",
            category = "Popular"
        ),
        LaundryService(
            id = "2",
            name = "Dry Cleaning",
            description = "Premium care for your delicate garments using professional solvents.",
            price = 150.0,
            completionTime = "48 Hours",
            category = "Premium"
        ),
        LaundryService(
            id = "3",
            name = "Ironing Only",
            description = "Crisp and professional ironing for your shirts and trousers.",
            price = 30.0,
            completionTime = "12 Hours",
            category = "Basic"
        )
    )

    HamrolaundryAppTheme {
        ServicesScreenContent(
            servicesResource = Resource.Success(mockServices),
            onBackClick = {},
            onServiceClick = {}
        )
    }
}
