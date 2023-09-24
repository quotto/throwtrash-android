package net.mythrowaway.app.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test

class TrashTypeTest {
  @Test
  fun burn_toString() {
    assertEquals( "burn",TrashType.BURN.toString())
  }
  @Test
  fun unburn_toString() {
    assertEquals( "unburn",TrashType.UNBURN.toString())
  }
  @Test
  fun other_toString() {
    assertEquals( "other",TrashType.OTHER.toString())
  }
  @Test
  fun bin_toString() {
    assertEquals( "bin",TrashType.BOTTLE.toString())
  }
  @Test
  fun can_toString() {
    assertEquals( "can",TrashType.CAN.toString())
  }
  @Test
  fun petbottle_toString() {
    assertEquals( "petbottle",TrashType.PETBOTTLE.toString())
  }
  @Test
  fun paper_toString() {
    assertEquals( "paper",TrashType.PAPER.toString())
  }
  @Test
  fun plastic_toString() {
    assertEquals( "plastic",TrashType.PLASTIC.toString())
  }
  @Test
  fun resource_toString() {
    assertEquals( "resource",TrashType.RESOURCE.toString())
  }
  @Test
  fun coarse_toString() {
    assertEquals( "coarse",TrashType.COARSE.toString())
  }
  @Test
  fun illegalValue() {
    try {
      TrashType.valueOf("illegalValue")
      fail()
    } catch (e: IllegalArgumentException) {
      assertEquals("No enum constant net.mythrowaway.app.domain.TrashType.illegalValue", e.message)
    }
  }

  @Test
  fun burn_getTrashText() {
    assertEquals( "もえるゴミ",TrashType.BURN.getTrashText())
  }

  @Test
  fun unburn_getTrashText() {
    assertEquals( "もえないゴミ",TrashType.UNBURN.getTrashText())
  }

  @Test
  fun other_getTrashText() {
    assertEquals( "その他",TrashType.OTHER.getTrashText())
  }

  @Test
  fun bin_getTrashText() {
    assertEquals( "ビン",TrashType.BOTTLE.getTrashText())
  }

  @Test
  fun can_getTrashText() {
    assertEquals( "カン",TrashType.CAN.getTrashText())
  }

  @Test
  fun petbottle_getTrashText() {
    assertEquals( "ペットボトル",TrashType.PETBOTTLE.getTrashText())
  }

  @Test
  fun paper_getTrashText() {
    assertEquals( "紙",TrashType.PAPER.getTrashText())
  }

  @Test
  fun plastic_getTrashText() {
    assertEquals( "プラスチック",TrashType.PLASTIC.getTrashText())
  }

  @Test
  fun resource_getTrashText() {
    assertEquals( "資源ごみ",TrashType.RESOURCE.getTrashText())
  }

  @Test
  fun coarse_getTrashText() {
    assertEquals( "粗大ゴミ",TrashType.COARSE.getTrashText())
  }
}