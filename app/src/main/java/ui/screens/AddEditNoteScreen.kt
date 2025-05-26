package ui.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
// import androidx.compose.foundation.Image // Tidak digunakan secara langsung di sini, AsyncImage dari Coil
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
// import androidx.compose.material.icons.filled.StickyNote2 // Tidak digunakan karena painterResource
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale // Pastikan import ini ada
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.notesapp.R // Pastikan R.drawable.stickies_icon ada di project Anda
import data.Note
import data.NoteRepository
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight


@Composable
fun AddEditNoteScreen(
    noteId: String?,
    onNoteSaved: () -> Unit
) {
    val repository = NoteRepository()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var existingImageUrl by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(noteId) {
        if (noteId != null) {
            Log.d("AddEditNoteScreen", "Loading note with ID: $noteId")
            try {
                val result = repository.getNotes()
                if (result.isSuccess) {
                    val note = result.getOrNull()?.find { it.id == noteId }
                    if (note != null) {
                        title = note.title
                        content = note.content
                        existingImageUrl = note.imageUrl
                        Log.d("AddEditNoteScreen", "Note loaded: $note")
                    } else {
                        errorMessage = "Note not found"
                        Log.e("AddEditNoteScreen", "Note with ID $noteId not found")
                    }
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Failed to load note"
                    errorMessage = error
                    Log.e("AddEditNoteScreen", "Failed to load note: $error")
                }
            } catch (e: Exception) {
                Log.e("AddEditNoteScreen", "Exception while loading note: ${e.message}", e)
                errorMessage = "Error loading note: ${e.message}"
            }
        } else {
            Log.d("AddEditNoteScreen", "Add mode: No noteId provided")
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
        Log.d("AddEditNoteScreen", "Image picked: $uri")
    }

    // Warna latar belakang field untuk tema terang
    val fieldBackgroundColorLight = Color(0xFFF0F0F0) // Abu-abu sangat muda

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White // PERUBAHAN: Latar belakang putih
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.stickies_logo), // Ganti dengan gambar kamu
                contentDescription = "Logo Stickies",
                modifier = Modifier.size(115.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Add Your Notes!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 28.dp),
                style = TextStyle(color = Color.Black) // PERUBAHAN: Teks hitam
            )
            val borderColor = Color.LightGray
            val outlinedDescTextFieldColors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.LightGray, // Warna border saat fokus
                unfocusedBorderColor = Color.LightGray, // Warna border saat tidak fokus
                focusedLeadingIconColor = Color.DarkGray, // Sesuaikan jika perlu
                unfocusedLeadingIconColor = Color.DarkGray, // Sesuaikan jika perlu
                focusedPlaceholderColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = Color.Black
            )
            val fieldShape = RoundedCornerShape(24.dp)

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp) // Padding untuk jarak antar elemen
                    .shadow( // Shadow diterapkan pada Modifier yang sama dengan TextField
                        elevation = 4.dp,
                        shape = fieldShape, // Menggunakan fieldShape yang konsisten (misal, 24.dp)
                        clip = false // Umumnya false agar shadow terlihat di luar batas shape
                    )
                    .height(56.dp), // Tinggi TextField
                placeholder = { Text("Judul") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Notes,
                        contentDescription = "Judul Icon"
                    )
                },
                shape = fieldShape, // Bentuk TextField
                colors = outlinedDescTextFieldColors,
                singleLine = true
            )

            // Description Field
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp) // Padding untuk jarak antar elemen
                    .shadow( // Shadow diterapkan pada Modifier yang sama dengan TextField
                        elevation = 4.dp,
                        shape = fieldShape, // Menggunakan fieldShape yang konsisten
                        clip = false
                    )
                    .height(150.dp),// Spasi di bawah elemen ini
                placeholder = {
                    Text(
                        text = "Deskripsi",
                        modifier = Modifier.offset(y = (46).dp)
                    )

                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Notes,
                        contentDescription = "Deskripsi Icon",
                        modifier = Modifier.size(24.dp)
                    )
                },
                shape = fieldShape, // Bentuk sudut tetap rounded
                colors = outlinedDescTextFieldColors, // PERUBAHAN: Menggunakan warna baru
                maxLines = 5
            )

            // Image Picker (custom)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow( // Shadow diterapkan sebelum background
                        elevation = 4.dp,
                        shape = fieldShape // Menggunakan fieldShape yang konsisten
                    )
                    .background(Color.White, shape = fieldShape)
                    .border(BorderStroke(1.dp, borderColor), shape = fieldShape)// Background dengan shape
                    .clip(fieldShape) // Clip konten agar sesuai shape (opsional setelah background)
                    .clickable { launcher.launch("image/*") }
                    .padding(horizontal = 16.dp), // Padding internal untuk konten Row
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Notes,
                    contentDescription = "Image Icon",
                    tint = Color.DarkGray
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Image",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }

            val imageToDisplay = imageUri ?: existingImageUrl?.let { Uri.parse(it) }

            if (imageToDisplay != null) {
                Spacer(modifier = Modifier.height(16.dp))
                AsyncImage(
                    model = imageToDisplay,
                    contentDescription = "Selected or existing image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Gray.copy(alpha = 0.2f)), // Latar sedikit abu untuk placeholder
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (title.isBlank() || content.isBlank()) {
                        errorMessage = "Judul dan Deskripsi tidak boleh kosong"
                        return@Button
                    }
                    coroutineScope.launch {
                        // ... (logika penyimpanan tetap sama)
                        try {
                            val imageUrlToSave = imageUri?.let { uri ->
                                Log.d("AddEditNoteScreen", "Uploading image...")
                                val uploadResult = repository.uploadImage(uri, context)
                                uploadResult.getOrThrow().also {
                                    Log.d("AddEditNoteScreen", "Image uploaded successfully. URL: $it")
                                }
                            } ?: existingImageUrl

                            Log.d("AddEditNoteScreen", "Image URL to save: $imageUrlToSave")

                            val note = Note(
                                id = noteId ?: "",
                                title = title,
                                content = content,
                                imageUrl = imageUrlToSave
                            )
                            Log.d("AddEditNoteScreen", "Saving note to Firebase: $note")
                            val result = repository.saveNote(note)
                            if (result.isSuccess) {
                                Log.d("AddEditNoteScreen", "Note saved successfully")
                                onNoteSaved()
                            } else {
                                val error = result.exceptionOrNull()?.message ?: "Failed to save note"
                                Log.e("AddEditNoteScreen", "Failed to save note: $error")
                                errorMessage = "Gagal menyimpan catatan: $error"
                            }
                        } catch (e: Exception) {
                            Log.e("AddEditNoteScreen", "Exception while saving note: ${e.message}", e)
                            errorMessage = "Terjadi kesalahan: ${e.message}"
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = title.isNotBlank() && content.isNotBlank(),
                shape = fieldShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6F00), // Oranye tetap kontras
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.LightGray
                )
            ) {
                Text("Tambahkan", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage,
                    color = Color.Red, // PERUBAHAN: Warna error standar
                    style = TextStyle(fontSize = 14.sp),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}