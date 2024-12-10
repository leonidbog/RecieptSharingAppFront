package com.example.recieptsharingapp.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.recieptsharingapp.R
import com.example.recieptsharingapp.retrofit.RetrofitAuthClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal

@Composable
fun MyDebtsScreen(navHostController: NavHostController) {
    val context = LocalContext.current

    var debts by remember { mutableStateOf<Map<Long, BigDecimal>>(emptyMap()) }
    var userNameMap by remember { mutableStateOf<Map<Long, String>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val sharedPreferences = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "") ?: ""
        val password = sharedPreferences.getString("password", "") ?: ""

        val apiService = RetrofitAuthClient.create(username, password)

        // Сначала загрузим список друзей, чтобы была карта id -> username
        apiService.getFriends().enqueue(object : Callback<List<com.example.recieptsharingapp.model.dto.User>> {
            override fun onResponse(
                call: Call<List<com.example.recieptsharingapp.model.dto.User>>,
                response: Response<List<com.example.recieptsharingapp.model.dto.User>>
            ) {
                if (response.isSuccessful) {
                    val friends = response.body() ?: emptyList()
                    userNameMap = friends.associate { it.id to it.username }

                    // Теперь загрузим долги
                    apiService.getMyDebts().enqueue(object : Callback<Map<Long, BigDecimal>> {
                        override fun onResponse(
                            call: Call<Map<Long, BigDecimal>>,
                            response: Response<Map<Long, BigDecimal>>
                        ) {
                            if (response.isSuccessful) {
                                debts = response.body() ?: emptyMap()
                            } else {
                                errorMessage = "Ошибка при загрузке долгов: ${response.code()}"
                            }
                            isLoading = false
                        }

                        override fun onFailure(call: Call<Map<Long, BigDecimal>>, t: Throwable) {
                            errorMessage = "Сбой при загрузке долгов: ${t.message}"
                            isLoading = false
                        }
                    })

                } else {
                    errorMessage = "Ошибка при загрузке друзей: ${response.code()}"
                    isLoading = false
                }
            }

            override fun onFailure(call: Call<List<com.example.recieptsharingapp.model.dto.User>>, t: Throwable) {
                errorMessage = "Сбой при загрузке друзей: ${t.message}"
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
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Text(text = stringResource(R.string.hint_my_debts), style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "Неизвестная ошибка",
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                if (debts.isEmpty()) {
                    Text(text = stringResource(R.string.text_no_debts))
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(debts.toList()) { (userId, amount) ->
                            val userName =
                                userNameMap[userId] ?: "Неизвестный пользователь (ID: $userId)"
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(text = stringResource(R.string.text_user_debts_username, userName))
                                    Text(text = stringResource(R.string.text_user_debts_debt_amount, amount))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}