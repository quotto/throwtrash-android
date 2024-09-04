package net.mythrowaway.app.domain.trash.entity.sync

sealed class SyncState(val value: Int) {
    object Wait : SyncState(1)
    object Synced : SyncState(2)
}