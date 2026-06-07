package com.example.hamrolaundryapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hamrolaundryapp.model.ProductModel
import com.example.hamrolaundryapp.repo.ProductRepoImpl
import com.example.hamrolaundryapp.ui.theme.DarkBlue
import com.example.hamrolaundryapp.ui.theme.HamrolaundryAppTheme
import com.example.hamrolaundryapp.viewmodel.ProductViewModel

class Dashboard : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HamrolaundryAppTheme {
                val productViewModel = remember { ProductViewModel(ProductRepoImpl()) }
                DashboardScreen(productViewModel)
            }
        }
    }
}

@Composable
fun DashboardScreen(viewModel: ProductViewModel) {
    val context = LocalContext.current
    val products by viewModel.allProducts.collectAsState()
    val isLoading by viewModel.loading.collectAsState()

    // Fetch products when screen opens
    LaunchedEffect(Unit) {
        viewModel.getAllProduct()
    }

    DashboardContent(
        products = products,
        isLoading = isLoading,
        onAddProductClick = {
            context.startActivity(Intent(context, AddProduct::class.java))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    products: List<ProductModel>?,
    isLoading: Boolean,
    onAddProductClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Hamro Laundry", fontWeight = FontWeight.ExtraBold) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBlue,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProductClick,
                containerColor = DarkBlue,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF5F5F5))
        ) {
            if (isLoading && (products == null || products.isEmpty())) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = DarkBlue
                )
            } else if (products.isNullOrEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.LocalLaundryService,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No services available", color = Color.Gray, fontSize = 18.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(products) { product ->
                        ProductItem(product)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductItem(product: ProductModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Placeholder
            Surface(
                modifier = Modifier.size(50.dp),
                shape = RoundedCornerShape(12.dp),
                color = DarkBlue.copy(alpha = 0.1f)
            ) {
                Icon(
                    Icons.Default.LocalLaundryService,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = DarkBlue
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = product.description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }

            Text(
                text = "Rs. ${product.price}",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DarkBlue
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    HamrolaundryAppTheme {
        // Use mock data for the preview to avoid Firebase initialization issues
        DashboardContent(
            products = listOf(
                ProductModel("1", "Washing", "Standard washing service", 100.0),
                ProductModel("2", "Dry Cleaning", "Premium care for suits", 500.0),
                ProductModel("3", "Ironing", "Professional ironing", 50.0)
            ),
            isLoading = false,
            onAddProductClick = {}
        )
    }
}
