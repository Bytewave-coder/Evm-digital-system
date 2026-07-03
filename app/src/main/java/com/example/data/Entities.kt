package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "students",
    indices = [Index(value = ["rollNumber", "classNum", "section"], unique = true)]
)
data class Student(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val rollNumber: String,
    val classNum: String,
    val section: String,
    val admissionNumber: String,
    val photoUri: String?,
    val hasVotedInCurrentEvent: Boolean = false
)

@Entity(tableName = "election_events")
data class ElectionEvent(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val classTarget: String,
    val sectionTarget: String,
    val electionType: String,
    val candidateLimit: Int,
    val startDateMillis: Long,
    val endDateMillis: Long,
    val description: String = "",
    val isLocked: Boolean = false,
    val isCompleted: Boolean = false,
    val isImportant: Boolean = false,
    val winnerId: String? = null
)

@Entity(
    tableName = "candidates",
    foreignKeys = [
        ForeignKey(entity = ElectionEvent::class, parentColumns = ["id"], childColumns = ["electionId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Student::class, parentColumns = ["id"], childColumns = ["studentId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("electionId"), Index("studentId")]
)
data class Candidate(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val electionId: String,
    val studentId: String,
    val partyName: String,
    val partySymbolUri: String?,
    val manifesto: String,
    val voteCount: Int = 0
)

@Entity(
    tableName = "vote_logs",
    foreignKeys = [
        ForeignKey(entity = ElectionEvent::class, parentColumns = ["id"], childColumns = ["electionId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("electionId")]
)
data class VoteLog(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val electionId: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val hashVerification: String
)

data class CandidateWithStudent(
    val candidate: Candidate,
    val student: Student
)
