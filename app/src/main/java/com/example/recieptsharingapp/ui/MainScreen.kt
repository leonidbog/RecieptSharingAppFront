package com.example.recieptsharingapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.recieptsharingapp.R

@Composable
fun MainScreen(navController: NavHostController) {

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
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontStyle = FontStyle.Italic,
                    fontSize = 48.sp
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = { navController.navigate("profile") }) {
                Text(text = stringResource(id = R.string.profile_button))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("expenses") }) {
                Text(text = stringResource(id = R.string.my_expenses_button))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("friends") }) {
                Text(text = stringResource(id = R.string.friends_button))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("add_expense") }) {
                Text(text = stringResource(id = R.string.add_expense_button))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("my_groups") }) {
                Text(text = stringResource(id = R.string.my_groups_button))
            }
        }
    }
}