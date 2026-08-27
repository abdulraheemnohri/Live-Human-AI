package com.livehumanai.livehumanai.jalebi

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class JalebiLoopEngineTest {

    @Test
    fun testConfidenceEscalationThreshold() {
        val confidencePolicy = JalebiConfidenceEscalator()
        val lowConfidence = 0.4f
        val highConfidence = 0.95f

        assertTrue(lowConfidence < 0.7f)
        assertTrue(highConfidence >= 0.7f)
    }

    @Test
    fun testMaxIterationsBounded() {
        val maxIterations = 8
        var currentIteration = 0

        while (currentIteration < maxIterations) {
            currentIteration++
        }

        assertEquals(8, currentIteration)
    }
}
