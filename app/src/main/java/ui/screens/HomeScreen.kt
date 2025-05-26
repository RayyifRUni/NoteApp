package ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.notesapp.R
import data.Note
import data.NoteRepository
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onAddNote: () -> Unit,
    onEditNote: (String) -> Unit
) {
    val repository = NoteRepository()
    val coroutineScope = rememberCoroutineScope()
    var notes by remember { mutableStateOf<List<Note>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }

    fun loadNotes() {
        coroutineScope.launch {
            try {
                val result = repository.getNotes()
                if (result.isSuccess) {
                    notes = result.getOrNull() ?: emptyList()
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Gagal memuat catatan"
                }
            } catch (e: Exception) {
                Log.e("HomeScreen", "Error memuat catatan: ${e.message}", e)
                errorMessage = "Error memuat catatan: ${e.message}"
            }
        }
    }

    fun deleteNote(noteId: String) {
        coroutineScope.launch {
            try {
                val result = repository.deleteNote(noteId)
                if (result.isSuccess) {
                    loadNotes()
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Gagal menghapus catatan"
                }
            } catch (e: Exception) {
                Log.e("HomeScreen", "Error menghapus catatan: ${e.message}", e)
                errorMessage = "Error menghapus catatan: ${e.message}"
            }
        }
    }

    fun downloadNote(note: Note) {
        // Implement download functionality
        Log.d("HomeScreen", "Download note: ${note.title}")
    }

    LaunchedEffect(Unit) {
        loadNotes()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Log.d("HomeScreen", "Tombol Tambah Catatan diklik")
                    try {
                        onAddNote()
                    } catch (e: Exception) {
                        Log.e("HomeScreen", "Error navigasi ke AddEditNoteScreen: ${e.message}", e)
                        errorMessage = "Gagal membuka layar tambah catatan: ${e.message}"
                    }
                },
                containerColor = Color(0xFFFFD5C17), // Warna oranye
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Catatan"
                )
            }
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            item {
                // Header dengan logo dan judul
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.stickies_logo),
                        contentDescription = "Stickies Logo",
                        modifier = Modifier.size(115.dp)
                    )
                }
            }

            if (notes.isEmpty()) {
                item {
                    Text(
                        text = "Tidak ada catatan. Tambah catatan baru!",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    )
                }
            } else {
                items(notes) { note ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFEECE13) // Kuning terang
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Konten catatan
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                note.imageUrl?.let { url ->
                                    AsyncImage(
                                        model = url,
                                        contentDescription = "Gambar Catatan",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(140.dp)
                                            .padding(bottom = 8.dp)
                                    )
                                }

                                Text(
                                    text = note.title,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = note.content,
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                            }

                            // Button actions di sebelah kanan
                            Column(
                                modifier = Modifier.padding(start = 12.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                IconButton(
                                    onClick = { onEditNote(note.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { deleteNote(note.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { downloadNote(note) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Download,
                                        contentDescription = "Download",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (errorMessage.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorMessage, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}