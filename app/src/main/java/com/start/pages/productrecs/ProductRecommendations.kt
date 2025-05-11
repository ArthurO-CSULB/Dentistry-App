package com.start.pages.productrecs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

// This shows the page that the user sees
@Composable
fun ProductShopPage(products: List<Product>, navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Every product category
        Text(
            "Browse Products",
            style = MaterialTheme.typography.titleMedium,
            fontSize = 30.sp)
        Spacer(modifier = Modifier.height(8.dp))

        ProductCarousel(products, navController)
    }
}

// The function for the carousel itself
@Composable
fun ProductCarousel(products: List<Product>, navController: NavController) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy((-100).dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(products) { product ->
            CarouselCard(product) {
                when (product.name) {
                    "Toothbrushes" -> navController.navigate("toothbrushes")
                    "Toothpastes" -> navController.navigate("toothpastes")
                    "Floss" -> navController.navigate("floss")
                    "Mouthwash" -> navController.navigate("mouthwash")
                }
            }
        }
    }
}

// The card that will be shown in the carousel
@Composable
fun CarouselCard(product: Product, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .size(250.dp)
            .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(product.iconRes),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .aspectRatio(1f),
                    contentScale = ContentScale.Fit
                )

                // Text below the image
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        product.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// ALl the product categories
@Composable
fun ProductRecommendations(modifier: Modifier = Modifier, navController: NavController) {
    val productInfo = listOf(
        Product("Toothbrushes", iconRes = R.drawable.rec_toothbrush, true),
        Product("Toothpastes", R.drawable.rec_toothpaste, true),
        Product("Floss", R.drawable.rec_floss, true),
        Product("Mouthwash", R.drawable.rec_mouthwash)
    )

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            ProductShopPage(products = productInfo, navController = navController)
        }

        // Button to go back home.
        TextButton(onClick = {
            navController.navigate("home")
        }) {
            Text(text = "Home", fontSize = 32.sp)
        }
    }
}

// Basic class of each product
data class Product(
    val name: String,
    val iconRes: Int,
    val isTopProduct: Boolean = false
)
