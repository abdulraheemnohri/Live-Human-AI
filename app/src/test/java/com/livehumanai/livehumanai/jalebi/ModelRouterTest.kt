package com.livehumanai.livehumanai.jalebi

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelRouterTest {

    @Test
    fun testLiteProfileSelection() {
        val totalRamGb = 6
        val profile = if (totalRamGb <= 6) "Lite" else if (totalRamGb <= 12) "Standard" else "Pro"
        assertEquals("Lite", profile)
    }

    @Test
    fun testProProfileSelection() {
        val totalRamGb = 16
        val profile = if (totalRamGb <= 6) "Lite" else if (totalRamGb <= 12) "Standard" else "Pro"
        assertEquals("Pro", profile)
    }
}
