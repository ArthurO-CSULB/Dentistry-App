package com.start.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.start.ui.theme.Purple80
import com.start.ui.theme.PurpleGrey40

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DentalRoutinePage(navController: NavController) {

    Scaffold(
        topBar = {CenterAlignedTopAppBar(
            title = {Text("Dental Routine Page")},
            navigationIcon = {
                // back button
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back to previous page"
                    )
                }
            },
            // Specifies Color of the top bar
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Purple80,
                titleContentColor = PurpleGrey40
            )
        )

        }
    )
    { innerPadding ->

        Column(
            modifier = Modifier.padding(innerPadding)
        ) {

        }
    }

}

@Preview(showBackground = true)
@Composable
fun TestDentalPage() {
    DentalRoutinePage(rememberNavController())
}