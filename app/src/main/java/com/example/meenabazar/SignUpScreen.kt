package com.example.meenabazar

import android.util.Patterns
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SignUpScreenSimple(
    onBackClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
    onLoginClick: () -> Unit
) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Validation error states
    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }

    fun validateInputs(): Boolean {

        nameError = ""
        emailError = ""
        passwordError = ""
        confirmPasswordError = ""

        var isValid = true

        // Full name validation
        if (name.isBlank()) {
            nameError = "Full name is required"
            isValid = false
        }

        // Email validation
        if (email.isBlank()) {
            emailError = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            emailError = "Enter a valid email address"
            isValid = false
        }

        // Password validation
        if (password.isBlank()) {
            passwordError = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            passwordError = "Password must contain at least 6 characters"
            isValid = false
        }

        // Confirm password validation
        if (confirmPassword.isBlank()) {
            confirmPasswordError = "Confirm password is required"
            isValid = false
        } else if (password != confirmPassword) {
            confirmPasswordError = "Passwords do not match"
            isValid = false
        }

        return isValid
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // Background image
        Image(
            painter = painterResource(id = R.drawable.signuplogo),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x66000000),
                            Color.Transparent,
                            Color(0xAA000000)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Back button
            OutlinedButton(
                onClick = onBackClick,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(
                    1.dp,
                    Color.White.copy(alpha = 0.4f)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White.copy(alpha = 0.10f),
                    contentColor = Color.White
                ),
                modifier = Modifier.align(Alignment.Start)
            ) {
                Text(
                    text = "← Back",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "Create Account",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Join Meena Bazar and discover your style",
                color = Color(0xFFE0E0E0),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Registration form
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(16.dp)
            ) {

                // Full Name
                SimpleField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = ""
                    },
                    hint = "Full Name"
                )

                if (nameError.isNotEmpty()) {
                    ValidationError(nameError)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Email
                SimpleField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = ""
                    },
                    hint = "Email"
                )

                if (emailError.isNotEmpty()) {
                    ValidationError(emailError)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Password
                SimpleField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = ""
                    },
                    hint = "Password",
                    isPassword = true
                )

                if (passwordError.isNotEmpty()) {
                    ValidationError(passwordError)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Confirm Password
                SimpleField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        confirmPasswordError = ""
                    },
                    hint = "Confirm Password",
                    isPassword = true
                )

                if (confirmPasswordError.isNotEmpty()) {
                    ValidationError(confirmPasswordError)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Existing Sign Up button
                Button(
                    onClick = {
                        if (validateInputs()) {
                            onSignUpClick()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD2A15B),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Sign Up",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Existing Login link
                Row(
                    modifier = Modifier.align(
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Already have an account? ",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )

                    Text(
                        text = "Log in",
                        color = Color(0xFFD2A15B),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable {
                            onLoginClick()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}


@Composable
fun SimpleField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    isPassword: Boolean = false
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                color = Color.White.copy(alpha = 0.14f),
                shape = RoundedCornerShape(28.dp)
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.28f),
                shape = RoundedCornerShape(28.dp)
            ),
        contentAlignment = Alignment.CenterStart
    ) {

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            visualTransformation =
                if (isPassword) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 16.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            decorationBox = { innerTextField ->

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {

                    if (value.isEmpty()) {
                        Text(
                            text = hint,
                            color = Color(0xFFD6D6D6),
                            fontSize = 16.sp
                        )
                    }

                    innerTextField()
                }
            }
        )
    }
}


@Composable
fun ValidationError(
    message: String
) {
    Text(
        text = message,
        color = Color(0xFFFF6B6B),
        fontSize = 12.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                top = 4.dp
            )
    )
}