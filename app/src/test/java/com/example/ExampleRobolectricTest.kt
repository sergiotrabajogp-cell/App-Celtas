package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.celtas.data.CelticAdventureRepository
import com.example.celtas.model.ExplorerRank
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Aventura Celta", appName)
  }

  @Test
  fun `repository loads initial data for child explorer`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = CelticAdventureRepository(context)

    val profile = repo.profile.value
    assertNotNull(profile)
    assertEquals("Arturo", profile.name)
    assertEquals(ExplorerRank.NOVATO, profile.rank)

    val chapters = repo.chapters.value
    assertEquals(5, chapters.size)
    assertTrue(chapters[0].isUnlocked)

    val locations = repo.mapLocations.value
    assertEquals(6, locations.size)

    val relics = repo.relics.value
    assertEquals(6, relics.size)

    val trophies = repo.trophies.value
    assertEquals(8, trophies.size)

    val achievements = repo.achievements.value
    assertEquals(8, achievements.size)
  }
}
