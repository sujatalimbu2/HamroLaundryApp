package com.example.hamrolaundryapp.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.hamrolaundryapp.ui.theme.HamrolaundryAppTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hamrolaundryapp.model.UserModel
import com.example.hamrolaundryapp.utils.Resource
import com.example.hamrolaundryapp.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onLoginClick: () -> Unit,
    onAdminPortalClick: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val userResource by viewModel.userProfile.collectAsState()
    val updateResult by viewModel.updateResult.collectAsState()

    ProfileScreenContent(
        userResource = userResource,
        updateResult = updateResult,
        onLogout = {
            viewModel.logout()
            onLogout()
        },
        onLoginClick = onLoginClick,
        onAdminPortalClick = onAdminPortalClick,
        onUpdateProfile = { name, address, contact ->
            viewModel.updateProfile(name, address, contact)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenContent(
    userResource: Resource<UserModel>,
    updateResult: Resource<Unit>?,
    onLogout: () -> Unit,
    onLoginClick: () -> Unit,
    onAdminPortalClick: () -> Unit,
    onUpdateProfile: (String, String, String) -> Unit
) {
    val context = LocalContext.current
    val isAdmin = (userResource as? Resource.Success)?.data?.role == "admin"

    var isEditMode by remember { mutableStateOf(false) }
    
    // Form States
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }

    // Sync form states when data is loaded
    LaunchedEffect(userResource) {
        if (userResource is Resource.Success) {
            val user = userResource.data!!
            name = user.name
            address = user.address
            contact = user.contact
        }
    }

    // Handle update result
    LaunchedEffect(updateResult) {
        if (updateResult is Resource.Success) {
            isEditMode = false
            Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
        } else if (updateResult is Resource.Error) {
            Toast.makeText(context, updateResult.message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", fontWeight = FontWeight.Bold) },
                actions = {
                    if (userResource is Resource.Success && !isEditMode) {
                        IconButton(onClick = { isEditMode = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                        }
                    } else if (isEditMode) {
                        IconButton(onClick = { isEditMode = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel")
                        }
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
            when (userResource) {
                is Resource.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is Resource.Success, is Resource.Error -> {
                    val user = (userResource as? Resource.Success)?.data
                    
                    if (user == null) {
                        // Guest State
                        Column(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(100.dp), tint = Color.LightGray)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("You are not logged in", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text("Login to book services and track orders", color = Color.Gray, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(32.dp))
                            Button(
                                onClick = onLoginClick,
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Login / Sign Up", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // Logged In User State
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Profile Image / Avatar
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(80.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            if (isEditMode) {
                                // Edit Form
                                ProfileInputField(value = name, onValueChange = { name = it }, label = "Full Name", icon = Icons.Default.Person)
                                Spacer(modifier = Modifier.height(16.dp))
                                ProfileInputField(value = contact, onValueChange = { contact = it }, label = "Phone Number", icon = Icons.Default.Phone)
                                Spacer(modifier = Modifier.height(16.dp))
                                ProfileInputField(value = address, onValueChange = { address = it }, label = "Address", icon = Icons.Default.LocationOn)
                                
                                Spacer(modifier = Modifier.height(32.dp))
                                
                                Button(
                                    onClick = { onUpdateProfile(name, address, contact) },
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = updateResult !is Resource.Loading
                                ) {
                                    if (updateResult is Resource.Loading) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                    } else {
                                        Text("Save Changes", fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else {
                                // Display View
                                Text(user.name.ifEmpty { "User" }, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                Text(user.email, fontSize = 16.sp, color = Color.Gray)

                                Spacer(modifier = Modifier.height(32.dp))

                                ProfileInfoCard(label = "Phone", value = user.contact.ifEmpty { "Not set" }, icon = Icons.Default.Phone)
                                Spacer(modifier = Modifier.height(12.dp))
                                ProfileInfoCard(label = "Address", value = user.address.ifEmpty { "Not set" }, icon = Icons.Default.LocationOn)
                                
                                Spacer(modifier = Modifier.height(24.dp))

                                // Admin Portal Access (Only for Admin Account)
                                if (isAdmin) {
                                    OutlinedButton(
                                        onClick = onAdminPortalClick,
                                        modifier = Modifier.fillMaxWidth().height(56.dp),
                                        shape = RoundedCornerShape(12.dp),
                                    ) {
                                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Admin Portal", fontWeight = FontWeight.Bold)
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                }

                                OutlinedButton(
                                    onClick = onLogout,
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Logout", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    HamrolaundryAppTheme {
        ProfileScreenContent(
            userResource = Resource.Success(
                UserModel(
                    id = "1",
                    name = "John Doe",
                    email = "john@example.com",
                    address = "123 Street",
                    contact = "1234567890",
                    role = "user"
                )
            ),
            updateResult = null,
            onLogout = {},
            onLoginClick = {},
            onAdminPortalClick = {},
            onUpdateProfile = { _, _, _ -> }
        )
    }
}

@Composable
fun ProfileInfoCard(label: String, value: String, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = label, fontSize = 12.sp, color = Color.Gray)
                Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun ProfileInputField(value: String, onValueChange: (String) -> Unit, label: String, icon: ImageVector) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        leadingIcon = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        singleLine = true
    )
}
