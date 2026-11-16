package com.github.atmoon.zeloapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.atmoon.zeloapp.data.model.Comment
import com.github.atmoon.zeloapp.data.model.Issue
import kotlinx.coroutines.flow.Flow

@Dao
interface IssueDao {
    @Query("SELECT * FROM issues ORDER BY createdAt DESC")
    fun observeAllIssues(): Flow<List<Issue>>

    @Query("SELECT COUNT(*) FROM issues")
    suspend fun countIssues(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIssue(issue: Issue)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllIssues(issues: List<Issue>)

    @Update
    suspend fun updateIssue(issue: Issue)

    @Delete
    suspend fun deleteIssue(issue: Issue)

    @Query("SELECT * FROM comments WHERE issueId = :issueId ORDER BY createdAt DESC")
    fun observeComments(issueId: String): Flow<List<Comment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: Comment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllComments(comments: List<Comment>)
}