package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateElectionScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var classTarget by remember { mutableStateOf("10") }
    var classExpanded by remember { mutableStateOf(false) }
    val classOptions = listOf("6", "7", "8", "9", "10", "11", "12")
    var sectionTarget by remember { mutableStateOf("A") }
    var sectionExpanded by remember { mutableStateOf(false) }
    val sectionOptions = listOf("A", "B", "C", "D", "E")
    var candidateLimit by remember { mutableStateOf("3") }
    var electionType by remember { mutableStateOf("Class Representative") }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Create New Election", color = Color.White) },
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
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Election Title", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Title, contentDescription = "Title", tint = PrimaryBlue) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color.DarkGray
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ExposedDropdownMenuBox(
                    expanded = classExpanded,
                    onExpandedChange = { classExpanded = !classExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = classTarget,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Class", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Class, contentDescription = "Class", tint = PrimaryBlue) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color.DarkGray
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = classExpanded,
                        onDismissRequest = { classExpanded = false },
                        modifier = Modifier.background(DarkBackground)
                    ) {
                        classOptions.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption, color = Color.White) },
                                onClick = {
                                    classTarget = selectionOption
                                    classExpanded = false
                                },
                                modifier = Modifier.background(DarkBackground)
                            )
                        }
                    }
                }
                
                ExposedDropdownMenuBox(
                    expanded = sectionExpanded,
                    onExpandedChange = { sectionExpanded = !sectionExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = sectionTarget,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Section", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.MeetingRoom, contentDescription = "Section", tint = PrimaryBlue) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sectionExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color.DarkGray
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = sectionExpanded,
                        onDismissRequest = { sectionExpanded = false },
                        modifier = Modifier.background(DarkBackground)
                    ) {
                        sectionOptions.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption, color = Color.White) },
                                onClick = {
                                    sectionTarget = selectionOption
                                    sectionExpanded = false
                                },
                                modifier = Modifier.background(DarkBackground)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = electionType,
                onValueChange = { electionType = it },
                label = { Text("Election Type", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.HowToVote, contentDescription = "Election Type", tint = PrimaryBlue) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color.DarkGray
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        viewModel.createElection(
                            title = title,
                            classTarget = classTarget,
                            sectionTarget = sectionTarget,
                            candidateLimit = candidateLimit.toIntOrNull() ?: 3,
                            type = electionType
                        )
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Election", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
