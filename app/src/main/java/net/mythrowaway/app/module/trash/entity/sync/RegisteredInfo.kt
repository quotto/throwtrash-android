package net.mythrowaway.app.module.trash.entity.sync

class RegisteredInfo(
  private val _userId: String,
  private val _latestTrashListUpdateTimestamp: Long,
) {
  val userId: String
    get() = _userId

  val latestTrashListRegisteredTimestamp: Long
    get() = _latestTrashListUpdateTimestamp
}