package net.mythrowaway.app.domain.migration.usecase

interface VersionRepositoryInterface {
    fun getConfigVersion(): Int
    fun updateConfigVersion(version: Int)
}
