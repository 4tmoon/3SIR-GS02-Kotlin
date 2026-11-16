package com.github.atmoon.zeloapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.atmoon.zeloapp.data.model.Comment
import com.github.atmoon.zeloapp.data.model.Issue

@Database(entities = [Issue::class, Comment::class], version = 1, exportSchema = false)
abstract class ZeloDatabase : RoomDatabase() {
    abstract fun issueDao(): IssueDao

    companion object {
        @Volatile
        private var INSTANCE: ZeloDatabase? = null

        fun getInstance(context: Context): ZeloDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ZeloDatabase::class.java,
                    "zelo_database.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}