package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EduVoteDao {
    // Students
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<Student>)

    @Query("SELECT * FROM students ORDER BY name ASC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE classNum = :classNum AND section = :section ORDER BY name ASC")
    fun getStudentsByClassAndSection(classNum: String, section: String): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE rollNumber = :rollNumber AND name COLLATE NOCASE = :name LIMIT 1")
    suspend fun getStudentByRollAndName(rollNumber: String, name: String): Student?
    
    @Query("UPDATE students SET hasVotedInCurrentEvent = :hasVoted WHERE id = :studentId")
    suspend fun updateStudentVoteStatus(studentId: String, hasVoted: Boolean)

    // Elections
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElectionEvent(event: ElectionEvent)
    
    @Update
    suspend fun updateElectionEvent(event: ElectionEvent)

    @Delete
    suspend fun deleteElectionEvent(event: ElectionEvent)

    @Query("SELECT * FROM election_events ORDER BY startDateMillis DESC")
    fun getAllElections(): Flow<List<ElectionEvent>>
    
    @Query("SELECT * FROM election_events WHERE id = :id LIMIT 1")
    fun getElectionById(id: String): Flow<ElectionEvent?>
    
    @Query("SELECT * FROM election_events WHERE isCompleted = 0")
    fun getOngoingElections(): Flow<List<ElectionEvent>>

    // Candidates
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCandidate(candidate: Candidate)
    
    @Query("SELECT * FROM candidates WHERE electionId = :electionId ORDER BY voteCount DESC")
    fun getCandidatesForElection(electionId: String): Flow<List<Candidate>>
    
    @Query("SELECT c.*, s.* FROM candidates c INNER JOIN students s ON c.studentId = s.id WHERE c.electionId = :electionId ORDER BY c.voteCount DESC")
    fun getCandidatesWithStudentsForElection(electionId: String): Flow<Map<Candidate, Student>>

    // Voting Transaction
    @Insert
    suspend fun insertVoteLog(log: VoteLog)

    @Query("UPDATE candidates SET voteCount = voteCount + 1 WHERE id = :candidateId")
    suspend fun incrementVoteCount(candidateId: String)

    @Transaction
    suspend fun castVoteGuest(candidateId: String, electionId: String, voteHash: String) {
        incrementVoteCount(candidateId)
        insertVoteLog(VoteLog(electionId = electionId, hashVerification = voteHash))
    }
}
