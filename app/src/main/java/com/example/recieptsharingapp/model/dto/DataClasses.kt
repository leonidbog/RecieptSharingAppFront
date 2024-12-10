package com.example.recieptsharingapp.model.dto

data class User(
    val id: Long,
    val username: String
    // Добавьте другие поля, если необходимо
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String, // Or whatever your API returns on successful login
    // ... other response data
)

data class RegisterRequest(
    val username: String,
    val password: String,
    // ... other registration fields
)


data class Group(
    val id: Long,
    val name: String,
    val creator: User,
    val members: List<User>
)

data class CreateGroupDTO(
    val name: String,
    val friendIds: Set<Long>
)

data class CreateExpenseDTO(
    val description: String,
    val amount: Double,
    val groupId: Long,
    val participantIds: Set<Long>
)


data class Expense(
    val description: String,
    val amount: Double,
    val payer: User,
    val participants: List<User>
)