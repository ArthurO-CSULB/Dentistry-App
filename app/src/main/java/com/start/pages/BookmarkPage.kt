package com.start.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.start.viewmodels.BookmarksViewModel

@Composable
fun BookmarkPage(modifier: Modifier = Modifier, navController: NavController, viewModel: BookmarksViewModel = viewModel()) {
    val bookmarks by viewModel.bookmarks.observeAsState(emptyList())

    // Launch effect to load bookmarks
    LaunchedEffect(Unit) {
        viewModel.loadBookmarks()
    }

    // Bookmark Page UI
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Saved Dental Clinics", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        // LazyVerticalGrid for displaying bookmarks
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // 2 columns for the bookmarks
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(bookmarks) { bookmark ->
                BookmarkCard(bookmark, navController, viewModel)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    // Column for home
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        // Button to go back home.
        TextButton(onClick = {
            navController.navigate("home")
        }) {
            Text(text = "Home", fontSize = 32.sp)
        }
    }
}

@Composable
fun BookmarkCard(bookmark: BookmarkInformation, navController: NavController, viewModel: BookmarksViewModel = viewModel()) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = bookmark.clinicName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                // "X" button to delete bookmark
                TextButton(
                    onClick = {
                        viewModel.deleteBookmark(bookmark.clinicID)
                    }
                ) {
                    Text("X", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }

            Text(text = bookmark.clinicName, style = MaterialTheme.typography.titleMedium)
            Text(text = "Rating: ${bookmark.ratingScore}", style = MaterialTheme.typography.bodySmall)
            if (bookmark.review.isNotEmpty()) {
                Text(text = "\"${bookmark.review}\"", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
