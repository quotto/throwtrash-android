package net.mythrowaway.app.module.theme.usecase

interface ThemeRepositoryInterface {
  fun isDarkModeEnabled(): Boolean
  fun saveDarkModeEnabled(enabled: Boolean)
}
