package net.mythrowaway.app.domain.trash.entity.sync

sealed class SyncState(val value: Int) {
    companion object {
        fun from(value: Int): SyncState {
            return when (value) {
                0 -> NotInit
                1 -> Wait
                2 -> Synced
                else -> throw IllegalArgumentException("Unknown SyncState value: $value")
            }
        }
    }
    object NotInit: SyncState(0)
    object Wait : SyncState(1)
    object Synced : SyncState(2)
}