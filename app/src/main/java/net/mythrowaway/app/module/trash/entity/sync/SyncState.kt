package net.mythrowaway.app.module.trash.entity.sync

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
    data object NotInit: SyncState(0)
    data object Wait : SyncState(1)
    data object Synced : SyncState(2)

    override fun toString(): String {
        return when (this) {
            NotInit -> "NotInit"
            Wait -> "Wait"
            Synced -> "Synced"
        }
    }
}