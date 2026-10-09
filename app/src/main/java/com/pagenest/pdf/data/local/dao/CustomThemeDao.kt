package com.pagenest.pdf.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pagenest.pdf.data.local.entity.CustomThemeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomThemeDao {
    @Query("SELECT * FROM custom_themes ORDER BY createdAt DESC")
    fun getAllCustomThemes(): Flow<List<CustomThemeEntity>>

    @Query("SELECT * FROM custom_themes WHERE id = :id LIMIT 1")
    suspend fun getThemeById(id: String): CustomThemeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTheme(theme: CustomThemeEntity)

    @Query("DELETE FROM custom_themes WHERE id = :id")
    suspend fun deleteTheme(id: String)
}
