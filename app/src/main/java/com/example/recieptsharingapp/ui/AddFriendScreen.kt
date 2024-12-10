package com.example.recieptsharingapp.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@Composable
fun AddFriendScreen(
    navHostController: NavHostController
) {
    var friendId by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

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
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.add_friend_button),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = friendId,
                onValueChange = { friendId = it },
                label = { Text(stringResource(R.string.hint_add_friend_friendsID)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            val errorEnterFriendsID = stringResource(R.string.error_add_friend_enter_friendsID)
            Button(
                onClick = {
                    if (friendId.isNotBlank()) {
                        isLoading = true
                        errorMessage = null

                        // Получаем сохраненные имя пользователя и пароль
                        val sharedPreferences =
                            context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
                        val username = sharedPreferences.getString("username", "") ?: ""
                        val password = sharedPreferences.getString("password", "") ?: ""

                        val apiService = RetrofitAuthClient.create(username, password)

                        coroutineScope.launch {
                            try {
                                val response = apiService.addFriend(friendId.toLong())
                                if (response.isSuccessful) {
                                    navHostController.popBackStack()
                                } else {
                                    errorMessage = "Ошибка: ${response.code()}"
                                }
                            } catch (e: Exception) {
                                errorMessage = "Сбой: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    } else {
                        errorMessage = errorEnterFriendsID
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.add_friend_button))
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}