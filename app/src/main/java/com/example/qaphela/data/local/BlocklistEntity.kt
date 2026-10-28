package com.example.qaphela.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocklist_items")
data class BlocklistEntity(
    @PrimaryKey(autoGenerate = true)
    val blocklistId: Long = 0,
    val indicator: String,
    val indicatorType: String, // "URL", "PHONE", "DOMAIN", "KEYWORD"
    val reason: String,
    val source: String,
    val lastUpdated: Long = System.currentTimeMillis()
)
