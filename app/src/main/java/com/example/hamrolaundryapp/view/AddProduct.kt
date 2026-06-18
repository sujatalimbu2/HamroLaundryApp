package com.example.hamrolaundryapp.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hamrolaundryapp.model.ProductModel
import com.example.hamrolaundryapp.repo.ProductRepo
import com.example.hamrolaundryapp.repo.ProductRepoImpl
import com.example.hamrolaundryapp.ui.theme.DarkBlue
import com.example.hamrolaundryapp.ui.theme.HamrolaundryAppTheme
import com.example.hamrolaundryapp.viewmodel.ProductViewModel

class AddProduct : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HamrolaundryAppTheme {
                val productViewModel = remember { ProductViewModel(ProductRepoImpl()) }
                AddProductScreen(productViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(viewModel: ProductViewModel) {
    var productName by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    val isLoading by viewModel.loading.observeAsState(initial = false)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Add New Product",
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    scrolledContainerColor = Color.Unspecified,
                    navigationIconContentColor = Color.Unspecified,
                    titleContentColor = Color.Unspecified,
                    actionIconContentColor = Color.Unspecified
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Product Name Field
            ProductInputField(
                value = productName,
                onValueChange = { productName = it },
                label = "Product Name",
                placeholder = "Enter product name",
                icon = Icons.Default.ShoppingBag
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Price Field
            ProductInputField(
                value = productPrice,
                onValueChange = { productPrice = it },
                label = "Price",
                placeholder = "0.00",
                icon = Icons.Default.AttachMoney,
                keyboardType = KeyboardType.Decimal
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description Field
            OutlinedTextField(
                value = productDescription,
                onValueChange = { productDescription = it },
                label = { Text("Description") },
                placeholder = { Text("Enter product details...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = {
                    Icon(Icons.Default.Description, contentDescription = null, tint = DarkBlue)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DarkBlue,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Submit Button
            Button(
                onClick = {
                    if (productName.isNotEmpty() && productDescription.isNotEmpty() && productPrice.isNotEmpty()) {
                        val price = productPrice.toDoubleOrNull() ?: 0.0
                        val product = ProductModel(
                            name = productName,
                            description = productDescription,
                            price = price
                        )
                        viewModel.addProduct(product) { success, message ->
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            if (success) {
                                productName = ""
                                productDescription = ""
                                productPrice = ""
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        "Add Product",
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun ProductInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        leadingIcon = {
            Icon(icon, contentDescription = null, tint = DarkBlue)
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = DarkBlue,
            unfocusedBorderColor = Color.LightGray
        )
    )
}

@Preview(showBackground = true)
@Composable
fun AddProductPreview() {
    val fakeRepo = object : ProductRepo {
        override fun addProduct(product: ProductModel, callback: (Boolean, String) -> Unit) {}
        override fun deleteProduct(id: String, callback: (Boolean, String) -> Unit) {}
        override fun editProduct(
            id: String,
            product: ProductModel,
            callback: (Boolean, String) -> Unit
        ) {
        }

        override fun getAllProduct(callback: (Boolean, String, List<ProductModel>?) -> Unit) {}
        override fun getProductById(id: String, callback: (Boolean, String, ProductModel?) -> Unit) {}
    }
    HamrolaundryAppTheme {
        AddProductScreen(viewModel = ProductViewModel(fakeRepo))
    }
}
