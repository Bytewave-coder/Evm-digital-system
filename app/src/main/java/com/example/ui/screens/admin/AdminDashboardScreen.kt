package com.example.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.CircleShape
import com.example.data.ElectionEvent
import com.example.ui.MainViewModel
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CardBackground
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.ElevatedBackground
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueLight
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: MainViewModel,
    onCreateElection: () -> Unit,
    onAddCandidates: () -> Unit,
    onAddStudents: () -> Unit,
    onViewResults: (String) -> Unit,
    onVoterMode: () -> Unit
) {
    var currentTab by remember { mutableStateOf("home") }
    val ongoingElections by viewModel.ongoingElections.collectAsState()
    val allElections by viewModel.allElections.collectAsState()

    var fabExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("https://api.dicebear.com/9.x/avataaars/png?seed=Admin")
                                .crossfade(true)
                                .build(),
                            contentDescription = "Admin Profile",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.DarkGray)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("EduVote Pro", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 1.sp)
                            Text("Admin Dashboard", fontSize = 12.sp, color = PrimaryBlueLight, fontWeight = FontWeight.Medium)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { /* Do nothing */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                if (fabExpanded) {
                    ExtendedFloatingActionButton(
                        onClick = { 
                            fabExpanded = false
                            onAddStudents()
                        },
                        containerColor = ElevatedBackground,
                        contentColor = Color.White,
                        icon = { Icon(Icons.Default.People, contentDescription = "Add Students") },
                        text = { Text("Add Students") },
                        modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
                    )
                    ExtendedFloatingActionButton(
                        onClick = { 
                            fabExpanded = false
                            onAddCandidates()
                        },
                        containerColor = ElevatedBackground,
                        contentColor = Color.White,
                        icon = { Icon(Icons.Default.Star, contentDescription = "Add Candidates") },
                        text = { Text("Add Candidates") },
                        modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
                    )
                    ExtendedFloatingActionButton(
                        onClick = { 
                            fabExpanded = false
                            onCreateElection()
                        },
                        containerColor = ElevatedBackground,
                        contentColor = Color.White,
                        icon = { Icon(Icons.Default.HowToVote, contentDescription = "Create Election") },
                        text = { Text("Create Session") },
                        modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
                    )
                }
                FloatingActionButton(
                    onClick = { fabExpanded = !fabExpanded },
                    containerColor = PrimaryBlue,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
                ) {
                    Icon(if (fabExpanded) Icons.Default.Add else Icons.Default.Add, contentDescription = "Options")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = {
            NavigationBar(
                containerColor = DarkBackground,
                contentColor = TextSecondary,
                modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            ) {
                NavigationBarItem(
                    selected = currentTab == "home",
                    onClick = { currentTab = "home" },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryBlue, selectedTextColor = PrimaryBlue, unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray, indicatorColor = Color.Transparent)
                )
                NavigationBarItem(
                    selected = currentTab == "elections",
                    onClick = { currentTab = "elections" },
                    icon = { Icon(Icons.Default.List, contentDescription = "Elections") },
                    label = { Text("Elections") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryBlue, selectedTextColor = PrimaryBlue, unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray, indicatorColor = Color.Transparent)
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onVoterMode() },
                    icon = { Icon(Icons.Default.People, contentDescription = "Voters") },
                    label = { Text("Voters") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryBlue, selectedTextColor = PrimaryBlue, unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray, indicatorColor = Color.Transparent)
                )
                NavigationBarItem(
                    selected = currentTab == "results",
                    onClick = { currentTab = "results" },
                    icon = { Icon(Icons.Default.Poll, contentDescription = "Results") },
                    label = { Text("Results") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryBlue, selectedTextColor = PrimaryBlue, unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray, indicatorColor = Color.Transparent)
                )
            }
        }
    ) { padding ->
        if (currentTab == "home" || currentTab == "results") {
            LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(
                        label = "Total Voters",
                        value = "250",
                        icon = Icons.Default.People,
                        iconTint = PrimaryBlueLight,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Total Elections",
                        value = allElections.size.toString(),
                        icon = Icons.Default.HowToVote,
                        iconTint = SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(
                        label = "Ongoing",
                        value = ongoingElections.size.toString(),
                        icon = Icons.Default.PlayCircle,
                        iconTint = Color(0xFFF59E0B),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Completed",
                        value = allElections.count { it.isCompleted }.toString(),
                        icon = Icons.Default.CheckCircle,
                        iconTint = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                
                Text("Ongoing Election", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (ongoingElections.isEmpty()) {
                item {
                    Text("No ongoing elections", color = Color.Gray, modifier = Modifier.padding(16.dp))
                }
            } else {
                items(ongoingElections) { election ->
                    ElectionCard(election = election, onClick = { onViewResults(election.id) })
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Recent Elections", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            val recentElections = allElections.filter { it.isCompleted }
            if (recentElections.isEmpty()) {
                item {
                    Text("No recent elections", color = Color.Gray, modifier = Modifier.padding(16.dp))
                }
            } else {
                items(recentElections) { election ->
                    ElectionCard(election = election, onClick = { onViewResults(election.id) })
                }
            }
            
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
        } else if (currentTab == "elections") {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Manage Elections", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                if (allElections.isEmpty()) {
                    item {
                        Text("No elections created yet.", color = Color.Gray, modifier = Modifier.padding(16.dp))
                    }
                } else {
                    items(allElections) { election ->
                        ManageElectionCard(
                            election = election,
                            viewModel = viewModel,
                            onViewResults = { onViewResults(election.id) }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, iconTint: Color, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, BorderColor),
        modifier = modifier.height(100.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(label, color = TextSecondary, fontSize = 11.sp, letterSpacing = 1.sp)
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun ManageElectionCard(election: ElectionEvent, viewModel: MainViewModel, onViewResults: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editTitle by remember { mutableStateOf(election.title) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Election") },
            text = { Text("Are you sure you want to delete this election?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteElection(election)
                    showDeleteDialog = false
                }) { Text("Delete", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel", color = Color.White) }
            },
            containerColor = CardBackground,
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }
    
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Election Name") },
            text = {
                OutlinedTextField(
                    value = editTitle,
                    onValueChange = { editTitle = it },
                    label = { Text("Election Title") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (editTitle.isNotBlank()) {
                        viewModel.updateElectionTitle(election, editTitle)
                        showEditDialog = false
                    }
                }) { Text("Save", color = PrimaryBlue) }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Cancel", color = Color.White) }
            },
            containerColor = CardBackground,
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = ElevatedBackground),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, if (election.isImportant) Color(0xFFFFD700).copy(alpha = 0.5f) else PrimaryBlue.copy(alpha = 0.2f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (election.isCompleted) {
                    Text("COMPLETED", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                } else {
                    Text("LIVE NOW", color = PrimaryBlueLight, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
                
                IconButton(onClick = { viewModel.toggleElectionImportance(election, !election.isImportant) }, modifier = Modifier.size(24.dp)) {
                    Icon(
                        if (election.isImportant) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Important",
                        tint = if (election.isImportant) Color(0xFFFFD700) else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(election.title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                IconButton(onClick = { editTitle = election.title; showEditDialog = true }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Class: ${election.classTarget} (Sec ${election.sectionTarget})", color = TextSecondary, fontSize = 12.sp)
            
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                if (!election.isCompleted) {
                    Button(
                        onClick = { viewModel.markElectionComplete(election) },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen.copy(alpha = 0.2f), contentColor = SuccessGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).padding(end = 4.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Complete", fontSize = 12.sp)
                    }
                }
                Button(
                    onClick = onViewResults,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue.copy(alpha = 0.2f), contentColor = PrimaryBlueLight),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                ) {
                    Icon(Icons.Default.Poll, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Results", fontSize = 12.sp)
                }
                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.2f), contentColor = Color(0xFFFF6B6B)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun ElectionCard(election: ElectionEvent, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = ElevatedBackground),
        shape = RoundedCornerShape(32.dp),
        border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.2f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (election.isCompleted) {
                    Text("COMPLETED", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                } else {
                    Text("LIVE NOW", color = PrimaryBlueLight, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(election.title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Target Class: ${election.classTarget} (Sec ${election.sectionTarget})", color = TextSecondary, fontSize = 12.sp)
        }
    }
}
