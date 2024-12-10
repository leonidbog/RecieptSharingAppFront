// com.example.recieptsharingapp.ui.AddGroupScreen

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
import com.example.recieptsharingapp.model.dto.CreateGroupDTO
import com.example.recieptsharingapp.model.dto.User
import com.example.recieptsharingapp.retrofit.RetrofitAuthClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun AddGroupScreen(
    navHostController: NavHostController
) {
    var groupName by remember { mutableStateOf("") }
    var allFriends by remember { mutableStateOf<List<User>>(emptyList()) }
    var selectedFriendIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Загрузка списка друзей
    LaunchedEffect(Unit) {
        val sharedPreferences = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "") ?: ""
        val password = sharedPreferences.getString("password", "") ?: ""

        val apiService = RetrofitAuthClient.create(username, password)
        apiService.getFriends().enqueue(object : Callback<List<User>> {
            override fun onResponse(
                call: Call<List<User>>,
                response: Response<List<User>>
            ) {
                if (response.isSuccessful) {
                    allFriends = response.body() ?: emptyList()
                } else {
                    errorMessage = "Ошибка: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                errorMessage = "Сбой: ${t.message}"
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
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.create_button),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text(stringResource(R.string.hint_group_name)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = stringResource(R.string.text_add_add_group_add_friends_to_group), style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))

            // Реализация мультивыбора друзей
            LazyColumn {
                items(allFriends) { friend ->
                    val isSelected = selectedFriendIds.contains(friend.id)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { checked ->
                                selectedFriendIds = if (checked) {
                                    selectedFriendIds + friend.id
                                } else {
                                    selectedFriendIds - friend.id
                                }
                            }
                        )
                        Text(text = friend.username)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (groupName.isNotBlank()) {
                        val sharedPreferences =
                            context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
                        val username = sharedPreferences.getString("username", "") ?: ""
                        val password = sharedPreferences.getString("password", "") ?: ""

                        val apiService = RetrofitAuthClient.create(username, password)

                        coroutineScope.launch {
                            try {
                                val response = apiService.createGroup(
                                    CreateGroupDTO(
                                        name = groupName,
                                        friendIds = selectedFriendIds
                                    )
                                )
                                if (response.isSuccessful) {
                                    navHostController.popBackStack()
                                } else {
                                    errorMessage = "Ошибка: ${response.errorBody()?.string()}"
                                }
                            } catch (e: Exception) {
                                errorMessage = "Сбой: ${e.message}"
                            }
                        }
                    } else {
                        errorMessage = "Введите название группы"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.create_button))
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