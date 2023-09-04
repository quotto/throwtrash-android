package net.mythrowaway.app.presenter

import net.mythrowaway.app.service.TrashDataConverter
import net.mythrowaway.app.domain.TrashData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TrashDataConverterTest: TrashDataConverter() {
    @Test
    fun jsonToTrashData_notOther() {
        val data = """
            {"type":"burn","schedules":[{"type":"weekday","value":"0"}]}
        """.trimIndent()
        val trashData: TrashData = jsonToTrashData(data)

        assertEquals("burn", trashData.type)
        assertEquals(null, trashData.trash_val)
        assertEquals("weekday", trashData.schedules[0].type)
        assertEquals("0", trashData.schedules[0].value)
    }

    @Test
    fun jsonToTrashData_Other_MultiSchedule() {
        val data = """
            {"type":"other","trash_val":"生ゴミ","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"1","start":"2020-02-01","interval": 2}}]}
        """.trimIndent()
        val trashData: TrashData = jsonToTrashData(data)

        assertEquals("other", trashData.type)
        assertEquals("生ゴミ", trashData.trash_val)
        assertEquals("weekday", trashData.schedules[0].type)
        assertEquals("0", trashData.schedules[0].value)
        assertEquals("evweek", trashData.schedules[1].type)
        assertEquals("1", (trashData.schedules[1].value as HashMap<String,String>)["weekday"])
        assertEquals("2020-02-01", (trashData.schedules[1].value as HashMap<String,String>)["start"])
        assertEquals(2, (trashData.schedules[1].value as HashMap<String,Int>)["interval"])
    }

    @Test
    fun jsonToTrashList() {
        val data = """
            [
                {"type": "burn","schedules":[{"type":"weekday", "value": "1"},{"type":"month","value":"2"}]},
                {"type": "other","trash_val":"生ゴミ","schedules":[{"type":"evweek","value":{"weekday":"1","start":"2020-02-01","interval": 3}}]}
            ]
        """

        val trashList:ArrayList<TrashData> = jsonToTrashList(data)

        assertEquals("burn",trashList[0].type)
        assertEquals("weekday",trashList[0].schedules[0].type)
        assertEquals("1",trashList[0].schedules[0].value)
        assertEquals("month",trashList[0].schedules[1].type)
        assertEquals("2",trashList[0].schedules[1].value)
        assertEquals("other",trashList[1].type)
        assertEquals("生ゴミ",trashList[1].trash_val)
        assertEquals("evweek",trashList[1].schedules[0].type)
        assertEquals("1",(trashList[1].schedules[0].value as HashMap<String,String>)["weekday"])
        assertEquals("2020-02-01",(trashList[1].schedules[0].value as HashMap<String,String>)["start"])
        assertEquals(3,(trashList[1].schedules[0].value as HashMap<String,Int>)["interval"])
    }

    @Test
    fun jsonToTrashList2() {
        val data = """
            [{"id":"1607829262285","schedules":[{"type":"biweek","value":"1-1"}],"trash_val":"不燃ゴミ","type":"other"},{"id":"1607829315858","schedules":[{"type":"weekday","value":"5"}],"trash_val":"資源ペット","type":"other"},{"id":"1607829354040","schedules":[{"type":"weekday","value":"2"}],"trash_val":"有価物","type":"other"},{"id":"1607829380667","schedules":[{"type":"weekday","value":"3"},{"type":"weekday","value":"6"}],"trash_val":"可燃ゴミ","type":"other"}]"
        """.trimIndent()

        val trashList: ArrayList<TrashData> = jsonToTrashList(data)
        assertEquals(4,trashList.size)
        assertEquals("1607829262285",trashList[0].id)
    }

    @Test
    fun jsonToTrashList_Empty() {
        val data = "[]"
        val result:ArrayList<TrashData> = jsonToTrashList(data)
        assert(result.isEmpty())
    }


}