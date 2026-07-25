package com.auraplayer.app.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class SettingsPreferencesTest {

    @get:Rule
    val tmpFolder = TemporaryFolder()

    @Test
    fun setHomeRailOrderAndHiddenRails_persistsCorrectly() = runTest {
        val testFile = tmpFolder.newFile("test_settings.preferences_pb")
        val dataStore = PreferenceDataStoreFactory.create(
            produceFile = { testFile }
        )
        val prefs = SettingsPreferences(dataStore)

        val newOrder = listOf("MOST_PLAYED_ARTISTS", "MADE_FOR_YOU", "CONTINUE_LISTENING")
        prefs.setHomeRailOrder(newOrder)

        val hidden = setOf("ON_REPEAT")
        prefs.setHiddenRails(hidden)

        val settings = prefs.settingsFlow.first()
        assertEquals(newOrder, settings.homeRailOrder)
        assertTrue(settings.hiddenRails.contains("ON_REPEAT"))
    }
}
