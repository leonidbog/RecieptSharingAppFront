package com.example.recieptsharingapp.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext // Импортируйте это для доступа к context
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.recieptsharingapp.R
import com.example.recieptsharingapp.model.dto.Group
import com.example.recieptsharingapp.model.dto.User
import com.example.recieptsharingapp.retrofit.RetrofitAuthClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun GroupsScreen(
    navHostController: NavHostController
) {
    var groupsList by remember { mutableStateOf<List<Group>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Получаем context
    val context = LocalContext.current

    // Загрузка друзей при первом запуске компонуемой функции
    LaunchedEffect(Unit) {
        val sharedPreferences = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "") ?: ""
        val password = sharedPreferences.getString("password", "") ?: ""

        if (username.isEmpty() || password.isEmpty()) {
            errorMessage = "Необходимо войти в систему"
            isLoading = false
            return@LaunchedEffect
        }

        // Создаем аутентифицированный Retrofit клиент
        val apiService = RetrofitAuthClient.create(username, password)

        // Выполняем сетевой запрос
        apiService.getMyGroups().enqueue(object : Callback<List<Group>> {
            override fun onResponse(
                call: Call<List<Group>>,
                response: Response<List<Group>>
            ) {
                if (response.isSuccessful) {
                    groupsList = response.body() ?: emptyList()
                } else {
                    errorMessage = "Ошибка: ${response.code()}"
                }
                isLoading = false
            }

            override fun onFailure(call: Call<List<Group>>, t: Throwable) {
                errorMessage = "Сбой: ${t.message}"
                isLoading = false
            }
        })
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
            modifier = Modifier.fillMaxSize()
        ) {
            // Кнопка "Добавить группу" сверху
            Button(
                onClick = { navHostController.navigate("add_group_screen") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = stringResource(id = R.string.create_group_button))
            }

            // Отображение индикатора загрузки или ошибки
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = errorMessage ?: "Неизвестная ошибка")
                }
            } else {
                // Список друзей ниже
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(groupsList) { group ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clickable {
                                    navHostController.navigate("expenses_by_group/${group.id}/${group.name}")
                                },
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Text(
                                text = group.name,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}