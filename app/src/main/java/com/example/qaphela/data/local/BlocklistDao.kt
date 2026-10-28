package com.example.qaphela.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BlocklistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<BlocklistEntity>): List<Long>

    @Query("SELECT * FROM blocklist_items ORDER BY lastUpdated DESC")
    fun getAllBlocklistItems(): Flow<List<BlocklistEntity>>

    @Query("SELECT * FROM blocklist_items WHERE indicator LIKE '%' || :query || '%' OR reason LIKE '%' || :query || '%'")
    fun searchBlocklist(query: String): Flow<List<BlocklistEntity>>

    @Query("SELECT * FROM blocklist_items WHERE :input LIKE '%' || indicator || '%' OR indicator LIKE '%' || :input || '%'")
    suspend fun findMatchingBlocklistItems(input: String): List<BlocklistEntity>

    @Query("SELECT COUNT(*) FROM blocklist_items")
    suspend fun getCount(): Int
}
