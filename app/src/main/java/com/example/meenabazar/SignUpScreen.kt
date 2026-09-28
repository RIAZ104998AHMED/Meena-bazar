package com.example.meenabazar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SignUpScreenSimple(
    onBackClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
    onLoginClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // Background
        Image(
            painter = painterResource(id = R.drawable.signuplogo),
            contentDescription = "Sign up background",
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

            Spacer(modifier = Modifier.height(80.dp))

            // Screen title
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

            // Registration form container
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
                // Input fields will be added in the next session.
            }
        }
    }
}