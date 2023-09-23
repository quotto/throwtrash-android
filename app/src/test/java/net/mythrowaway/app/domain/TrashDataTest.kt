package net.mythrowaway.app.domain

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TrashDataTest {
  @Test
  fun equalsWithTypeAndValue_TypeIsEquals_TrashValIsNotEquals() {
    val trash1 = TrashData().apply {
      type = "burn"
      trash_val = ""
    }
    val trash2 = TrashData().apply {
      type = "burn"
      trash_val = "burn"
    }
    assertTrue(trash1.equalsWithTypeAndValue(trash2))
  }

  @Test
  fun equalsWithTypeAndValue_TypeIsNotEquals_TrashValIsEquals() {
    val trash1 = TrashData().apply {
      type = "burn"
      trash_val = "burn"
    }
    val trash2 = TrashData().apply {
      type = "unburn"
      trash_val = "burn"
    }
    assertFalse(trash1.equalsWithTypeAndValue(trash2))
  }

  @Test
  fun equalsWithTypeAndValue_TypeIsEquals_TrashValIsNull() {
    val trash1 = TrashData().apply {
      type = "burn"
      trash_val = null
    }
    val trash2 = TrashData().apply {
      type = "burn"
      trash_val = "burn"
    }
    assertTrue(trash1.equalsWithTypeAndValue(trash2))
  }

  @Test
  fun equalsWithTypeAndValue_TypeIsNotEquals_TrashValIsNotEquals() {
    val trash1 = TrashData().apply {
      type = "burn"
      trash_val = "burn"
    }
    val trash2 = TrashData().apply {
      type = "unburn"
      trash_val = "burn"
    }
    assertFalse(trash1.equalsWithTypeAndValue(trash2))
  }

  @Test
  fun equalsWithTypeAndValue_TypeIsNotEquals_TrashValIsNull() {
    val trash1 = TrashData().apply {
      type = "burn"
      trash_val = null
    }
    val trash2 = TrashData().apply {
      type = "unburn"
      trash_val = "burn"
    }
    assertFalse(trash1.equalsWithTypeAndValue(trash2))
  }

  @Test
  fun equalsWithTypeAndValue_TypeIsEquals_TrashValIsEquals() {
    val trash1 = TrashData().apply {
      type = "burn"
      trash_val = "burn"
    }
    val trash2 = TrashData().apply {
      type = "unburn"
      trash_val = "burn"
    }
    assertFalse(trash1.equalsWithTypeAndValue(trash2))
  }

  @Test
  fun equalsWithTypeAndValue_TypeIsOther_TypeIsEquals_TrashValIsEquals() {
    val trash1 = TrashData().apply {
      type = "other"
      trash_val = "生ごみ"
    }
    val trash2 = TrashData().apply {
      type = "other"
      trash_val = "生ごみ"
    }
    assertTrue(trash1.equalsWithTypeAndValue(trash2))
  }

  @Test
  fun equalsWithTypeAndValue_TypeIsOther_TypeIsEquals_TrashValIsNotEquals() {
    val trash1 = TrashData().apply {
      type = "other"
      trash_val = "生ごみ"
    }
    val trash2 = TrashData().apply {
      type = "other"
      trash_val = "燃えないごみ"
    }
    assertFalse(trash1.equalsWithTypeAndValue(trash2))
  }

  @Test
  fun equalsWithTypeAndValue_TypeIsOther_TypeIsEquals_TrashValIsNull() {
    val trash1 = TrashData().apply {
      type = "other"
      trash_val = "生ごみ"
    }
    val trash2 = TrashData().apply {
      type = "other"
      trash_val = null
    }
    assertFalse(trash1.equalsWithTypeAndValue(trash2))
  }
}