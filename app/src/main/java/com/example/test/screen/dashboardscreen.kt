package com.example.test.screen

import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import coil.compose.AsyncImage
import com.example.test.R
import com.example.test.data.Project
import com.example.test.screens.ProfileScreen
import com.example.test.viewmodel.ProfileViewModel
import com.example.test.screen.ContentScreen
import com.example.test.util.UserDataStore
import com.example.test.viewmodel.ProjectViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(currentTitle: String, onMenuClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(currentTitle, color = Color(0xFFE58DD6))
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color(0xFFE58DD6))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardMainScreen() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val internalNavController = rememberNavController()
    val navBackStackEntry by internalNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "dashboard"
    val context = LocalContext.current
    val viewModel: ProjectViewModel = viewModel(factory = ProjectViewModel.Factory(context.applicationContext as android.app.Application))
    val projects by viewModel.allProjects.observeAsState(initial = emptyList())

    val title = when (currentRoute) {
        "data_entry" -> "Data Entry"
        "train_model" -> "Train Model"
        "profile" -> "Edit Profil"
        "data_api" -> "Data API"
        "project" -> "Projek"
        "request_dataset" -> "Request Dataset"
        "data_processing" -> "Data Processing"
        else -> "Dashboard"
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Sidebar(internalNavController)
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopBar(currentTitle = title) {
                    coroutineScope.launch { drawerState.open() }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                NavigationHost(internalNavController, projects)
            }
        }
    }
}

@Composable
fun Sidebar(navController: NavHostController, viewModel: ProfileViewModel = viewModel()) {
    val context = LocalContext.current

    val usernameFlow = remember { UserDataStore.getUsername(context) }
    val username by usernameFlow.collectAsState(initial = null)
    val displayedUsername = username ?: "Admin"

    LaunchedEffect(username) {
        username?.let { viewModel.loadProfile(it) }
    }

    val profile by viewModel.profile.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE58DD6))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate("profile") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }
                .padding(vertical = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                if (!profile?.photoUri.isNullOrEmpty()) {
                    AsyncImage(
                        model = Uri.parse(profile?.photoUri),
                        contentDescription = "Profile Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default Icon",
                        tint = Color(0xFFE58DD6),
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(displayedUsername, color = Color.White, fontWeight = FontWeight.Bold)
                Text("Edit Profile", color = Color.White, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DrawerItem(icon = Icons.Default.Dashboard, label = "Dashboard") {
            navController.navigate("dashboard") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }

        DrawerItem(icon = Icons.Default.Edit, label = "Data Entry") {
            navController.navigate("data_entry") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }

        DrawerItem(icon = Icons.Default.AutoGraph, label = "Train Model") {
            navController.navigate("train_model") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }

        DrawerItem(icon = Icons.Default.CloudDownload, label = "API") {
            navController.navigate("data_api") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
        DrawerItem(icon = Icons.Default.Work, label = "Project") {
            navController.navigate("project") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
        DrawerItem(icon = Icons.Default.Work, label = "Request Dataset") {
            navController.navigate("request_dataset") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
        DrawerItem(icon = Icons.Default.Work, label = "Data Processing") {
            navController.navigate("data_processing") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
}

@Composable
fun DrawerItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, color = Color.White, fontSize = 16.sp)
    }
}

@Composable
fun NavigationHost(navController: NavHostController, projects: List<Project>) {
    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            DashboardScreen(projects, navController)
        }
        composable("data_entry") {
            DataEntryScreen(navController)
        }
        composable("train_model") {
            TrainModelScreen(navController)
        }
        composable("profile") {
            ProfileScreen(navController)
        }
        composable("data_api") {
            ContentScreen()
        }
        composable("project") {
            ProjectScreen(navController)
        }
        composable("request_dataset") {
            RequestDatasetScreen(navController)
        }
        composable("data_processing") {
            DataProcessingScreen(navController)
        }
    }
}

@Composable
fun DashboardScreen(projects: List<Project>, navController: NavHostController)
{
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(8.dp)
    ) {
        Text(
            text = "Dashboard",
            color = Color(0xFFDC74D8),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            Text("Deteksi Hewan")
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFDADA)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text("Visualisasi Proses Training", fontWeight = FontWeight.Bold)
                Row {
                    Image(
                        painter = painterResource(R.drawable.train),
                        contentDescription = null,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF8A7)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Perbandingan akurasi antar model", fontWeight = FontWeight.Medium)
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("45%", color = Color.Blue, fontWeight = FontWeight.Bold)
                        Box(
                            modifier = Modifier
                                .height(60.dp)
                                .width(30.dp)
                                .background(Color.Blue)
                        )
                        Text("CNN")
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("65%", color = Color.Magenta, fontWeight = FontWeight.Bold)
                        Box(
                            modifier = Modifier
                                .height(90.dp)
                                .width(30.dp)
                                .background(Color.Magenta)
                        )
                        Text("MobileNet")
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("85%", color = Color.Red, fontWeight = FontWeight.Bold)
                        Box(
                            modifier = Modifier
                                .height(110.dp)
                                .width(30.dp)
                                .background(Color.Red)
                        )
                        Text("VGG16")
                    }
                }
            }
        }

        Text("Daftar Proyek Terbaru", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))

        // Scroll Horizontal Table
        Box(modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp)) {
            Column {
                Row(modifier = Modifier.background(Color(0xFFF1F1F1)).padding(8.dp)) {
                    TableHeaderCell("#", 40.dp)
                    TableHeaderCell("Nama Proyek", 120.dp)
                    TableHeaderCell("Deskripsi", 120.dp)
                    TableHeaderCell("Status", 80.dp)
                    TableHeaderCell("Dibuat Oleh", 100.dp)
                    TableHeaderCell("Tanggal Dibuat", 120.dp)
                    TableHeaderCell("Aksi", 100.dp)
                }

                projects.forEachIndexed { index, project ->
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)) {
                        TableCell((index + 1).toString(), 40.dp)
                        TableCell(project.projectName, 120.dp)
                        TableCell(project.description ?: "-", 120.dp)
                        StatusCell(project.status, 80.dp)
                        TableCell(project.createdBy ?: "N/A", 100.dp)
                        TableCell(project.startDate ?: "N/A", 120.dp)
                        Row(modifier = Modifier.width(100.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Button(onClick = { /* TODO: Handle View */ }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) {
                                Text("View", fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Tombol bawah
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Button(
                onClick = { navController.navigate("project") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Buat Proyek Baru", color = Color.White)
            }
        }
    }
}

@Composable
fun TableHeaderCell(text: String, width: Dp) {
    Text(
        text,
        modifier = Modifier
            .width(width)
            .padding(4.dp),
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )
}


@Composable
fun StatusCell(status: String?, width: Dp) {
    val color = when (status?.lowercase()) {
        "completed" -> Color(0xFF4CAF50)
        "ongoing" -> Color(0xFF03A9F4)
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .width(width)
            .padding(4.dp)
            .background(color = color, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(status ?: "-", color = Color.White, fontSize = 12.sp)
    }
}
