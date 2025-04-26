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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dentalhygiene.R

/*
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
*/

// This shows the page that the user sees
@Composable
fun ProductShopPage(products: List<Product>, navController: NavController) {
    // The top three products
    val topProducts = products.filter { it.isTopProduct }.take(3)

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text("Top 3 Products of the Week", style = MaterialTheme.typography.titleMedium)

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(topProducts) { product ->
                TopProductCard(product) {
                    when (product.name) {
                        "Toothbrushes" -> navController.navigate("toothbrushes")
                    }
                }
            }
        }

        // Every product category
        Text("Browse Products", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        ProductCarousel(products, navController)
    }
}

// Top products function
@Composable
fun TopProductCard(product: Product, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(120.dp)
            .height(160.dp),
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
        }
    }
}

// The function for the carousel itself
@Composable
fun ProductCarousel(products: List<Product>, navController: NavController) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
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
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                    Text(
                        product.description,
                        fontSize = 10.sp,
                        color = Color.Gray,
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
        Product("Toothbrushes", iconRes = R.drawable.rec_toothbrush, "test", true),
        Product("Toothpastes", R.drawable.rec_toothpaste, "test", true),
        Product("Floss", R.drawable.rec_floss, "test", true),
        Product("Mouthwash", R.drawable.rec_mouthwash, "test")
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
    val description: String,
    val isTopProduct: Boolean = false
)
