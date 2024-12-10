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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.recieptsharingapp.R
import com.example.recieptsharingapp.model.dto.Expense
import com.example.recieptsharingapp.retrofit.RetrofitAuthClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ExpensesByGroupScreen(
    groupId: Long,
    groupName: String,
    navHostController: NavHostController
) {
    val context = LocalContext.current

    var expensesList by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(groupId) {
        val sharedPreferences = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "") ?: ""
        val password = sharedPreferences.getString("password", "") ?: ""

        val apiService = RetrofitAuthClient.create(username, password)

        apiService.getExpensesByGroup(groupId).enqueue(object : Callback<List<Expense>> {
            override fun onResponse(call: Call<List<Expense>>, response: Response<List<Expense>>) {
                if (response.isSuccessful) {
                    expensesList = response.body() ?: emptyList()
                } else {
                    errorMessage = "Ошибка загрузки трат: ${response.code()}"
                }
                isLoading = false
            }

            override fun onFailure(call: Call<List<Expense>>, t: Throwable) {
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
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Text(
                text = "Траты по группе: $groupName",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "Неизвестная ошибка",
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                if (expensesList.isEmpty()) {
                    Text(text = "Нет трат в этой группе")
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(expensesList) { expense ->
                            ExpenseItem(expense = expense)
                        }
                    }
                }
            }
        }
    }
}

// Пример компонента для отображения отдельного расхода
@Composable
fun ExpenseItem(expense: Expense) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Описание: ${expense.description}")
            Text(text = "Сумма: ${expense.amount}")
            Text(text = "Плательщик: ${expense.payer.username}")

            // Если есть участники
            if (expense.participants.isNotEmpty()) {
                Text(text = "Участники:")
                expense.participants.forEach { participant ->
                    Text(text = "- ${participant.username}")
                }
            }
        }
    }
}