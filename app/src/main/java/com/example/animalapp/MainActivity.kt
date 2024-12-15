package com.example.animalapp

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.animalapp.ui.theme.AnimalAppTheme

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnimalAppTheme {
                AnimalApp()
            }
        }
    }
}

// Menu três pontinhos
@Composable
fun AnimalAppMenu(onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon( Icons.Default.MoreVert, contentDescription = "Menu" )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
            text = { Text("Dog") },
            onClick = {
                expanded = false
                onOptionSelected("Dog")
            }
        )
        DropdownMenuItem(
            text = { Text("Cat") },
            onClick = {
                expanded = false
                onOptionSelected("Cat")
            }
        )
    }
}

// Tela principal
@ExperimentalMaterial3Api
@Composable
fun HomeScreen(onAnimalSelected: (String) -> Unit) {
    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("AnimalApp") },
                actions = {
                    AnimalAppMenu(onOptionSelected = onAnimalSelected)
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text("Seleciona um animal no menu")
        }
    }
}

// Tela de animal
@Composable
fun AnimalScreen(animal: String) {
    val context = LocalContext.current
    val imageRes = if (animal == "Dog") R.drawable.dog else R.drawable.cat
    val soundRes = if (animal == "Dog") R.raw.dog_bark else R.raw.cat_meow
    val videoRes = if (animal == "Dog") R.raw.dog_video else R.raw.cat_video

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Imagem do animal
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "$animal Image",
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botão para reproduzir o som
        Button(
            onClick = {
                try {
                    val mediaPlayer = MediaPlayer.create(context, soundRes)
                    if (mediaPlayer != null) {
                        mediaPlayer.start()
                        mediaPlayer.setOnCompletionListener { mediaPlayer.release() }
                    } else {
                        println("Erro: MediaPlayer retornou null para o recurso $soundRes")
                    }
                } catch (e: Exception) {
                    println("Erro ao incializar MediaPlayer")
                }
            }
        ) {
            Text("Reproduzir Som")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botão para reproduzir o video
        Button(
            onClick = {
                val intent = Intent(context, VideoPlayerActivity::class.java)
                intent.putExtra("videoRes", videoRes)
                context.startActivity(intent)
            }
        ) {
            Text("Reproduzir Video")
        }
    }
}

// Define a lógica de navegação e exibe as telas
@ExperimentalMaterial3Api
@Composable
fun AnimalApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen( onAnimalSelected = {
                animal -> navController.navigate("animal/$animal")
            })
        }

        composable(
            "animal/{animal}",
            arguments = listOf(navArgument("animal") { type = NavType.StringType })
        ) { navBackStackEntry ->
            val animal = navBackStackEntry.arguments?.getString("animal") ?: "Dog"
            AnimalScreen(animal)
        }
    }
}