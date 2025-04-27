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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dentalhygiene.R

@Composable
fun ToothbrushRecs(modifier: Modifier, navController: NavController) {
    // Electric toothbrushes
    val electricToothbrushes = listOf(
        ToothpasteClass("Aquasonic Black Series Ultra Whitening Toothbrush", iconRes = R.drawable.rec_aquasonic_electric, price = "$39.95"),
        ToothpasteClass("PHILIPS Sonicare 1100 Power Toothbrush", iconRes = R.drawable.rec_philips_electric, price = "$19.96"),
        ToothpasteClass("Oral-B Pro 1000", iconRes = R.drawable.rec_oral_b_pro_1000, price = "$49.94")
    )

    // Manual toothbrushes
    val manualToothbrushes = listOf(
        ToothpasteClass("Colgate Extra Clean Toothbrush", iconRes = R.drawable.rec_colgate_extra_clean, price = "$4.96"),
        ToothpasteClass("Oral-B Advantage Vivid Dual Action Whitening Toothbrushes", iconRes = R.drawable.rec_oral_b_3d_white, price = "$7.99"),
        ToothpasteClass("Oral-B Charcoal Toothbrushes", iconRes = R.drawable.rec_oral_b_charcoal, price = "$6.97")
    )

    // Mini toothbrushes
    val travelToothbrushes = listOf(
        ToothpasteClass("GUM Folding Travel Toothbrush", iconRes = R.drawable.rec_gum_travel, price = "$6.00"),
        ToothpasteClass("cleaings Mini Brushes", iconRes = R.drawable.rec_cleaings_mini, price = "$7.90"),
        ToothpasteClass("Lingito Travel Folding Toothbrush", iconRes = R.drawable.rec_lingito_travel, price = "$8.99")
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Electric Toothbrushes Section
        Text("Electric Toothbrushes", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))
        ProductCarousel(products = electricToothbrushes, navController = navController)

        // Manual Toothbrushes Section
        Spacer(modifier = Modifier.height(32.dp))
        Text("Manual Toothbrushes", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))
        ProductCarousel(products = manualToothbrushes, navController = navController)

        // Travel Toothbrushes Section
        Spacer(modifier = Modifier.height(32.dp))
        Text("Travel Toothbrushes", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))
        ProductCarousel(products = travelToothbrushes, navController = navController)

        // Navigate to Home Button
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = {
            navController.navigate("home")
        }) {
            Text(text = "Home", fontSize = 32.sp)
        }
    }
}

// ProductCarousel composable to show products in a horizontal scrollable list
@Composable
fun ProductCarousel(products: List<ToothbrushClass>, navController: NavController) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Loop products
        val loopProducts = products + products
        items(loopProducts) { product ->
            ProductCard(product) {
            }
        }
    }
}

// ProductCard composable for displaying individual product details
@Composable
fun ProductCard(product: ToothbrushClass, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(200.dp)
            .height(300.dp),
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
            Text(
                text = product.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 4.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(product.price, fontSize = 12.sp, fontWeight = FontWeight.Normal)
        }
    }
}

// Product data class with name, icon, and price properties
data class ToothbrushClass(
    val name: String,
    val iconRes: Int,
    val price: String
)
