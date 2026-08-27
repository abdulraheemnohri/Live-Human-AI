package com.livehumanai.livehumanai.data

import com.livehumanai.livehumanai.data.database.entity.MemoryEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date

class MemoryEntityTest {

    @Test
    fun testMemoryEntityCreation() {
        val memory = MemoryEntity(
            id = 1L,
            content = "User prefers Urdu and English",
            title = "User Preferences",
            type = MemoryEntity.MemoryType.PREFERENCE,
            createdAt = Date(),
            updatedAt = Date(),
            isImportant = true,
            tags = listOf("language", "preferences")
        )

        assertEquals(1L, memory.id)
        assertEquals("User Preferences", memory.title)
        assertEquals(MemoryEntity.MemoryType.PREFERENCE, memory.type)
        assertTrue(memory.content.contains("Urdu"))
        assertTrue(memory.isImportant)
        assertEquals(2, memory.tags.size)
    }
}
