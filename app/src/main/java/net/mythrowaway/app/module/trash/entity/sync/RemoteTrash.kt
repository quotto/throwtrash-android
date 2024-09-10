package net.mythrowaway.app.module.trash.entity.sync

import net.mythrowaway.app.module.trash.entity.trash.TrashList

class RemoteTrash(private val _timestamp: Long, private val _trashList: TrashList) {
    val timestamp: Long
        get() = _timestamp

    val trashList: TrashList
        get() = _trashList
}