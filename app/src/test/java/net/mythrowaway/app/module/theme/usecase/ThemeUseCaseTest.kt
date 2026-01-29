package net.mythrowaway.app.module.theme.usecase

import androidx.appcompat.app.AppCompatDelegate
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class ThemeUseCaseTest {
  @Mock
  private lateinit var themeRepository: ThemeRepositoryInterface

  @InjectMocks
  private lateinit var themeUseCase: ThemeUseCase

  private lateinit var mockedStatic: MockedStatic<AppCompatDelegate>

  @BeforeEach
  fun beforeEach() {
    MockitoAnnotations.openMocks(this)
    mockedStatic = Mockito.mockStatic(AppCompatDelegate::class.java)
  }

  @AfterEach
  fun afterEach() {
    mockedStatic.close()
  }

  @Test
  fun applySavedTheme_Light() {
    Mockito.`when`(themeRepository.isDarkModeEnabled()).thenReturn(false)

    themeUseCase.applySavedTheme()

    mockedStatic.verify {
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
  }

  @Test
  fun updateTheme_Dark() {
    themeUseCase.updateTheme(true)

    Mockito.verify(themeRepository).saveDarkModeEnabled(true)
    mockedStatic.verify {
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
  }
}
