package com.start.pages.productrecs

import android.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

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
                painter = painterResource(id = product.imageResId),
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
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(products) { product ->
            CarouselCard(product) {
                when (product.name) {
                    "Toothbrushes" -> navController.navigate("toothbrushes")
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
            .width(160.dp)
            .height(160.dp),
        shape = (RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(product.imageResId),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(product.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(product.description, fontSize = 10.sp, color = Color.Gray)
        }
    }
}


// ALl the product categories
@Composable
fun ProductRecommendations(modifier: Modifier = Modifier, navController: NavController) {
    val mockProducts = listOf(
        Product("Toothbrushes", R.drawable.ic_menu_camera, "test", true),
        Product("Whitening Toothpaste", R.drawable.ic_menu_camera, "test", true),
        Product("Floss", R.drawable.ic_menu_camera, "test", true),
        Product("Mouthwash", R.drawable.ic_menu_camera, "test")
    )

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        ProductShopPage(products = mockProducts, navController = navController)

        // Button to go back home.
        TextButton(onClick = {
            navController.navigate("home")
        }) {
            Text(text = "Home", fontSize = 16.sp)
        }
    }
}

// Basic class of each product
data class Product(
    val name: String,
    val imageResId: Int,
    val description: String,
    val isTopProduct: Boolean = false
)

/*
@Preview(showBackground = true)
@Composable
fun ProductShopPagePreview() {
    val mockProducts = listOf(
        Product("Electric Toothbrush", android.R.drawable.ic_menu_camera, "test", true),
        Product("Whitening Toothpaste", android.R.drawable.ic_menu_camera, "test", true),
        Product("Floss", android.R.drawable.ic_menu_camera, "test", true),
        Product("Manual Toothbrush", android.R.drawable.ic_menu_camera, "test"),
        Product("Mouthwash", android.R.drawable.ic_menu_camera, "test")
    )

    MaterialTheme {
        ProductShopPage(products = mockProducts)
    }
}*/
