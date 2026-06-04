package com.example.hamrolaundryapp

import android.content.Intent
import android.widget.Toast
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hamrolaundryapp.model.UserModel
import com.example.hamrolaundryapp.repo.UserRepoImpl
import com.example.hamrolaundryapp.ui.theme.Blue
import com.example.hamrolaundryapp.ui.theme.DarkBlue
import com.example.hamrolaundryapp.viewmodel.UserViewModel

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userViewModel = remember { UserViewModel(UserRepoImpl()) }
            RegistrationBody(
                userViewModel = userViewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationBody(
    userViewModel: UserViewModel
) {

//    var email : String = ""
//    vs
    var email by remember { mutableStateOf("") }

    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var visibility by remember { mutableStateOf(false) }

    val context = LocalContext.current



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 35.dp),
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            "Sign Up", style = TextStyle(
                color = DarkBlue,
                fontWeight = FontWeight.W800,
                fontSize = 35.sp,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
                .padding(40.dp)
        )

        Text(
            text="Fill your information below to explore our shop",
            style = TextStyle(
                fontSize = 15.sp,
                textAlign = TextAlign.Justify
            ),
        )
        Spacer(modifier = Modifier.height(60.dp))

        OutlinedTextField(
            value = name,
            onValueChange = {
                //ram
                name = it
            },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null)
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Your name")
            },
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                focusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                focusedIndicatorColor = Color.Transparent,
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = address,
            onValueChange = {
                //ram
                address = it
            },
            leadingIcon = {
                Icon(Icons.Default.LocationOn, contentDescription = null)
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Your address")
            },
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                focusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                focusedIndicatorColor = Color.Blue,
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                //ram
                email = it
            },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null)
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("abc@gmail.com")
            },
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                focusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                focusedIndicatorColor = Color.Transparent,
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                //ram
                password = it
            },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null)
            },
            visualTransformation = if (visibility)
                VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = {
                    visibility = !visibility
                }) {
                    Icon(
                        imageVector =
                            if (visibility)
                                Icons.Default.Visibility
                            else
                                Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("********")
            },
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                focusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                focusedIndicatorColor = Color.Blue,
            )
        )

        Spacer(modifier = Modifier.height(15.dp))


        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            ElevatedButton(
                onClick = {

                    userViewModel.register(email,password){
                        success,msg,userId->
                        if(success){
                            val model = UserModel(
                                id = userId,
                                name = name,
                                email = email,
                                address = address,
                                contact = ""
                            )
                            userViewModel.addUser(userId, model){
                                success,msg->
                                if(success){
                                    Toast.makeText(context, msg,
                                        Toast.LENGTH_LONG).show()
                                }else{
                                    Toast.makeText(context, msg,
                                        Toast.LENGTH_LONG).show()
                                }
                            }
                        }else{
                            Toast.makeText(context,msg,
                                Toast.LENGTH_LONG).show()
                        }
                    }
//                    val sharedPreferences = context.getSharedPreferences(
//                        "User",
//                        Context.MODE_PRIVATE
//                    )
//
//                    sharedPreferences.edit {
//                        putString("email", email)
//                        putString("password", password)
//                        putString("name", name)
//                        putString("address", address)
//                    }

//                    Toast.makeText(context, "Registration successful", Toast.LENGTH_LONG).show()
//                    val intent = Intent(context, Login::class.java)
//                    context.startActivity(intent)
//                    context.findActivity()?.finish()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = DarkBlue,
                    contentColor = Color.White
                )

            ) {
                Text("Sign up", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Already have an account? ", color = Color.Gray, fontSize = 14.sp)
            Text(
                "Login here",
                modifier = Modifier.clickable {
                    context.startActivity(Intent(context, Login::class.java))
                },
                style = TextStyle(color = Blue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            )
        }

    }
}


@Preview(showBackground = true)
@Composable
fun RegistrationPreview() {
    RegistrationBody(userViewModel = UserViewModel(UserRepoImpl()))
}
