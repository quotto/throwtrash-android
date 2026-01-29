package net.mythrowaway.app.module.theme.infra

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import net.mythrowaway.app.stub.StubSharedPreferencesImpl
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class PreferenceThemeRepositoryImplTest {
  @Mock
  private lateinit var mockContext: Context

  @InjectMocks
  private lateinit var instance: PreferenceThemeRepositoryImpl

  private val stubSharedPreference = StubSharedPreferencesImpl()

  private lateinit var mockedStatic: MockedStatic<PreferenceManager>

  @BeforeEach
  fun beforeEach() {
    MockitoAnnotations.openMocks(this)
    mockedStatic = Mockito.mockStatic(PreferenceManager::class.java)
    mockedStatic.`when`<SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(mockContext) }
      .thenReturn(stubSharedPreference)
    stubSharedPreference.removeAll()
  }

  @AfterEach
  fun afterEach() {
    mockedStatic.close()
  }

  @Test
  fun isDarkModeEnabled_DefaultFalse() {
    assertFalse(instance.isDarkModeEnabled())
  }

  @Test
  fun saveDarkModeEnabled_Enable() {
    instance.saveDarkModeEnabled(true)

    assertTrue(instance.isDarkModeEnabled())
  }

  @Test
  fun saveDarkModeEnabled_Disable() {
    instance.saveDarkModeEnabled(true)
    instance.saveDarkModeEnabled(false)

    assertFalse(instance.isDarkModeEnabled())
  }
}
