package net.mythrowaway.app.domain

import com.fasterxml.jackson.annotation.JsonValue

enum class TrashType(val type: String) {
  BURN("burn"),
  UNBURN("unburn"),
  PLASTIC("plastic"),
  CAN("can"),
  PETBOTTLE("petbottle"),
  BOTTLE("bin"),
  PAPER("paper"),
  COARSE("coarse"),
  RESOURCE("resource"),
  OTHER("other");

  @JsonValue
  override fun toString(): String {
    return type
  }

  fun getTrashText(): String {
    return when (this) {
      BURN -> "もえるゴミ"
      UNBURN -> "もえないゴミ"
      PLASTIC -> "プラスチック"
      CAN -> "カン"
      PETBOTTLE -> "ペットボトル"
      BOTTLE -> "ビン"
      PAPER -> "紙"
      COARSE -> "粗大ゴミ"
      RESOURCE -> "資源ごみ"
      OTHER -> "その他"
    }
  }
}