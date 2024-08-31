package net.mythrowaway.app.domain.trash.entity

sealed class SyncState(val value: Int) {
    object SyncNone : SyncState(0)
    object Wait : SyncState(1)
    object Synced : SyncState(2)
}