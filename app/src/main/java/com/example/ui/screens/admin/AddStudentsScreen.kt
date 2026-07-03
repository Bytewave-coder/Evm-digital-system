package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ElectionEvent
import com.example.ui.MainViewModel
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStudentsScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val allElections by viewModel.allElections.collectAsState()
    
    var selectedElection by remember { mutableStateOf<ElectionEvent?>(null) }
    var electionExpanded by remember { mutableStateOf(false) }
    
    var numStudents by remember { mutableStateOf("") }
    
    var studentsAdded by remember { mutableStateOf(0) }
    var targetStudents by remember { mutableStateOf(0) }
    
    // States for current student being entered
    var currentName by remember { mutableStateOf("") }
    var currentRollNo by remember { mutableStateOf("") }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Add Students", color = Color.White) },
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
            if (targetStudents == 0) {
                // Phase 1: Configuration
                ExposedDropdownMenuBox(
                    expanded = electionExpanded,
                    onExpandedChange = { electionExpanded = !electionExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedElection?.title ?: "Select Session",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Vote Session", color = Color.Gray) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = electionExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = electionExpanded,
                        onDismissRequest = { electionExpanded = false },
                        modifier = Modifier.background(DarkBackground)
                    ) {
                        allElections.forEach { election ->
                            DropdownMenuItem(
                                text = { Text(election.title, color = Color.White) },
                                onClick = {
                                    selectedElection = election
                                    electionExpanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = numStudents,
                    onValueChange = { numStudents = it },
                    label = { Text("Number of students voting right now", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = {
                        val parsed = numStudents.toIntOrNull()
                        if (selectedElection != null && parsed != null && parsed > 0) {
                            targetStudents = parsed
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Start Adding Students", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            } else if (studentsAdded < targetStudents) {
                // Phase 2: Input Students
                Text("Student ${studentsAdded + 1} of $targetStudents", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = currentName,
                    onValueChange = { currentName = it },
                    label = { Text("Voter Name", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = currentRollNo,
                    onValueChange = { currentRollNo = it },
                    label = { Text("Roll Number", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = {
                        if (currentName.isNotBlank() && currentRollNo.isNotBlank() && selectedElection != null) {
                            viewModel.addStudent(
                                name = currentName,
                                rollNumber = currentRollNo,
                                classNum = selectedElection!!.classTarget,
                                section = selectedElection!!.sectionTarget
                            )
                            studentsAdded++
                            currentName = ""
                            currentRollNo = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Save & Next", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("All students added successfully!", color = Color.White, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)) {
                            Text("Finish", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
