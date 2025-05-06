package com.start.pages.profile_pages

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.dentalhygiene.R
import com.start.repos.PointsProgressionRepo
import com.start.viewmodels.Emblem
import com.start.viewmodels.PointsProgressionViewModel
import com.start.viewmodels.Prestige
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmblemsPage(modifier: Modifier, navController: NavController,
                pointsProgressionViewModel: PointsProgressionViewModel
) {

    // Collect the flows of prestige and exp emitted from view model.
    val prestige = pointsProgressionViewModel.prestige.collectAsState()
    val exp = pointsProgressionViewModel.experience.collectAsState()
    // Store the exp value from the previous recomposition of the UI. Be sure to update it when
    // exp changes.
    val expTemp = remember {mutableIntStateOf(exp.value.toInt())}
    val prestigeTemp = remember {mutableIntStateOf(prestige.value.toInt())}

    val prestigeInfo: Prestige = pointsProgressionViewModel.prestiges[prestige.value.toInt()]
    // Get the max experience for the current prestige of the user from the array that
    // stores objects of prestiges.
    val maxExp: Long = pointsProgressionViewModel.prestiges[prestigeTemp.intValue].maxExp

    // Variable to store the level progress of the progress bar. Will be updated iteratively
    // in a coroutine to display the progress increasing/decreasing. Initialize it with the initial
    // progress calculated by user exp and max exp for their prestige. Will be remembered across all
    // recompositions.
    var levelProgress by remember {mutableStateOf(exp.value.toFloat() / maxExp.toInt())}

    // Coroutine scope to reset points and increase prestige.
    val scope = rememberCoroutineScope()

    // Boolean to store if the progress bar is moving or not.
    var progressMoving by remember {mutableStateOf(false)}

    // Fake Emblem object and Emblem list for testing
    //val tooth = Emblem(1L, "Toothpick", 100L, "https://firebasestorage.googleapis.com/v0/b/dentalhygiene-bc3cd.firebasestorage.app/o/emblems%2Fprestige0%2Fcool%20green%20emoji%20toothpick.png?alt=media&token=265319af-69d0-4c09-922f-411529049524")
    //var emblemsTest : List<Emblem> = mutableListOf(tooth, tooth, tooth, tooth, tooth, tooth, tooth, tooth)

    //Gets all valid emblems from the database to be used in the EmblemList. Emblems that are less than or equal to the user's current prestige level are valid.
    pointsProgressionViewModel.getEmblems(prestige.value)
    val emblems by pointsProgressionViewModel.emblemList

    // Launched effect to update the progress bar reactively to the changes in database.
    LaunchedEffect(exp.value) {


        // Previous percentage the bar was at based off user experience, vs the current
        // percentage the bar is currently at when it increased/decreased.
        val prevPercentage = ((expTemp.intValue.toFloat() / maxExp.toInt()) * 100).toInt()
        val currPercentage = ((exp.value.toFloat() / maxExp.toInt()) * 100).toInt()

        Log.d("PREV PERCENTAGE", prevPercentage.toString())
        Log.d("CURR PERCENTAGE", currPercentage.toString())

        // State that the progress bar is moving.
        progressMoving = true

        // Call back function to update the progress bar. Pass in integer values of percentage.
        // So if you want 10% pass in 10 etc.
        reactiveProgress(prevPercentage, currPercentage) { progress ->
            // Update the progress bar from the value that is passed in.
            levelProgress = progress
        }

        // When progression is finished, temp values become the current values.
        expTemp.intValue = exp.value.toInt()
        prestigeTemp.intValue = prestige.value.toInt()

        // State that the progress bar is not moving.
        progressMoving = false
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title =
                {
                    Text(
                        text = "Emblem Shop",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back Button",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.secondaryContainer)
            )
        },
        bottomBar = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ){
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    if (progressMoving) {
                        Text("${(maxExp.toInt() * levelProgress).roundToInt()} / ${maxExp.toInt()}",)
                    } else {
                        Text("${exp.value.toInt()} / ${maxExp.toInt()}")
                    }
                }
                // Progress Indicator for the points bar.
                LinearProgressIndicator(
                    progress = { levelProgress },
                    modifier = modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .padding(bottom = 4.dp),
                    color = Color.Green
                )
            }
        }
    ){innerPadding ->
        // Adds the EmblemList as the body of the page
        EmblemList(
            emblems = emblems,
            //onEmblemClick = { navController.popBackStack() },
            //onButtonClick = {},
            modifier = Modifier.padding(innerPadding),
            pointsProgressionViewModel = pointsProgressionViewModel
        )
    }
}

// A composable for the list of emblems that the user can scroll through and choose from based on their prestige level
@Composable
fun EmblemList(pointsProgressionViewModel: PointsProgressionViewModel, emblems: List<Emblem>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        items(emblems) { emblem ->

            var owned by remember { mutableStateOf(false)}
            LaunchedEffect(emblem.name) {
                pointsProgressionViewModel.isOwned(emblem.name){ result ->
                    owned = result
                }
            }

            // Checks if the user already owns the emblem. If they do, the button displays "Equip"
            // Else if the user has already equipped the emblem, the button displays "Unequipped"
            // Else, the user does not own the emblem, so the button displays "Buy"
            val buttonText = when {
                pointsProgressionViewModel.equippedEmblem.value == emblem.imageUrl -> "Unequip"
                owned && (pointsProgressionViewModel.equippedEmblem.value != emblem.imageUrl) -> "Equip"
                else -> "Buy"
            }

            val buttonColor = when (buttonText) {
                "Buy"     -> Color(0xFF009900)
                "Equip"   -> Color(0xFF128DF9)
                "Unequip" -> Color(0xFFFD6868)
                else      -> MaterialTheme.colorScheme.primary
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    //.padding(vertical = 8.dp)
            ){
                EmblemCard(emblem = emblem, pointsProgressionViewModel)
                Spacer(modifier = Modifier.width(4.dp))
                Button(
                    onClick = {
                        // When the button says "Buy", pressing it buys the emblem
                        // When the button says "Equip", pressing it equips the emblem
                        when (buttonText)
                        {
                            "Buy" -> {
                                pointsProgressionViewModel.buyEmblem(emblem)
                                owned = true
                            }
                            "Equip" -> pointsProgressionViewModel.equipEmblem(emblem)
                            "Unequip" -> pointsProgressionViewModel.unequipEmblem()
                        }

                    },
                    // If the button is already equipped, the button is disabled
                    //enabled = buttonText != "Equipped",
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonColors(
                        buttonColor,
                        contentColor = Color.White,
                        disabledContainerColor = buttonColor,
                        disabledContentColor = buttonColor
                    ),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(86.dp)
                        //.padding(start = 4.dp)
                )
                {
                    Text(
                        text = buttonText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// Composable for each individual Emblem card that appears in the Emblem List.
@Composable
fun EmblemCard(emblem: Emblem, pointsProgressionViewModel: PointsProgressionViewModel) {
    // Gathers the user's prestige state
    val prestige = pointsProgressionViewModel.prestige.collectAsState()
    // Gathers the user's current prestige level
    val prestigeInfo: Prestige = pointsProgressionViewModel.prestiges[prestige.value.toInt()]

    val emblemColor = when (emblem.prestige)
    {
        0L -> Color(0xFFFAF9E8)
        1L -> Color(0xFF24E966)
        2L -> Color(0xFFFFEE06)
        3L -> Color(0xFFFD8C0A)
        4L -> Color(0xFFFA335B)
        5L -> Color(0xFF398FFF)
        6L -> Color(0xFFAB62FF)
        else -> Color(0xFFFFCE06)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(fraction = 0.70f),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardColors(
            MaterialTheme.colorScheme.onTertiaryContainer,
            MaterialTheme.colorScheme.secondaryContainer,
            Color.LightGray,
            Color.Black)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            // Displays the emblem itself
            AsyncImage(
                model = emblem.imageUrl,
                contentDescription = emblem.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column (
                verticalArrangement = Arrangement.SpaceEvenly
            ){
                // Displays the emblem's name
                Text(
                    text = emblem.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondaryContainer
                )
                // Displays the emblem's price
                Text(
                    text = "${emblem.price} PTS",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Magenta,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
                // Displays the emblem's prestige Tier level in the tier's specific color
                Text(
                    text = "Prestige ${emblem.prestige} Emblem",
                    style = MaterialTheme.typography.bodySmall,
                    color = emblemColor,
                    fontWeight = FontWeight.Bold,
                    //fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
            }
        }
    }
}

/**
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun EmblemPageLayout() {
    val tooth = Emblem("https://firebasestorage.googleapis.com/v0/b/dentalhygiene-bc3cd.firebasestorage.app/o/emblems%2Fprestige0%2Fcool%20green%20emoji%20toothpick.png?alt=media&token=265319af-69d0-4c09-922f-411529049524", "Toothpick", 0, 100L)
    val emblems : List<Emblem> = mutableListOf(tooth, tooth, tooth, tooth, tooth, tooth, tooth, tooth)

    val modifier = Modifier
    Scaffold(
        topBar = {
            TopAppBar(
                title =
                {
                    Text(
                        text = "Emblem Shop",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back Button",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults. topAppBarColors(MaterialTheme.colorScheme.secondaryContainer)
            )
        },
        bottomBar = {
            // Progress Indicator for the points bar.
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Top
                )
                {
                    Text("600/1000")
                }
                LinearProgressIndicator(
                    progress = { 0.5F },
                    modifier = modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .padding(bottom = 4.dp),
                    color = Color.Green
                )
            }
        }
    ) { innerPadding ->
        EmblemList(
            emblems = emblems,
            onEmblemClick = { /* handle click */ },
            modifier = Modifier.padding(innerPadding),
            pointsProgressionViewModel = TODO(),
        )
    }
}
**/

