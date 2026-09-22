package com.example.meenabazar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meenabazar.ui.theme.CreamText
import com.example.meenabazar.ui.theme.DarkOverlayBottom
import com.example.meenabazar.ui.theme.DarkOverlayTop
import com.example.meenabazar.ui.theme.GlassBorder
import com.example.meenabazar.ui.theme.GlassWhite
import com.example.meenabazar.ui.theme.GuestButton
import com.example.meenabazar.ui.theme.SignUpButton
import com.example.meenabazar.ui.theme.SoftCream

@Composable
fun LoginScreen(
    onSignUpClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // Background
        Image(
            painter = painterResource(id = R.drawable.authorize_logo),
            contentDescription = "Authorization background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Dark gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DarkOverlayTop,
                            Color.Transparent,
                            DarkOverlayBottom
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
                    vertical = 18.dp
                )
        ) {

            // Top login button
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
                OutlinedButton(
                    onClick = {
                        // Login screen will be connected in RIAZ-5 / RIAZ-6
                    },
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = GlassBorder
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = GlassWhite,
                        contentColor = CreamText
                    )
                ) {
                    Text(
                        text = stringResource(R.string.log_in),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Application title and subtitle
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = stringResource(R.string.tittle),
                    color = CreamText,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Text(
                    text = stringResource(R.string.subtittle),
                    color = SoftCream,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(
                        top = 10.dp,
                        start = 24.dp,
                        end = 24.dp
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Authentication action panel
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp),
                color = GlassWhite,
                border = BorderStroke(
                    width = 1.dp,
                    color = GlassBorder
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // Sign Up
                    Button(
                        onClick = onSignUpClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SignUpButton,
                            contentColor = CreamText
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.sign_up),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Guest login
                    OutlinedButton(
                        onClick = {
                            // Guest behavior can be added later
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = GlassBorder
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = GuestButton,
                            contentColor = CreamText
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.login_as_guest),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}