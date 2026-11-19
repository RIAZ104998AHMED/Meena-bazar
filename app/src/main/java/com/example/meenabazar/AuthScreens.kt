package com.example.meenabazar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meenabazar.ui.theme.Forest

@Composable
fun LoginScreen(onSignUpClick: () -> Unit, onLoginClick: () -> Unit, onGuestClick: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Image(painterResource(R.drawable.authorize_logo), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Forest.copy(alpha = .25f), Forest.copy(alpha = .97f)))))
        Column(
            Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState()).padding(28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("MEENA BAZAR", color = Color.White, letterSpacing = 4.sp, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(100.dp))
            Text("A little more\nyou.", color = Color.White, fontFamily = FontFamily.Serif, fontSize = 56.sp, lineHeight = 60.sp)
            Text("Thoughtful styles. Everyday stories.\nDiscover clothing that feels like you.", color = Color.White.copy(alpha = .9f), style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(32.dp))
            Button(onClick = onSignUpClick, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0DFC2), contentColor = Forest)) {
                Text("Create account")
            }
            OutlinedButton(onClick = onLoginClick, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) { Text("Log in") }
            TextButton(onClick = onGuestClick, modifier = Modifier.align(Alignment.CenterHorizontally), colors = ButtonDefaults.textButtonColors(contentColor = Color.White)) { Text("Explore as guest →") }
            Text("A clothing store, made for your everyday.", color = Color.White.copy(alpha = .8f), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun AuthLayout(title: String, subtitle: String, onBack: () -> Unit, enabled: Boolean, content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(onClick = onBack, enabled = enabled) { Text("← Back") }
        Text("MEENA BAZAR", color = MaterialTheme.colorScheme.primary, letterSpacing = 3.sp, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))
        Text(title, style = MaterialTheme.typography.headlineLarge, fontFamily = FontFamily.Serif)
        Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
        content()
        Text("Accounts are saved on this device for this course demo.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun LoginFormScreen(ui: BazarUiState, onBack: () -> Unit, onLogin: (LoginData) -> Unit, onSignUp: () -> Unit) {
    var email by rememberSaveable { mutableStateOf("") }
    // Passwords deliberately stay out of saved-instance-state bundles.
    var password by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }
    val validation = if (submitted) AuthValidator.login(LoginData(email, password)) else LoginValidationState()
    val focus = LocalFocusManager.current
    val submit = {
        submitted = true
        if (AuthValidator.login(LoginData(email, password)).isValid && !ui.busy) {
            focus.clearFocus()
            onLogin(LoginData(email, password))
        }
    }
    AuthLayout("Welcome back.", "Your next favorite is waiting for you.", onBack, !ui.busy) {
        ui.notice?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        SimpleField(email, { email = it }, "Email", error = validation.emailError, keyboardType = KeyboardType.Email, enabled = !ui.busy)
        SimpleField(password, { password = it }, "Password", isPassword = true, error = validation.passwordError, enabled = !ui.busy, onDone = submit)
        ui.error?.let { ValidationError(it) }
        PrimaryAction(if (ui.busy) "Logging in…" else "Log in", !ui.busy, submit)
        TextButton(onClick = onSignUp, enabled = !ui.busy, modifier = Modifier.fillMaxWidth()) { Text("New here? Create account") }
    }
}

@Composable
fun SignUpScreen(ui: BazarUiState, onBack: () -> Unit, onRegister: (RegistrationData) -> Unit, onLogin: () -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }
    val data = RegistrationData(name, email, password, confirm)
    val validation = if (submitted) AuthValidator.registration(data) else RegistrationValidationState()
    val focus = LocalFocusManager.current
    val submit = {
        submitted = true
        if (AuthValidator.registration(data).isValid && !ui.busy) {
            focus.clearFocus()
            onRegister(data)
        }
    }
    AuthLayout("Find your everyday.", "Create an account and make yourself at home.", onBack, !ui.busy) {
        SimpleField(name, { name = it }, "Full name", error = validation.fullNameError, enabled = !ui.busy)
        SimpleField(email, { email = it }, "Email", error = validation.emailError, keyboardType = KeyboardType.Email, enabled = !ui.busy)
        SimpleField(password, { password = it }, "Password", isPassword = true, error = validation.passwordError, enabled = !ui.busy)
        if (validation.passwordError == null) Text("Use at least 8 characters.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        SimpleField(confirm, { confirm = it }, "Confirm password", isPassword = true, error = validation.confirmPasswordError, enabled = !ui.busy, onDone = submit)
        ui.error?.let { ValidationError(it) }
        PrimaryAction(if (ui.busy) "Creating account…" else "Sign up", !ui.busy, submit)
        TextButton(onClick = onLogin, enabled = !ui.busy, modifier = Modifier.fillMaxWidth()) { Text("Already have an account? Log in") }
    }
}

@Composable
fun SimpleField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    isPassword: Boolean = false,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true,
    onDone: (() -> Unit)? = null,
    singleLine: Boolean = true
) {
    var visible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(hint) },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 2,
        enabled = enabled,
        isError = error != null,
        supportingText = if (error != null) ({ ValidationError(error) }) else null,
        visualTransformation = if (isPassword && !visible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = if (isPassword) ({ TextButton(onClick = { visible = !visible }, enabled = enabled) { Text(if (visible) "Hide" else "Show") } }) else null,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else keyboardType, imeAction = if (onDone != null) ImeAction.Done else ImeAction.Next),
        keyboardActions = KeyboardActions(onDone = { onDone?.invoke() }),
        shape = MaterialTheme.shapes.medium
    )
}

@Composable
fun ValidationError(message: String) {
    Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
}

@Composable
fun PrimaryAction(label: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(onClick = onClick, enabled = enabled, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp), shape = MaterialTheme.shapes.medium) { Text(label, fontWeight = FontWeight.SemiBold) }
}
