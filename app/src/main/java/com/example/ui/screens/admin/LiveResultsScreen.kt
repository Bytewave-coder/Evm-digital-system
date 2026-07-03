package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.data.CandidateWithStudent
import com.example.ui.MainViewModel
import com.example.ui.theme.CardBackground
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveResultsScreen(
    electionId: String,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val candidates by viewModel.candidatesForActiveElection.collectAsState()
    val election by viewModel.getElectionById(electionId).collectAsState(initial = null)
    
    LaunchedEffect(electionId) {
        viewModel.loadCandidatesForElection(electionId)
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Live Results", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(election?.title ?: "Loading...", color = Color.Gray, fontSize = 14.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            
            if (candidates.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No results available yet.", color = Color.Gray)
                }
            } else {
                candidates.forEachIndexed { index, item ->
                    val color = when (index % 3) {
                        0 -> Color(0xFF6366F1) // Indigo
                        1 -> Color(0xFFFACC15) // Yellow
                        else -> Color(0xFF22C55E) // Green
                    }
                    CandidateResultBar(
                        candidate = item,
                        color = color
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun CandidateResultBar(candidate: CandidateWithStudent, color: Color) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        if (candidate.student.photoUri != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(candidate.student.photoUri)
                    .crossfade(true)
                    .build(),
                contentDescription = "Candidate Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.DarkGray)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Text(candidate.student.name.take(1), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(candidate.student.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    if (candidate.candidate.partySymbolUri != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(candidate.candidate.partySymbolUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Party Logo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                        )
                    }
                }
                Text("${candidate.candidate.voteCount} Votes", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
