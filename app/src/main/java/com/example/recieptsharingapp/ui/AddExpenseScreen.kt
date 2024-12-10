package com.example.recieptsharingapp.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.ArrowDropDown
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
import com.example.recieptsharingapp.model.dto.CreateExpenseDTO
import com.example.recieptsharingapp.model.dto.Group
import com.example.recieptsharingapp.retrofit.RetrofitAuthClient
import kotlinx.coroutines.launch

@Composable
fun AddExpenseScreen(
    navHostController: NavHostController
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    var groups by remember { mutableStateOf<List<Group>>(emptyList()) }
    var selectedGroup by remember { mutableStateOf<Group?>(null) }

    // Список участников выбранной группы
    var selectedParticipantIds by remember { mutableStateOf<Set<Long>>(emptySet()) }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Для дропдауна выбора группы
    var groupDropdownExpanded by remember { mutableStateOf(false) }

    // Загрузка списка групп пользователя
    LaunchedEffect(Unit) {
        val sharedPreferences = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "") ?: ""
        val password = sharedPreferences.getString("password", "") ?: ""

        val apiService = RetrofitAuthClient.create(username, password)
        apiService.getMyGroups().enqueue(object : retrofit2.Callback<List<Group>> {
            override fun onResponse(
                call: retrofit2.Call<List<Group>>,
                response: retrofit2.Response<List<Group>>
            ) {
                if (response.isSuccessful) {
                    groups = response.body() ?: emptyList()
                } else {
                    errorMessage = "Ошибка при загрузке групп: ${response.code()}"
                }
                isLoading = false
            }

            override fun onFailure(call: retrofit2.Call<List<Group>>, t: Throwable) {
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
            Text(text = stringResource(R.string.add_expense_button), style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.hint_add_expense_expense_description)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text(stringResource(R.string.hint_add_expense_amount)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Выбор группы
            Text(stringResource(R.string.text_add_expense_select_group), style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = selectedGroup?.name ?: "",
                    onValueChange = {},
                    label = { Text(stringResource(R.string.hint_add_expense_group)) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { groupDropdownExpanded = !groupDropdownExpanded }) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.ArrowDropDown,
                                contentDescription = stringResource(R.string.create_button)
                            )
                        }
                    }
                )

                DropdownMenu(
                    expanded = groupDropdownExpanded,
                    onDismissRequest = { groupDropdownExpanded = false }
                ) {
                    groups.forEach { group ->
                        DropdownMenuItem(
                            text = { Text(group.name) },
                            onClick = {
                                selectedGroup = group
                                selectedParticipantIds = emptySet() // сбросить выбранных участников
                                groupDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Список участников выбранной группы (мультивыбор)
            selectedGroup?.let { group ->
                Text(
                    "Выберите участников, участвующих в расходе:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn {
                    items(group.members) { member ->
                        val isSelected = selectedParticipantIds.contains(member.id)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { checked ->
                                    selectedParticipantIds = if (checked) {
                                        selectedParticipantIds + member.id
                                    } else {
                                        selectedParticipantIds - member.id
                                    }
                                }
                            )
                            Text(text = member.username)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Кнопка "Создать"
            Button(
                onClick = {
                    if (description.isBlank() || amount.isBlank()) {
                        errorMessage = "Пожалуйста, введите описание и сумму"
                        return@Button
                    }

                    val amt = amount.toDoubleOrNull()
                    if (amt == null || amt <= 0.0) {
                        errorMessage = "Введите корректную сумму"
                        return@Button
                    }

                    val groupId = selectedGroup?.id
                    if (groupId == null) {
                        errorMessage = "Выберите группу"
                        return@Button
                    }

                    if (selectedParticipantIds.isEmpty()) {
                        errorMessage = "Выберите хотя бы одного участника"
                        return@Button
                    }

                    val sharedPreferences =
                        context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
                    val username = sharedPreferences.getString("username", "") ?: ""
                    val password = sharedPreferences.getString("password", "") ?: ""

                    val apiService = RetrofitAuthClient.create(username, password)

                    coroutineScope.launch {
                        try {
                            val dto = CreateExpenseDTO(
                                description = description,
                                amount = amt,
                                groupId = groupId,
                                participantIds = selectedParticipantIds
                            )

                            val response = apiService.createExpense(dto)
                            if (response.isSuccessful) {
                                // Возвращаемся назад
                                navHostController.popBackStack()
                            } else {
                                errorMessage =
                                    "Ошибка при создании расхода: ${response.errorBody()?.string()}"
                            }
                        } catch (e: Exception) {
                            errorMessage = "Сбой при создании расхода: ${e.message}"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.create_button))
            }

            // Ошибки
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
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