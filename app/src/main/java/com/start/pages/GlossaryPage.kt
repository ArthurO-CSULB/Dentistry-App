package com.start.pages


import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.json.JSONArray


// Data class for glossary terms
// Stores a term and its corresponding definition
data class VocabularyTerm(val term: String, val definition: String)


// Glossary Page Composable
// Displays a search bar and a list of vocabulary terms
@Composable
fun GlossaryPage(modifier: Modifier = Modifier, navController: NavController) {
    val context = LocalContext.current
    val vocabularyTerms = remember { loadVocabularyFromAssets(context) }
    var searchQuery by remember { mutableStateOf("") }


    Column(
        modifier = modifier.fillMaxSize().padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Glossary Page", fontSize = 32.sp, modifier = Modifier.align(Alignment.CenterHorizontally))


        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search for a term") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { /* Handle search action */ }
            )
        )


        Spacer(modifier = Modifier.height(16.dp))


        // Filtered Vocabulary List
        val filteredTerms = vocabularyTerms.filter {
            it.term.startsWith(searchQuery, ignoreCase = true)
        }


        LazyColumn(
            modifier = Modifier.weight(1f) // This makes it scrollable within the available space
        ) {
            items(filteredTerms) { term ->
                VocabularyItem(term = term)
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }


        Spacer(modifier = Modifier.height(16.dp))


        // Button to go back home, placed inside the scrollable column
        TextButton(
            onClick = { navController.navigate("home") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Home")
        }
    }
}


// Displays an individual vocabulary term and its definition
@Composable
fun VocabularyItem(term: VocabularyTerm) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = term.term,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = term.definition,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


// Loads vocabulary terms from a JSON file in the assets folder
fun loadVocabularyFromAssets(context: Context): List<VocabularyTerm> {
    val terms = mutableListOf<VocabularyTerm>()
    try {
        // Open the JSON file from the assets folder
        val inputStream = context.assets.open("dentalGlossary.json")
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val jsonString = String(buffer, Charsets.UTF_8)


        // Parse the JSON string into a JSONArray
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val term = jsonObject.getString("term")
            val definition = jsonObject.getString("definition")
            // Add each term and definition as a VocabularyTerm object
            terms.add(VocabularyTerm(term, definition))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return terms
}



