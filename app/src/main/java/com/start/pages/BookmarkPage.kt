package com.start.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.unit.dp

@Composable
fun BookmarkPage(modifier: Modifier = Modifier, navController: NavController, viewModel: BookmarksViewModel = viewModel()) {
    val bookmarks by viewModel.bookmarks.observeAsState(emptyList())

    // Show all bookmarked dental clinics
    Column(modifier = Modifier.fillMaxSize().padding(40.dp)) {
        Text("Saved Dental Clinics", style = MaterialTheme.typography.headlineSmall)

        LazyColumn(modifier = Modifier.weight(1f).padding(top = 8.dp)) {
            items(bookmarks) { bookmark ->
                BookmarkCard(bookmark)
            }
        }

        // Button to go back home.
        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { navController.navigate("home") }
        ) {
            Text(text = "Home")
        }
    }

}

@Composable
fun BookmarkCard(bookmark: BookmarkInformation) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = bookmark.title, style = MaterialTheme.typography.titleMedium)
            Text(text = bookmark.address, style = MaterialTheme.typography.bodyMedium)
            Text(text = "Rating: ${bookmark.rating}", style = MaterialTheme.typography.bodySmall)
            Text(text = "\"${bookmark.review}\"", style = MaterialTheme.typography.bodySmall)
        }
    }
}