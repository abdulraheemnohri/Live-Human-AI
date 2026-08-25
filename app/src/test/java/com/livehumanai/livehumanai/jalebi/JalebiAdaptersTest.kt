package com.livehumanai.livehumanai.jalebi

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class JalebiAdaptersTest {
    @Test
    fun cameraMarksOnlyMeaningfulSceneChanges() {
        val adapter = JalebiCameraAdapter()
        val first = adapter.submit(1L, "desk-document", listOf("document"))
        val same = adapter.submit(2L, "desk-document", listOf("document"))
        val changed = adapter.submit(3L, "desk-phone", listOf("phone"))
        assertTrue(first.sceneChanged)
        assertFalse(same.sceneChanged)
        assertTrue(changed.sceneChanged)
    }

    @Test
    fun audioAdapterRejectsPartialOrBlankSpeech() {
        val adapter = JalebiAudioAdapter()
        assertTrue(adapter.submitTranscript("hello", .9f, final = true) != null)
        assertTrue(adapter.submitTranscript("hello", .9f, final = false) == null)
        assertTrue(adapter.submitTranscript("   ", .9f, final = true) == null)
    }
}
