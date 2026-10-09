package com.pagenest.pdf.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pagenest.pdf.data.local.dao.CustomThemeDao
import com.pagenest.pdf.data.local.dao.DocumentDao
import com.pagenest.pdf.data.local.dao.ReadingProgressDao
import com.pagenest.pdf.data.local.entity.CustomThemeEntity
import com.pagenest.pdf.data.local.entity.DocumentEntity
import com.pagenest.pdf.data.local.entity.ReadingProgressEntity

@Database(
    entities = [
        DocumentEntity::class,
        ReadingProgressEntity::class,
        CustomThemeEntity::class,
        com.pagenest.pdf.data.local.entity.BookmarkEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao
    abstract fun readingProgressDao(): ReadingProgressDao
    abstract fun customThemeDao(): CustomThemeDao
    abstract fun bookmarkDao(): com.pagenest.pdf.data.local.dao.BookmarkDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pagenest.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
