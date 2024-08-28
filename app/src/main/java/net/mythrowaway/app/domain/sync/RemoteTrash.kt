package net.mythrowaway.app.domain.sync

import net.mythrowaway.app.domain.TrashList

class RemoteTrash(private val _timestamp: Long, private val _trashList: TrashList) {
    val timestamp: Long
        get() = _timestamp

    val trashList: TrashList
        get() = _trashList
}