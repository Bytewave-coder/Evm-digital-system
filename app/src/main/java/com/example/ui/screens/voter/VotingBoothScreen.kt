package com.example.ui.screens.voter

import android.app.Activity
import android.graphics.Bitmap
import android.view.WindowManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.filled.Person
import com.example.data.CandidateWithStudent
import com.example.ui.MainViewModel
import com.example.ui.VoteResult
import com.example.ui.theme.CardBackground
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VotingBoothScreen(
    electionId: String,
    viewModel: MainViewModel,
    onVoteSuccess: () -> Unit
) {
    val candidates by viewModel.candidatesForActiveElection.collectAsState()
    val currentVoter by viewModel.currentVoter.collectAsState()
    val voteResult by viewModel.voteResultState.collectAsState()
    
    var candidateToVote by remember { mutableStateOf<CandidateWithStudent?>(null) }
    var voterNameConfirm by remember { mutableStateOf("") }
    var isVerifying by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        if (bitmap != null) {
            candidateToVote?.let { candidate ->
                viewModel.castVote(voterNameConfirm, candidate.candidate.id, electionId)
                candidateToVote = null
                isVerifying = false
            }
        }
    }
    
    DisposableEffect(Unit) {
        val activity = context as? Activity
        // Removing FLAG_SECURE to allow screen streaming in emulator
        onDispose {
            // Nothing to clear
        }
    }
    
    LaunchedEffect(electionId) {
        viewModel.loadCandidatesForElection(electionId)
    }
    
    LaunchedEffect(voteResult) {
        if (voteResult is VoteResult.Success) {
            viewModel.clearVoteResult()
            onVoteSuccess()
        }
    }

    if (isVerifying && candidateToVote != null) {
        AlertDialog(
            onDismissRequest = { isVerifying = false },
            title = { Text("Security Check") },
            text = {
                Column {
                    Text("You are casting your vote for ${candidateToVote?.student?.name}.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Next, you will need to capture a photo for security purposes.", color = Color.Gray, fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        voterNameConfirm = currentVoter?.name ?: "Unknown"
                        cameraLauncher.launch(null)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Take Photo & Vote")
                }
            },
            dismissButton = {
                TextButton(onClick = { isVerifying = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = CardBackground,
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { 
                    Text("Voting Booth", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                "Select your candidate", 
                color = Color.White, 
                fontSize = 18.sp, 
                modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 16.dp)
            )
            
            if (candidates.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Loading candidates or no candidates found...", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(candidates) { item ->
                        CandidateRow(
                            candidateItem = item,
                            onVoteClick = { 
                                candidateToVote = item
                                voterNameConfirm = ""
                                isVerifying = true
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CandidateRow(candidateItem: CandidateWithStudent, onVoteClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with Coil
            if (candidateItem.student.photoUri != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(candidateItem.student.photoUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Candidate Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.DarkGray)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(candidateItem.student.name.take(1), color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(candidateItem.student.name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (candidateItem.candidate.partySymbolUri != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(candidateItem.candidate.partySymbolUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Party Logo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(candidateItem.candidate.partyName, color = PrimaryBlue, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            IconButton(
                onClick = onVoteClick,
                modifier = Modifier
                    .size(64.dp)
                    .background(PrimaryBlue, CircleShape)
                    .clip(CircleShape)
            ) {
                Icon(
                    Icons.Default.HowToVote, 
                    contentDescription = "Vote", 
                    tint = Color.White, 
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
