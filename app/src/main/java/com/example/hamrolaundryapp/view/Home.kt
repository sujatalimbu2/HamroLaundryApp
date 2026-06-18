package com.example.hamrolaundryapp.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hamrolaundryapp.model.ProductModel
import com.example.hamrolaundryapp.repo.ProductRepoImpl
import com.example.hamrolaundryapp.viewmodel.ProductViewModel

class Home : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomeScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val productViewModel = remember { ProductViewModel(ProductRepoImpl()) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        productViewModel.getAllProduct()
    }

    val allProduct by productViewModel.allProducts.observeAsState(initial = emptyList())
    val loading = productViewModel.loading.observeAsState(initial = false )
    
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                title = {
                    Text("Product Dashboard", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        (context as? ComponentActivity)?.finish()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, AddProduct::class.java)
                    context.startActivity(intent)
                },
                containerColor = Color.Black,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if(loading.value){
                item {
                    CircularProgressIndicator()
                }
            }else {
                val productList = allProduct ?: emptyList()
                items(productList) { data ->
                    ProductCard(
                        product = data,
                        onDelete = {
                            productViewModel.deleteProduct(data.id) { success, msg ->
                                // Handle callback if needed
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: ProductModel, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = product.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Price: Rs. ${product.price}",
                fontSize = 16.sp,
                color = Color.DarkGray
            )
            Text(
                text = product.description,
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            
            Row(modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = { /* Edit logic */ }) {
                    Text("Edit", color = Color(0xFF6200EE))
                }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = onDelete) {
                    Text("Delete", color = Color.Red)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreviewContent() {
    HomeScreen()
}
