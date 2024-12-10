package com.example.recieptsharingapp.retrofit


import com.example.recieptsharingapp.model.dto.CreateExpenseDTO
import com.example.recieptsharingapp.model.dto.CreateGroupDTO
import com.example.recieptsharingapp.model.dto.Expense
import com.example.recieptsharingapp.model.dto.Group
import com.example.recieptsharingapp.model.dto.LoginRequest
import com.example.recieptsharingapp.model.dto.LoginResponse
import com.example.recieptsharingapp.model.dto.RegisterRequest
import com.example.recieptsharingapp.model.dto.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.math.BigDecimal

interface ApiAuthService {
    @GET("api/users/profile")
    fun profileId(): Call<Long?>


    @GET("/api/users/friends")
    fun getFriends(): Call<List<User>>


    @POST("/api/users/friends/add/{friendId}")
    suspend fun addFriend(@Path("friendId") friendId: Long): Response<Void>

    @GET("/api/groups/my_groups")
    fun getMyGroups(): Call<List<Group>>

    @POST("/api/groups/create")
    suspend fun createGroup(@Body createGroupDTO: CreateGroupDTO): Response<Group>
    @POST("/api/expenses/create")
    suspend fun createExpense(@Body dto: CreateExpenseDTO): Response<Void>

    @GET("/api/debts/me")
    fun getMyDebts(): Call<Map<Long, BigDecimal>>

    @GET("/api/expenses/group/{groupId}")
    fun getExpensesByGroup(@Path("groupId") groupId: Long): Call<List<Expense>>

}

