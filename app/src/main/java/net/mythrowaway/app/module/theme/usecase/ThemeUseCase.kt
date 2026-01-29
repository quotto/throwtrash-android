package net.mythrowaway.app.module.theme.usecase

import androidx.appcompat.app.AppCompatDelegate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeUseCase @Inject constructor(
  private val themeRepository: ThemeRepositoryInterface,
) {
  fun isDarkModeEnabled(): Boolean {
    return themeRepository.isDarkModeEnabled()
  }

  fun saveDarkModeEnabled(enabled: Boolean) {
    themeRepository.saveDarkModeEnabled(enabled)
  }

  fun applyTheme(enabled: Boolean) {
    val mode = if (enabled) {
      AppCompatDelegate.MODE_NIGHT_YES
    } else {
      AppCompatDelegate.MODE_NIGHT_NO
    }
    AppCompatDelegate.setDefaultNightMode(mode)
  }

  fun updateTheme(enabled: Boolean) {
    saveDarkModeEnabled(enabled)
    applyTheme(enabled)
  }

  fun applySavedTheme() {
    applyTheme(isDarkModeEnabled())
  }
}
