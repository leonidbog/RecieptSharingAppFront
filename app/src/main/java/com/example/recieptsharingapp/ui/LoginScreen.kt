package com.example.recieptsharingapp.ui


import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.recieptsharingapp.R
import com.example.recieptsharingapp.model.dto.LoginRequest
import com.example.recieptsharingapp.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

@Composable
fun LoginScreen(navController: NavHostController,  context: Context, onLanguageSwitch: (String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var currentLanguage by remember { mutableStateOf(context.resources.configuration.locales[0].language) }

    val configuration = LocalConfiguration.current
    val contextState = rememberUpdatedState(context)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Фоновое изображение
        Image(
            painter = painterResource(id = R.drawable.earth),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(Color.Blue.copy(alpha = 0.5f))
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка переключения языка
            Button(
                onClick = {
                    currentLanguage = if (currentLanguage == "en") "ru" else "en"
                    switchLanguage(contextState.value, currentLanguage)
                    onLanguageSwitch(currentLanguage)
                }
            ) {
                Text(text = stringResource(R.string.language_button))
            }

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontStyle = FontStyle.Italic,
                    fontSize = 48.sp
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                value = username,
                onValueChange = { username = it },
                label
                = { Text(stringResource(R.string.hint_username)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.hint_password)) },
                visualTransformation = PasswordVisualTransformation(),

                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (showError) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(onClick = {
                if (username.isBlank() || password.isBlank()) {
                    showError = true
                    errorMessage = context.getString(R.string.error_fill_all_fields)
                    return@Button
                }

                val call = RetrofitClient.apiService.login(LoginRequest(username, password))
                call.enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (response.isSuccessful) {
                            // Сохраняем username и password (например, в SharedPreferences)
                            val sharedPreferences =
                                context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
                            with(sharedPreferences.edit()) {
                                putString("username", username)
                                putString(
                                    "password",
                                    password
                                ) // В реальном приложении НЕ храните пароль в открытом виде!
                                apply()
                            }
                            // Переходим на главный экран
                            navController.navigate("main")
                        } else {
                            showError = true
                            errorMessage =
                                response.toString() // Или более информативное сообщение об ошибке
                        }
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        showError = true
                        errorMessage = t.message.toString()
                    }
                })
            }) {
                Text(stringResource(R.string.login_button))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("registration") }) {
                Text(stringResource(R.string.registration_button))
            }

        }
    }
}

private fun switchLanguage(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val config = context.resources.configuration
    config.setLocale(locale)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)

}