package com.example.qaphela.data.repository

import com.example.qaphela.data.local.BlocklistDao
import com.example.qaphela.data.local.BlocklistEntity
import kotlinx.coroutines.flow.Flow

class BlocklistRepository(private val blocklistDao: BlocklistDao) {

    fun getAllBlocklistItems(): Flow<List<BlocklistEntity>> = blocklistDao.getAllBlocklistItems()

    fun searchBlocklist(query: String): Flow<List<BlocklistEntity>> {
        return if (query.isBlank()) {
            blocklistDao.getAllBlocklistItems()
        } else {
            blocklistDao.searchBlocklist(query.trim())
        }
    }
}
