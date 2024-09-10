package net.mythrowaway.app.module.trash.entity.trash

class TrashList(trashList: List<Trash>) {

  private var _trashList: MutableList<Trash> = trashList.toMutableList()
  val trashList: List<Trash>
        get() = _trashList.toList()

    init {
      if (_trashList.size > 10) {
            throw IllegalArgumentException("Trash count is over 10.")
        }
        // 同じIDのTrashが存在する場合はエラーを返す
        if (_trashList.size != _trashList.distinctBy { it.id }.size) {
            throw IllegalArgumentException("Trash ID is duplicated.")
        }
    }
    fun addTrash(trash: Trash) {
      // 同じIDのTrashが存在する場合はエラーを返す
      if (_trashList.any { it.equals(trash)}) {
        throw IllegalArgumentException("Trash ID ${trash.id} is already exists.")
      }
      // 10個以上のTrashは登録できない
      if (_trashList.size >= 10) {
        throw IllegalArgumentException("Trash count is over 10.")
      }
      _trashList.add(trash)
    }

    fun removeTrash(trash: Trash) {
      // 同じIDのTrashが存在しない場合はエラーを返す
      if (!_trashList.any { it.equals(trash) }) {
        throw IllegalArgumentException("Trash ID ${trash.id} is not found.")
      }
      _trashList.removeIf { it.equals(trash) }
    }

    fun canAddTrash(): Boolean {
        return _trashList.size < 10
    }
}