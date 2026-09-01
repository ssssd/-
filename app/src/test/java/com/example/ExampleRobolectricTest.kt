package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.core.localization.AppLanguage
import com.example.core.localization.LocaleManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("FocusFlow", appName)
  }

  @Test
  fun `verify multi-language translations exist`() {
    AppLanguage.entries.forEach { lang ->
      val strings = LocaleManager.getStrings(lang)
      assertNotNull(strings.appName)
      assertNotNull(strings.startFocus)
      assertNotNull(strings.settingsTitle)
    }
  }
}

