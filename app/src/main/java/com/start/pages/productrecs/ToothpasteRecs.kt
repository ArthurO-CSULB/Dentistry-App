package com.start.pages.productrecs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dentalhygiene.R

@Composable
fun ToothpasteRecs(modifier: Modifier, navController: NavController) {
    // Define products for each section
    val whiteningToothpaste = listOf(
        ToothpasteClass("Crest 3D White Advanced Luminous Mint Teeth Whitening Toothpaste", iconRes = R.drawable.rec_crest_whitening, price = "14.82"),
        ToothpasteClass("Colgate Optic White Advanced Hydrogen Peroxide Toothpaste", iconRes = R.drawable.rec_colgate_toothpaste, price = "$12.96"),
        ToothpasteClass("Boka Fluoride Free Toothpaste Nano Hydroxyapatite", iconRes = R.drawable.rec_boka_toothpaste, price = "$13.99")
    )

    val charcoalToothpaste = listOf(
        ToothpasteClass("hello Epic Whitening Charcoal Fluoride Free Toothpaste", iconRes = R.drawable.rec_hello_charcoal, price = "$5.97"),
        ToothpasteClass("Burt's Bees Charcoal Toothpaste", iconRes = R.drawable.rec_burtbeechar, price = "$14.91"),
        ToothpasteClass("Crest 3D White Advanced Charcoal Teeth Whitening Toothpaste", iconRes = R.drawable.rec_crest_3d_charcoal, price = "$13.46")
    )

    val sensitiveToothpaste = listOf(
        ToothpasteClass("Sensodyne Repair and Protect Whitening Toothpaste", iconRes = R.drawable.rec_sensodyne, price = "$12.48"),
        ToothpasteClass("Colgate Sensitive Maximum Strength Sensitive Toothpaste", iconRes = R.drawable.rec_colgate_sensitive, price = "$4.96"),
        ToothpasteClass("Crest Pro-Health Advanced Sensitive & Enamel Shield Toothpaste", iconRes = R.drawable.rec_crest_sensitive, price = "$4.97")
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Whitening Toothpaste Section
        Text("Whitening Toothpaste", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        ProductCarousel(products = whiteningToothpaste, navController = navController)

        // Charcoal Toothpaste Section
        Spacer(modifier = Modifier.height(32.dp))
        Text("Charcoal Toothpaste", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        ProductCarousel(products = charcoalToothpaste, navController = navController)

        // Sensitive Toothpaste Section
        Spacer(modifier = Modifier.height(32.dp))
        Text("Sensitive Toothpaste", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        ProductCarousel(products = sensitiveToothpaste, navController = navController)

        // Navigate to Home Button
        Spacer(modifier = Modifier.height(32.dp))
        TextButton(onClick = {
            navController.navigate("home")
        }) {
            Text(text = "Home", fontSize = 32.sp)
        }
    }
}

// ProductCarousel composable to show products in a horizontal scrollable list
@Composable
fun ProductCarousel(products: List<ToothpasteClass>, navController: NavController) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // just to loop
        val loopProducts = products + products
        items(loopProducts) { product ->
            ProductCard(product) {
            }
        }
    }
}

// ProductCard composable for displaying individual product details
@Composable
fun ProductCard(product: ToothpasteClass, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(120.dp)
            .height(180.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = product.iconRes),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(product.name, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(product.price, fontSize = 12.sp, fontWeight = FontWeight.Normal)
        }
    }
}

// Product data class with name, icon, and price properties
data class ToothpasteClass(
    val name: String,
    val iconRes: Int,
    val price: String
)
