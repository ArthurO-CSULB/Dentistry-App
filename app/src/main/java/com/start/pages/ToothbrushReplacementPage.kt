package com.start.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.start.ui.theme.Purple80
import com.start.ui.theme.PurpleGrey40

const val FONT_SIZE = 18

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToothbrushReplacementPage(navController: NavController) {

    val selectedDate = System.currentTimeMillis()

    Scaffold(
        topBar = {CenterAlignedTopAppBar(
            title = {Text("Toothbrush Healthiness Tracker")},
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back to previous page"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Purple80,
                titleContentColor = PurpleGrey40
            ),
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        )}

    ) {innerpadding ->
        Column(
            modifier = Modifier.padding(innerpadding).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.padding(20.dp))

            Text(
                text = "You last replaced your toothbrush on: ",
                fontSize = FONT_SIZE.sp,
                textAlign = TextAlign.Center)

            Spacer(Modifier.padding(8.dp))

            Text(
                text = "INSERT DATE HERE",
                fontSize = 22.sp,
                textAlign = TextAlign.Center)

            Spacer(Modifier.padding(8.dp))

            Text(
                text = "When you should replace your toothbrush: ",
                fontSize = FONT_SIZE.sp,
                textAlign = TextAlign.Center)

            Spacer(Modifier.padding(8.dp))

            Text(
                text = "RIGHT NOW!!",
                fontSize = 22.sp,
                textAlign = TextAlign.Center)

            Spacer(Modifier.padding(12.dp))

            Text(text = "It is recommended that toothbrushes, both manual and mechanical," +
                    " should be replaced every three (3) months. This is to ensure that you are" +
                    " not brushing your teeth with a toothbrush infested by colonies of bacteria.",
                textAlign = TextAlign.Center,
                fontSize = FONT_SIZE.sp,
                modifier = Modifier.padding(10.dp))

            Spacer(Modifier.padding(18.dp))

            Button(
                onClick = {
                }
            )
            {
                Text(text = "I already replaced my toothbrush",
                    fontSize = FONT_SIZE.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TestToothbrush() {
    ToothbrushReplacementPage(rememberNavController())
}

