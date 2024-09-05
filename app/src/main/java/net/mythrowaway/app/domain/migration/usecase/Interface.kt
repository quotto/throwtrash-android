package net.mythrowaway.app.domain.migration.usecase

interface VersionRepositoryInterface {
    fun getConfigVersion(): Int
    fun updateConfigVersion(version: Int)
}

interface MigrationRepositoryInterface {
    fun getStringValue(key: String, defaultValue: String): String
    fun getIntValue(key: String, defaultValue: Int): Int
    fun getLongValue(key: String, defaultValue: Long): Long

    fun getBooleanValue(key: String, defaultValue: Boolean): Boolean
}
