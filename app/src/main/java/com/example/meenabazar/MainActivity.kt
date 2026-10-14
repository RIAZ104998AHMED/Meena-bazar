package com.example.meenabazar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.meenabazar.ui.theme.МинабазарTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContent {

            МинабазарTheme {

                var screen by remember {
                    mutableStateOf("authorization")
                }

                when (screen) {

                    "authorization" -> LoginScreen(
                        onSignUpClick = {
                            screen = "signup"
                        },
                        onLoginClick = {
                            screen = "login"
                        }
                    )

                    "login" -> LoginFormScreen(
                        onBackClick = {
                            screen = "authorization"
                        },
                        onLoginClick = {
                            // Backend login is not implemented yet
                        },
                        onSignUpClick = {
                            screen = "signup"
                        }
                    )

                    "signup" -> SignUpScreenSimple(
                        onBackClick = {
                            screen = "authorization"
                        },
                        onSignUpClick = {
                            // Backend registration is not implemented yet
                        },
                        onLoginClick = {
                            screen = "login"
                        }
                    )
                }
            }
        }
    }
}