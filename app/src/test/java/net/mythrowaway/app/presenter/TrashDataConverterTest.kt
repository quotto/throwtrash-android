package net.mythrowaway.app.presenter

import net.mythrowaway.app.adapter.TrashDataConverter
import net.mythrowaway.app.domain.TrashData
import org.junit.Assert
import org.junit.Test

class TrashDataConverterTest: TrashDataConverter() {
    @Test
    fun jsonToTrashData_notOther() {
        val data = """
            {"type":"burn","schedules":[{"type":"weekday","value":"0"}]}
        """.trimIndent()
        val trashData: TrashData = jsonToTrashData(data)

        Assert.assertEquals("burn", trashData.type)
        Assert.assertEquals(null, trashData.trash_val)
        Assert.assertEquals("weekday", trashData.schedules[0].type)
        Assert.assertEquals("0", trashData.schedules[0].value)
    }

    @Test
    fun jsonToTrashData_Other_MultiSchedule() {
        val data = """
            {"type":"other","trash_val":"生ゴミ","schedules":[{"type":"weekday","value":"0"},{"type":"evweek","value":{"weekday":"1","start":"2020-02-01","interval": 2}}]}
        """.trimIndent()
        val trashData: TrashData = jsonToTrashData(data)

        Assert.assertEquals("other", trashData.type)
        Assert.assertEquals("生ゴミ", trashData.trash_val)
        Assert.assertEquals("weekday", trashData.schedules[0].type)
        Assert.assertEquals("0", trashData.schedules[0].value)
        Assert.assertEquals("evweek", trashData.schedules[1].type)
        Assert.assertEquals("1", (trashData.schedules[1].value as HashMap<String,String>)["weekday"])
        Assert.assertEquals("2020-02-01", (trashData.schedules[1].value as HashMap<String,String>)["start"])
        Assert.assertEquals(2, (trashData.schedules[1].value as HashMap<String,Int>)["interval"])
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

        Assert.assertEquals("burn",trashList[0].type)
        Assert.assertEquals("weekday",trashList[0].schedules[0].type)
        Assert.assertEquals("1",trashList[0].schedules[0].value)
        Assert.assertEquals("month",trashList[0].schedules[1].type)
        Assert.assertEquals("2",trashList[0].schedules[1].value)
        Assert.assertEquals("other",trashList[1].type)
        Assert.assertEquals("生ゴミ",trashList[1].trash_val)
        Assert.assertEquals("evweek",trashList[1].schedules[0].type)
        Assert.assertEquals("1",(trashList[1].schedules[0].value as HashMap<String,String>)["weekday"])
        Assert.assertEquals("2020-02-01",(trashList[1].schedules[0].value as HashMap<String,String>)["start"])
        Assert.assertEquals(3,(trashList[1].schedules[0].value as HashMap<String,Int>)["interval"])
    }

    @Test
    fun jsonToTrashList_Empty() {
        val data = "[]"
        val result:ArrayList<TrashData> = jsonToTrashList(data)
        assert(result.isEmpty())
    }


}