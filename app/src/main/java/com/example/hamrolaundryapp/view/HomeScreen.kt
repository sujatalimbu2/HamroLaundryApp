package com.example.hamrolaundryapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.hamrolaundryapp.model.LaundryService
import com.example.hamrolaundryapp.utils.Constants
import com.example.hamrolaundryapp.utils.Resource
import com.example.hamrolaundryapp.viewmodel.HomeViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onServiceClick: (String) -> Unit,
    onViewAllClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val userName by viewModel.userName.collectAsState()
    val popularServicesResource by viewModel.popularServices.collectAsState()
    
    val isAdmin = FirebaseAuth.getInstance().currentUser?.email == Constants.ADMIN_EMAIL

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Welcome,",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = userName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    if (isAdmin) {
                        SuggestionChip(
                            onClick = { /* Already in Admin Mode */ },
                            label = { Text("Admin Mode", fontSize = 10.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(labelColor = MaterialTheme.colorScheme.primary)
                        )
                    }
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Search Bar
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search services...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Promotional Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Get 20% OFF",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "On your first booking",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { /* TODO */ }) {
                            Text("Book Now")
                        }
                    }
                    // Placeholder image or real image
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .padding(end = 16.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Categories",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            val categories = listOf("Wash", "Dry Clean", "Iron", "Premium", "Express")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(categories) { category ->
                    CategoryItem(category)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Popular Services",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onViewAllClick) {
                    Text("View All")
                }
            }

            when (popularServicesResource) {
                is Resource.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is Resource.Success -> {
                    val services = (popularServicesResource as Resource.Success<List<LaundryService>>).data ?: emptyList()
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(services) { service ->
                            PopularServiceCard(service, onServiceClick)
                        }
                    }
                }
                is Resource.Error -> {
                    Text(
                        text = (popularServicesResource as Resource.Error).message ?: "Error",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryItem(name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            // Icon placeholder
            Text(name.take(1), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = name, fontSize = 12.sp)
    }
}

@Composable
fun PopularServiceCard(service: LaundryService, onClick: (String) -> Unit) {
    Card(
        onClick = { onClick(service.id) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            AsyncImage(
                model = service.imageUrl,
                contentDescription = service.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = service.name, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(text = "Rs. ${service.price}", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = service.completionTime, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
