package com.example.recieptsharingapp.ui


import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.recieptsharingapp.R
import com.example.recieptsharingapp.retrofit.RetrofitAuthClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ProfileScreen(navController: NavHostController) {
    var userId by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Загрузка ID пользователя при первом запуске экрана
    LaunchedEffect(Unit) {
        loadUserId(context) { id, usernameText ->
            userId = id
            username = usernameText
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.background))
        )
        Image(
            painter = painterResource(id = R.drawable.earth),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.profile_button),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontStyle = FontStyle.Italic,
                    fontSize = 48.sp
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                value = userId,
                onValueChange = { /* ID не изменяется пользователем */ },
                label = { Text("ID") },
                readOnly = true, // Делаем поле только для чтения
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = username,
                onValueChange = { },
                label = { Text(stringResource(R.string.hint_username)) },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            // ... другие элементы на экране профиля (например, кнопка "Сохранить")
        }
    }
}

// Функция для загрузки ID пользователя (замените на ваш API запрос)
private fun loadUserId(context: Context, onResult: (String, String) -> Unit) {
    val sharedPreferences = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
    val username = sharedPreferences.getString("username", "") ?: return // Получаем username из SharedPreferences
    val password = sharedPreferences.getString("password", "") ?: return



    val call = RetrofitAuthClient.create(username, password).profileId()
    call.enqueue(object : Callback<Long?> { // Изменен тип Callback на Long
        override fun onResponse(call: Call<Long?>, response: Response<Long?>) {
            if (response.isSuccessful) {
                val userId = response.body() ?: 0L
                onResult(userId.toString(), username)
            } else {
                // Обработка ошибки API
                Log.e("ProfileScreen", "Ошибка при получении ID: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<Long?>, t: Throwable) {
            // Обработка ошибки сети
            Log.e("ProfileScreen", "Ошибка сети: ${t.message}")
        }

    })

}
