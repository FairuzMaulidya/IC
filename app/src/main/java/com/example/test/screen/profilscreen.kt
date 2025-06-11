package com.example.test.screens

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.test.data.Profile
import com.example.test.util.UserDataStore
import com.example.test.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(navController: NavHostController, viewModel: ProfileViewModel = viewModel()) {
    val context = LocalContext.current
    val usernameFlow = remember { UserDataStore.getUsername(context) }
    val username by usernameFlow.collectAsState(initial = null)

    LaunchedEffect(username) {
        username?.let { viewModel.loadProfile(it) }
    }

    val profile by viewModel.profile.observeAsState()
    var name by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Load profile data ke form
    LaunchedEffect(profile) {
        profile?.let {
            name = it.name
            dob = it.dateOfBirth
            region = it.region
            country = it.country
            phone = it.mobile
            imageUri = it.photoUri?.let(Uri::parse)
        }
    }

    fun createImageUri(context: Context): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "profile_image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ProfileApp")
        }
        return context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }

    var tempCameraImageUri by remember { mutableStateOf<Uri?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            imageUri = tempCameraImageUri
            tempCameraImageUri?.let { viewModel.updatePhotoUri(it.toString()) }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUri = uri
            viewModel.updatePhotoUri(uri.toString())
        }
    }


    // Permissions
    val requiredPermissions = remember {
        mutableStateListOf<String>().apply {
            add(Manifest.permission.CAMERA)
            if (Build.VERSION.SDK_INT < 33) {
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        }
    }

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results -> }

    LaunchedEffect(Unit) {
        val notGranted = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        if (notGranted.isNotEmpty()) {
            permissionsLauncher.launch(notGranted.toTypedArray())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image
        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color(0xFFDE5C9D), CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color(0xFFDE5C9D), CircleShape),
                tint = Color(0xFFDE5C9D)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Button Row
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                onClick = {
                    tempCameraImageUri = createImageUri(context)
                    tempCameraImageUri?.let { takePictureLauncher.launch(it) }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDE5C9D))
            ) {
                Text("Take Photo")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { galleryLauncher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDE5C9D))
            ) {
                Text("Upload File")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input Form
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEFEF))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ProfileTextField("Username:", name) { name = it }
                ProfileTextField("Date of Birth:", dob) { dob = it }
                ProfileTextField("Region:", region) { region = it }
                ProfileTextField("Country:", country) { country = it }
                ProfileTextField("Mobile:", phone, KeyboardType.Phone) { phone = it }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Save/Delete Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    username?.let {
                        viewModel.saveProfile(
                            Profile(
                                username = it,
                                name = name,
                                dateOfBirth = dob,
                                region = region,
                                country = country,
                                mobile = phone,
                                photoUri = imageUri?.toString()
                            )
                        )
                        viewModel.loadProfile(it) // <--- Memastikan sidebar ikut update
                        navController.popBackStack()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDE5C9D))
            ) {
                Text("Save Changes")
            }

            }
        }
    }

@Composable
fun ProfileTextField(label: String, value: String, keyboardType: KeyboardType = KeyboardType.Text, onChange: (String) -> Unit) {
    Text(label)
    TextField(
        value = value,
        onValueChange = onChange,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        singleLine = true
    )
}
