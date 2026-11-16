package com.github.atmoon.zeloapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey val id: String,
    val issueId: String,
    val author: String,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)