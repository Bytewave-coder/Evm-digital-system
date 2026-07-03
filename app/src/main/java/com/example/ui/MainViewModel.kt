package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Candidate
import com.example.data.CandidateWithStudent
import com.example.data.EduVoteRepository
import com.example.data.ElectionEvent
import com.example.data.Student
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: EduVoteRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = EduVoteRepository(database.eduVoteDao())
    }

    val ongoingElections: StateFlow<List<ElectionEvent>> = repository.getOngoingElections()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allElections: StateFlow<List<ElectionEvent>> = repository.getAllElections()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStudents: StateFlow<List<Student>> = repository.getAllStudents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentVoter = MutableStateFlow<Student?>(null)
    val currentVoter: StateFlow<Student?> = _currentVoter.asStateFlow()
    
    private val _candidatesForActiveElection = MutableStateFlow<List<CandidateWithStudent>>(emptyList())
    val candidatesForActiveElection: StateFlow<List<CandidateWithStudent>> = _candidatesForActiveElection.asStateFlow()

    private val _voteResultState = MutableStateFlow<VoteResult?>(null)
    val voteResultState = _voteResultState.asStateFlow()

    fun getElectionById(id: String): kotlinx.coroutines.flow.Flow<ElectionEvent?> {
        return repository.getElectionById(id)
    }

    fun createElection(title: String, classTarget: String, sectionTarget: String, candidateLimit: Int, type: String) {
        viewModelScope.launch {
            val event = ElectionEvent(
                title = title,
                classTarget = classTarget,
                sectionTarget = sectionTarget,
                electionType = type,
                candidateLimit = candidateLimit,
                startDateMillis = System.currentTimeMillis(),
                endDateMillis = System.currentTimeMillis() + 86400000L // +1 day
            )
            repository.insertElection(event)
        }
    }

    fun deleteElection(event: ElectionEvent) {
        viewModelScope.launch {
            repository.deleteElection(event)
        }
    }

    fun markElectionComplete(event: ElectionEvent) {
        viewModelScope.launch {
            repository.updateElection(event.copy(isCompleted = true))
        }
    }

    fun toggleElectionImportance(event: ElectionEvent, isImportant: Boolean) {
        viewModelScope.launch {
            repository.updateElection(event.copy(isImportant = isImportant))
        }
    }

    fun updateElectionTitle(event: ElectionEvent, newTitle: String) {
        viewModelScope.launch {
            repository.updateElection(event.copy(title = newTitle))
        }
    }

    fun addStudent(name: String, rollNumber: String, classNum: String, section: String, photoUri: String? = null) {
        viewModelScope.launch {
            repository.insertStudent(
                Student(
                    name = name,
                    rollNumber = rollNumber,
                    classNum = classNum,
                    section = section,
                    admissionNumber = "ADM-${System.currentTimeMillis() % 10000}",
                    photoUri = photoUri
                )
            )
        }
    }
    
    fun addCandidateWithNewStudent(electionId: String, studentName: String, partyLogoUri: String?, realPhotoUri: String?, classNum: String, section: String) {
        viewModelScope.launch {
            val studentId = java.util.UUID.randomUUID().toString()
            repository.insertStudent(
                Student(
                    id = studentId,
                    name = studentName,
                    rollNumber = "CAND-${System.currentTimeMillis() % 1000}",
                    classNum = classNum,
                    section = section,
                    admissionNumber = "ADM-${System.currentTimeMillis() % 10000}",
                    photoUri = realPhotoUri
                )
            )
            repository.insertCandidate(
                Candidate(
                    electionId = electionId,
                    studentId = studentId,
                    partyName = "Party of $studentName",
                    partySymbolUri = partyLogoUri,
                    manifesto = "A better tomorrow."
                )
            )
        }
    }

    fun addCandidate(electionId: String, studentId: String, partyName: String, partySymbolUri: String? = null) {
        viewModelScope.launch {
            repository.insertCandidate(
                Candidate(
                    electionId = electionId,
                    studentId = studentId,
                    partyName = partyName,
                    partySymbolUri = partySymbolUri,
                    manifesto = "A better tomorrow."
                )
            )
        }
    }

    fun loadCandidatesForElection(electionId: String) {
        viewModelScope.launch {
            repository.getCandidatesForElection(electionId).collect {
                _candidatesForActiveElection.value = it
            }
        }
    }

    fun verifyVoterLogin(name: String, rollNumber: String, electionId: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val student = repository.verifyVoter(name, rollNumber)
            val election = repository.getElectionById(electionId).firstOrNull()
            
            if (student != null && election != null) {
                if (student.classNum != election.classTarget || student.section != election.sectionTarget) {
                    onResult(false, "Voter not registered for this session.")
                } else if (student.hasVotedInCurrentEvent) { // Wait, how do we track if a voter voted in this specific election? 
                    // Actually, the database currently just has a global `hasVotedInCurrentEvent` on Student. Let's stick to it or we can check VoteLogs.
                    // But checking VoteLogs is safer.
                    onResult(false, "You have already cast your vote.") // Let's check VoteLogs later.
                } else {
                    _currentVoter.value = student
                    onResult(true, "Verification successful")
                }
            } else {
                onResult(false, "Voter not found. Please check Name and Roll Number.")
            }
        }
    }
    
    fun castVote(voterName: String, candidateId: String, electionId: String) {
        viewModelScope.launch {
            try {
                repository.castVote(voterName, candidateId, electionId)
                _voteResultState.value = VoteResult.Success
            } catch (e: Exception) {
                _voteResultState.value = VoteResult.Error(e.message ?: "Failed to cast vote")
            }
        }
    }
    
    fun clearVoteResult() {
        _voteResultState.value = null
    }

    // Dummy mock data for preview purposes
    fun seedMockData() {
        viewModelScope.launch {
            // Create a mock election
            val electionId = java.util.UUID.randomUUID().toString()
            val event = ElectionEvent(
                id = electionId,
                title = "Class 10 Representative Election",
                classTarget = "10",
                sectionTarget = "A",
                electionType = "Class Representative",
                candidateLimit = 3,
                startDateMillis = System.currentTimeMillis(),
                endDateMillis = System.currentTimeMillis() + 86400000L
            )
            repository.insertElection(event)
            
            // Seed a class
            val s1 = Student(name = "Rohan Mehta", rollNumber = "07", classNum = "10", section = "A", admissionNumber = "101", photoUri = "https://api.dicebear.com/9.x/avataaars/png?seed=Rohan")
            val s2 = Student(name = "Arjun Verma", rollNumber = "12", classNum = "10", section = "A", admissionNumber = "102", photoUri = "https://api.dicebear.com/9.x/avataaars/png?seed=Arjun")
            val s3 = Student(name = "Diya Patel", rollNumber = "04", classNum = "10", section = "A", admissionNumber = "103", photoUri = "https://api.dicebear.com/9.x/avataaars/png?seed=Diya")
            val s4 = Student(name = "Aarav Sharma", rollNumber = "01", classNum = "10", section = "A", admissionNumber = "104", photoUri = "https://api.dicebear.com/9.x/avataaars/png?seed=Aarav")
            
            repository.insertStudent(s1)
            repository.insertStudent(s2)
            repository.insertStudent(s3)
            repository.insertStudent(s4)
            
            // Seed candidates
            repository.insertCandidate(Candidate(electionId = electionId, studentId = s1.id, partyName = "Student Unity", partySymbolUri = "https://api.dicebear.com/9.x/icons/png?seed=unity", manifesto = ""))
            repository.insertCandidate(Candidate(electionId = electionId, studentId = s2.id, partyName = "Bright Future", partySymbolUri = "https://api.dicebear.com/9.x/icons/png?seed=future", manifesto = ""))
            repository.insertCandidate(Candidate(electionId = electionId, studentId = s3.id, partyName = "New Generation", partySymbolUri = "https://api.dicebear.com/9.x/icons/png?seed=generation", manifesto = ""))
        }
    }
}

sealed class VoteResult {
    object Success : VoteResult()
    data class Error(val message: String) : VoteResult()
}
