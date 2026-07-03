package com.example.ui.screens.voter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.CardBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoterLoginScreen(
    viewModel: MainViewModel,
    onLoginSuccess: (String) -> Unit, // returns the electionId to navigate to
    onBack: () -> Unit
) {
    val ongoingElections by viewModel.ongoingElections.collectAsState()
    var selectedElectionId by remember { mutableStateOf<String?>(null) }
    var voterName by remember { mutableStateOf("") }
    var rollNumber by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (selectedElectionId != null) {
        AlertDialog(
            onDismissRequest = { 
                selectedElectionId = null
                errorMessage = null
            },
            title = { Text("Voter Identity") },
            text = {
                Column {
                    Text("Please enter your name and roll number to proceed.")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = voterName,
                        onValueChange = { voterName = it },
                        label = { Text("Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = rollNumber,
                        onValueChange = { rollNumber = it },
                        label = { Text("Roll Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(errorMessage!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (voterName.isNotBlank() && rollNumber.isNotBlank()) {
                            viewModel.verifyVoterLogin(voterName, rollNumber, selectedElectionId!!) { success, msg ->
                                if (success) {
                                    onLoginSuccess(selectedElectionId!!)
                                    selectedElectionId = null
                                    errorMessage = null
                                } else {
                                    errorMessage = msg
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Verify")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    selectedElectionId = null 
                    errorMessage = null
                }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Select Session", color = Color.White) },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Which session/class are you voting in?",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            if (ongoingElections.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No active sessions available.", color = Color.Gray, fontSize = 16.sp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(ongoingElections) { election ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedElectionId = election.id },
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(PrimaryBlue.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.HowToVote, contentDescription = null, tint = PrimaryBlue)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(election.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Class: ${election.classTarget} (${election.sectionTarget})", color = Color.Gray, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
