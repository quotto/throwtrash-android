package net.mythrowaway.app.service

import net.mythrowaway.app.adapter.repository.PreferencePersistImpl
import net.mythrowaway.app.domain.ExcludeDate
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.domain.TrashSchedule
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.ArrayList

@RunWith(PowerMockRunner::class)
@PrepareForTest(PreferencePersistImpl::class)
class TrashManagerTest {
    // 202001を想定したカレンダー日付
    private val dataSet: ArrayList<Int> = arrayListOf(29,30,31,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,1)
    private val  mockPersist: PreferencePersistImpl = PowerMockito.mock(PreferencePersistImpl::class.java)
    private val target: TrashManager = TrashManager(mockPersist)

    @Test
    fun getTrashName() {
        val method: Method = target.javaClass.getDeclaredMethod("getTrashName",String::class.java,String::class.java)
        method.isAccessible = true
        Assert.assertEquals("もえるゴミ",method.invoke(target,"burn", null))
        Assert.assertEquals("生ゴミ",method.invoke(target,"other","生ゴミ"))
        Assert.assertEquals("",method.invoke(target,"none","trash_val"))
        Assert.assertEquals("",method.invoke(target,"other",null))
    }

    @Test
    fun getComputeCalendar() {
        val method: Method = target.javaClass.getDeclaredMethod("getComputeCalendar",Int::class.java,Int::class.java,Int::class.java,Int::class.java)
        method.isAccessible = true

        // 当月
        val result1: Calendar = method.invoke(target,2020,1,12,13) as Calendar
        Assert.assertEquals(0,result1.get(Calendar.MONTH))
        Assert.assertEquals(1,result1.get(Calendar.DAY_OF_WEEK))

        // 前月
        val result2: Calendar = method.invoke(target,2020,1,31,2) as Calendar
        Assert.assertEquals(11,result2.get(Calendar.MONTH))
        Assert.assertEquals(3,result2.get(Calendar.DAY_OF_WEEK))

        // 翌月
        val result3: Calendar = method.invoke(target,2020,1,1,34) as Calendar
        Assert.assertEquals(1,result3.get(Calendar.MONTH))
        Assert.assertEquals(7,result3.get(Calendar.DAY_OF_WEEK))
    }

    @Test
    fun getEnableTrashListByWeekday() {
        val trash1 = TrashData().apply {
            type = "burn"
            schedules = arrayListOf(
                TrashSchedule().apply{
                type = "weekday"
                value = "1"
            },  TrashSchedule().apply{
                type = "weekday"
                value = "2"
            })
        }
        val trash2 = TrashData().apply {
            type = "bin"
            schedules = arrayListOf(
                TrashSchedule().apply{
                type = "weekday"
                value = "1"
            })
        }

        Mockito.`when`(mockPersist.getAllTrashSchedule()).thenReturn(arrayListOf(trash1,trash2))
        target.refresh()

        val result: Array<ArrayList<String>> = target.getEnableTrashList(2020,1,dataSet)
        Assert.assertEquals(2,result[8].size)
        Assert.assertEquals("もえるゴミ",result[8][0])
        Assert.assertEquals("ビン",result[8][1])
        Assert.assertEquals(1,result[9].size)
        Assert.assertEquals(0,result[10].size)
    }

    @Test
    fun getEnableTrashListByMonth() {
        val trash1 = TrashData().apply {
            type = "burn"
            schedules = arrayListOf(
                TrashSchedule().apply{
                type = "month"
                value = "3"
            }, TrashSchedule().apply{
                type = "month"
                value = "29"
            })
        }
        val trash2 = TrashData().apply {
            type = "other"
            trash_val = "家電"
            schedules = arrayListOf(
                TrashSchedule().apply{
                type = "month"
                value = "3"
            })
        }

        Mockito.`when`(mockPersist.getAllTrashSchedule()).thenReturn(arrayListOf(trash1,trash2))
        target.refresh()

        val result: Array<ArrayList<String>> = target.getEnableTrashList(2020,1,dataSet)
        Assert.assertEquals(2,result[5].size)
        Assert.assertEquals("もえるゴミ",result[5][0])
        Assert.assertEquals("家電",result[5][1])
        Assert.assertEquals(1,result[0].size)
        Assert.assertEquals(1,result[31].size)
        Assert.assertEquals("もえるゴミ",result[0][0])
    }

    @Test
    fun getEnableTrashListByBiweek() {
        val trash1 = TrashData().apply {
            type = "burn"
            schedules = arrayListOf(
                TrashSchedule().apply{
                type = "biweek"
                value = "0-3"
            }, TrashSchedule().apply{
                type = "biweek"
                value = "6-1"
            })
        }
        val trash2 = TrashData().apply {
            type = "other"
            trash_val = "家電"
            schedules = arrayListOf(
                TrashSchedule().apply{
                type = "biweek"
                value = "0-3"
            })
        }

        Mockito.`when`(mockPersist.getAllTrashSchedule()).thenReturn(arrayListOf(trash1,trash2))
        target.refresh()

        val result: Array<ArrayList<String>> = target.getEnableTrashList(2020,1,dataSet)
        Assert.assertEquals(2,result[21].size)
        Assert.assertEquals("もえるゴミ",result[21][0])
        Assert.assertEquals("家電",result[21][1])
        Assert.assertEquals(1,result[6].size)
        Assert.assertEquals(1,result[34].size)
        Assert.assertEquals("もえるゴミ",result[34][0])
    }

    @Test
    fun getEnableTrashListByEvweek_interval2() {
        val trash1 = TrashData().apply {
            type = "burn"
            schedules = arrayListOf(
                TrashSchedule().apply{
                type = "evweek"
                value = hashMapOf("weekday" to "3", "start" to "2020-01-05", "interval" to 2)
            },  TrashSchedule().apply{
                type = "evweek"
                value = hashMapOf("weekday" to "0", "start" to "2019-12-29", "interval" to 2)
            })
        }
        val trash2 = TrashData().apply {
            type = "other"
            trash_val = "家電"
            schedules = arrayListOf(
                TrashSchedule().apply{
                type = "evweek"
                value = hashMapOf("weekday" to "3", "start" to "2020-01-05", "interval" to 2)
            })
        }

        Mockito.`when`(mockPersist.getAllTrashSchedule()).thenReturn(arrayListOf(trash1,trash2))
        target.refresh()

        val result: Array<ArrayList<String>> = target.getEnableTrashList(2020,1,dataSet)
        Assert.assertEquals(2,result[10].size)
        Assert.assertEquals(2,result[24].size)
        Assert.assertEquals("もえるゴミ",result[10][0])
        Assert.assertEquals("家電",result[24][1])
        Assert.assertEquals(1,result[0].size)
        Assert.assertEquals(1,result[14].size)
        Assert.assertEquals(1,result[28].size)
        Assert.assertEquals("もえるゴミ",result[0][0])
    }

    fun getEnableTrashListByEvweek_interval3() {
        val trash1 = TrashData().apply {
            type = "burn"
            schedules = arrayListOf(
                TrashSchedule().apply{
                    type = "evweek"
                    value = hashMapOf("weekday" to "3", "start" to "2020-01-05", "interval" to 3)
                },  TrashSchedule().apply{
                    type = "evweek"
                    value = hashMapOf("weekday" to "0", "start" to "2019-12-22", "interval" to 3)
                })
        }
        val trash2 = TrashData().apply {
            type = "other"
            trash_val = "家電"
            schedules = arrayListOf(
                TrashSchedule().apply{
                    type = "evweek"
                    value = hashMapOf("weekday" to "3", "start" to "2020-01-05", "interval" to 3)
                })
        }

        Mockito.`when`(mockPersist.getAllTrashSchedule()).thenReturn(arrayListOf(trash1,trash2))
        target.refresh()

        val result: Array<ArrayList<String>> = target.getEnableTrashList(2020,1,dataSet)
        Assert.assertEquals(2,result[10].size)
        Assert.assertEquals(2,result[31].size)
        Assert.assertEquals("もえるゴミ",result[10][0])
        Assert.assertEquals("家電",result[31][1])
        Assert.assertEquals(0,result[0].size)
        Assert.assertEquals(1,result[14].size)
        Assert.assertEquals(1,result[35].size)
        Assert.assertEquals("もえるゴミ",result[14][0])
    }

    fun getEnableTrashListByEvweek_interval4() {
        val trash1 = TrashData().apply {
            type = "burn"
            schedules = arrayListOf(
                TrashSchedule().apply{
                    type = "evweek"
                    value = hashMapOf("weekday" to "3", "start" to "2019-12-29", "interval" to 4)
                },  TrashSchedule().apply{
                    type = "evweek"
                    value = hashMapOf("weekday" to "0", "start" to "2019-12-07", "interval" to 4)
                })
        }
        val trash2 = TrashData().apply {
            type = "other"
            trash_val = "家電"
            schedules = arrayListOf(
                TrashSchedule().apply{
                    type = "evweek"
                    value = hashMapOf("weekday" to "3", "start" to "2019-12-29", "interval" to 4)
                })
        }

        Mockito.`when`(mockPersist.getAllTrashSchedule()).thenReturn(arrayListOf(trash1,trash2))
        target.refresh()

        val result: Array<ArrayList<String>> = target.getEnableTrashList(2020,1,dataSet)
        Assert.assertEquals(2,result[3].size)
        Assert.assertEquals(2,result[31].size)
        Assert.assertEquals("もえるゴミ",result[3][0])
        Assert.assertEquals("家電",result[31][1])
        Assert.assertEquals(1,result[0].size)
        Assert.assertEquals(0,result[14].size)
        Assert.assertEquals(1,result[28].size)
        Assert.assertEquals("もえるゴミ",result[0][0])
        Assert.assertEquals("もえるゴミ",result[28][0])
    }

    /**
     * 旧バージョンデータとの非互換用テスト
     * 隔週スケジュールにinterval設定がない場合はデフォルト値2とする
     */
    @Test
    fun getEnableTrashListByEvweek_intervalNone() {
        val trash1 = TrashData().apply {
            type = "burn"
            schedules = arrayListOf(
                TrashSchedule().apply{
                    type = "evweek"
                    value = hashMapOf("weekday" to "3", "start" to "2020-01-05")
                },  TrashSchedule().apply{
                    type = "evweek"
                    value = hashMapOf("weekday" to "0", "start" to "2019-12-29")
                })
        }
        val trash2 = TrashData().apply {
            type = "other"
            trash_val = "家電"
            schedules = arrayListOf(
                TrashSchedule().apply{
                    type = "evweek"
                    value = hashMapOf("weekday" to "3", "start" to "2020-01-05")
                })
        }

        Mockito.`when`(mockPersist.getAllTrashSchedule()).thenReturn(arrayListOf(trash1,trash2))
        target.refresh()

        val result: Array<ArrayList<String>> = target.getEnableTrashList(2020,1,dataSet)
        Assert.assertEquals(2,result[10].size)
        Assert.assertEquals(2,result[24].size)
        Assert.assertEquals("もえるゴミ",result[10][0])
        Assert.assertEquals("家電",result[24][1])
        Assert.assertEquals(1,result[0].size)
        Assert.assertEquals(1,result[14].size)
        Assert.assertEquals(1,result[28].size)
        Assert.assertEquals("もえるゴミ",result[0][0])
    }


    @Test
    fun isEvWeekTrue_interval2() {
        // 同じ週のためTrue
        Assert.assertTrue(target.isEvWeek("2020-03-01", "2020-03-05", 2))
        // 翌々週のためTrue
        Assert.assertTrue(target.isEvWeek("2020-03-01", "2020-03-21",2))
        // 月マタギでtrue
        Assert.assertTrue(target.isEvWeek("2020-05-03", "2020-05-31",2))

        // 前々週のためTrue
        Assert.assertTrue(target.isEvWeek("2020-03-01", "2020-02-19",2))
    }

    @Test
    fun isEvWeekFalse_interval2() {
        // 翌週のためFalse
        Assert.assertFalse(target.isEvWeek("2020-03-01", "2020-03-11",2))
        // 前週のためFalse
        Assert.assertFalse(target.isEvWeek("2020-03-01", "2020-02-29",2))
    }

    @Test
    fun isEvWeekTrue_interval3() {
        // 同じ週のためTrue
        Assert.assertTrue(target.isEvWeek("2020-03-01", "2020-03-05", 3))
        // 3週後のためTrue
        Assert.assertTrue(target.isEvWeek("2020-03-01", "2020-03-23",3))
        // 月マタギでtrue
        Assert.assertTrue(target.isEvWeek("2020-05-03", "2020-06-16",3))

        // 3週前のためTrue
        Assert.assertTrue(target.isEvWeek("2020-03-01", "2020-02-12",3))
    }

    @Test
    fun isEvWeekFalse_interval3() {
        // 翌週のためFalse
        Assert.assertFalse(target.isEvWeek("2020-03-01", "2020-03-11",3))
        // 翌々週のためFalse
        Assert.assertFalse(target.isEvWeek("2020-03-01", "2020-03-21",3))
        // 前週のためFalse
        Assert.assertFalse(target.isEvWeek("2020-03-01", "2020-02-29",3))
        // 2週前のためFalse
        Assert.assertFalse(target.isEvWeek("2020-03-01", "2020-02-21",3))
    }

    @Test
    fun isEvWeekTrue_interval4() {
        // 同じ週のためTrue
        Assert.assertTrue(target.isEvWeek("2020-03-01", "2020-03-05", 4))
        // 4週後のためTrue
        Assert.assertTrue(target.isEvWeek("2020-03-01", "2020-03-30",4))
        // 月マタギでtrue
        Assert.assertTrue(target.isEvWeek("2020-05-03", "2020-07-01",4))

        // 4週前のためTrue
        Assert.assertTrue(target.isEvWeek("2020-03-01", "2020-02-04",4))
    }

    @Test
    fun isEvWeekFalse_interval4() {
        // 翌週のためFalse
        Assert.assertFalse(target.isEvWeek("2020-03-01", "2020-03-11",4))
        // 翌々週のためFalse
        Assert.assertFalse(target.isEvWeek("2020-03-01", "2020-03-21",4))
        // 前週のためFalse
        Assert.assertFalse(target.isEvWeek("2020-03-01", "2020-02-29",4))
        // 2週前のためFalse
        Assert.assertFalse(target.isEvWeek("2020-03-01", "2020-02-21",4))
        // 3週後のためFalse
        Assert.assertFalse(target.isEvWeek("2020-03-01", "2020-03-28",4))
        // 3週前のためFalse
        Assert.assertFalse(target.isEvWeek("2020-03-01", "2020-02-14",4))
    }


    @Test
    fun getTodaysTrash() {
        val trash1 = TrashData().apply {
            type = "burn"
            schedules = arrayListOf(
                TrashSchedule().apply{
                type = "weekday"
                value = "3"
            },  TrashSchedule().apply{
                type = "month"
                value = "5"
            })
        }
        val trash2 = TrashData().apply {
            type = "other"
            trash_val = "家電"
            schedules = arrayListOf(
                TrashSchedule().apply{
                type = "biweek"
                value = "3-1"
            })
        }
        val trash3 = TrashData().apply {
            type = "bin"
            trash_val = null
            schedules = arrayListOf(
                TrashSchedule().apply{
                    type = "evweek"
                    value = hashMapOf("start" to "2020-03-08","weekday" to "4","interval" to 2)
                },
                TrashSchedule().apply{
                    type = "evweek"
                    value = hashMapOf("start" to "2020-03-01","weekday" to "4","interval" to 4)
                }
            )
        }

        val trash4 = TrashData().apply {
            type = "paper"
            trash_val = null
            schedules = arrayListOf(
                TrashSchedule().apply{
                    type = "evweek"
                    value = hashMapOf("start" to "2020-03-08","weekday" to "0","interval" to 3)
                }
            )
        }

        // 旧バージョン非互換用テスト
        // 隔週スケジュールにintervalがない場合はデフォルト値2として扱う
        val trash5 = TrashData().apply {
            type = "petbottle"
            trash_val = null
            schedules = arrayListOf(
                TrashSchedule().apply{
                    type = "evweek"
                    // intervalが無いので2週間隔,trash3と全く同じスケジュールになる想定
                    value = hashMapOf("start" to "2020-03-08","weekday" to "4")
                },
                TrashSchedule().apply{
                    type = "evweek"
                    value = hashMapOf("start" to "2020-03-01","weekday" to "4","interval" to 4)
                }
            )
        }

        Mockito.`when`(mockPersist.getAllTrashSchedule()).thenReturn(arrayListOf(trash1,trash2,trash3,trash4,trash5))
        target.refresh()

        var result:ArrayList<TrashData> = target.getTodaysTrash(2020,3,4)
        Assert.assertEquals(2,result.size)
        Assert.assertEquals("burn", result[0].type)
        Assert.assertEquals("other", result[1].type)

        result = target.getTodaysTrash(2020,3,5)
        Assert.assertEquals(3,result.size)
        Assert.assertEquals("burn", result[0].type)
        Assert.assertEquals("bin", result[1].type)
        Assert.assertEquals("petbottle", result[2].type)

        result = target.getTodaysTrash(2020,3,12)
        Assert.assertEquals(2,result.size)
        Assert.assertEquals("bin", result[0].type)
        Assert.assertEquals("petbottle", result[1].type)

        result = target.getTodaysTrash(2020,3,29)
        Assert.assertEquals(1,result.size)
        Assert.assertEquals("paper", result[0].type)
    }

    @Test
    fun getTodaysTrash_BiWeek() {
        val trash1 = TrashData().apply {
            type = "burn"
            schedules = arrayListOf(
                TrashSchedule().apply {
                    type = "biweek"
                    value = "1-1"   // 2020/09/07は2週目の第1月曜
                })
        }

        val trash2 = TrashData().apply {
            type = "bottle"
            schedules = arrayListOf(
                TrashSchedule().apply {
                    type = "biweek"
                    value = "2-2"   // 2020/09/08は2週目の第2火曜
                })
        }

        val trash3 = TrashData().apply {
            type = "paper"
            schedules = arrayListOf(
                TrashSchedule().apply {
                    type = "biweek"
                    value = "3-5"   // 2020/09/30は5週目の第5水曜
                })
        }

        Mockito.`when`(mockPersist.getAllTrashSchedule()).thenReturn(arrayListOf(trash1,trash2,trash3))
        target.refresh()

        val result1:ArrayList<TrashData> = target.getTodaysTrash(2020,9,7)
        Assert.assertEquals(1,result1.size)
        Assert.assertEquals("burn",result1[0].type)

        val result2:ArrayList<TrashData> = target.getTodaysTrash(2020,9,8)
        Assert.assertEquals(1,result2.size)
        Assert.assertEquals("bottle",result2[0].type)

        val result3:ArrayList<TrashData> = target.getTodaysTrash(2020,9,1)
        Assert.assertEquals(0,result3.size)

        val result4:ArrayList<TrashData> = target.getTodaysTrash(2020,9,30)
        Assert.assertEquals(1,result4.size)
        Assert.assertEquals("paper",result4[0].type)
    }

    @Test
    fun getTodaysTrash_ExcludeDate() {
        val trash1 = TrashData().apply {
            type = "burn"
            schedules = arrayListOf(
                TrashSchedule().apply{
                    type = "weekday"
                    value = "3"
                },  TrashSchedule().apply{
                    type = "month"
                    value = "5"
                })
            excludes = listOf(
                ExcludeDate().apply {
                    month = 3
                    date = 4
                }
            )
        }
        val trash2 = TrashData().apply {
            type = "other"
            trash_val = "家電"
            schedules = arrayListOf(
                TrashSchedule().apply{
                    type = "biweek"
                    value = "3-1"
                })
        }

        val trash3 = TrashData().apply {
            type = "bin"
            trash_val = null
            schedules = arrayListOf(
                TrashSchedule().apply{
                    type = "evweek"
                    value = hashMapOf("start" to "2020-03-08","weekday" to "4","interval" to 2)
                },
                TrashSchedule().apply{
                    type = "evweek"
                    value = hashMapOf("start" to "2020-03-01","weekday" to "4","interval" to 4)
                }
            )
            excludes = listOf()
        }

        Mockito.`when`(mockPersist.getAllTrashSchedule()).thenReturn(arrayListOf(trash1,trash2,trash3))
        target.refresh()

        var result:ArrayList<TrashData> = target.getTodaysTrash(2020,3,4)
        Assert.assertEquals(1,result.size)
        Assert.assertEquals("other", result[0].type)

        result = target.getTodaysTrash(2020,3,5)
        Assert.assertEquals(2,result.size)
        Assert.assertEquals("burn", result[0].type)
        Assert.assertEquals("bin", result[1].type)

        result = target.getTodaysTrash(2020,3,12)
        Assert.assertEquals(1,result.size)
        Assert.assertEquals("bin", result[0].type)
    }

    @Test
    fun getEnableTrashList_ExcludeDate_Weekday() {
        val trash1 = TrashData().apply {
            type = "burn"
            schedules = arrayListOf(
                TrashSchedule().apply {
                    type = "weekday"
                    value = "2"
                },
                TrashSchedule().apply {
                    type = "weekday"
                    value = "6"
                }
            )
            excludes = listOf(
                // 前月例外
                ExcludeDate().apply {
                    month = 12
                    date = 31
                },
                // 当月例外
                ExcludeDate().apply {
                    month = 1
                    date = 7
                },
                // 翌月例外
                ExcludeDate().apply {
                    month = 2
                    date = 1
                }
            )
        }

        val trash2 = TrashData().apply {
            type = "unburn"
            schedules = arrayListOf(
                TrashSchedule().apply {
                    type = "weekday"
                    value = "2"
                },
                TrashSchedule().apply {
                    type = "weekday"
                    value = "6"
                }
            )
        }

        Mockito.`when`(mockPersist.getAllTrashSchedule()).thenReturn(arrayListOf(trash1,trash2))
        target.refresh()
        val result = target.getEnableTrashList(2020,1,dataSet)

        Assert.assertEquals(1,result[2].size)
        Assert.assertEquals(1,result[9].size)
        Assert.assertEquals(1,result[34].size)
    }

    @Test
    fun getEnableTrashList_ExcludeDate_Month(){
        val trash1 = TrashData().apply {
            type = "burn"
            schedules = arrayListOf(
                TrashSchedule().apply {
                    type = "month"
                    value = "29"
                },
                TrashSchedule().apply {
                    type = "month"
                    value = "1"
                }
            )
            excludes = listOf(
                // 前月例外
                ExcludeDate().apply {
                    month = 12
                    date = 29
                },
                // 当月例外
                ExcludeDate().apply {
                    month = 1
                    date = 1
                },
                // 翌月例外
                ExcludeDate().apply {
                    month = 2
                    date = 1
                }
            )
        }

        val trash2 = TrashData().apply {
            type = "unburn"
            schedules = arrayListOf(
                TrashSchedule().apply {
                    type = "month"
                    value = "29"
                },
                TrashSchedule().apply {
                    type = "month"
                    value = "1"
                }
            )
        }

        Mockito.`when`(mockPersist.getAllTrashSchedule()).thenReturn(arrayListOf(trash1,trash2))
        target.refresh()
        val result = target.getEnableTrashList(2020,1,dataSet)

        Assert.assertEquals(1,result[0].size)
        Assert.assertEquals(1,result[3].size)
        Assert.assertEquals(1,result[34].size)
    }

    @Test
    fun getEnableTrashList_ExcludeDate_Biweek() {
        val trash1 = TrashData().apply {
            type = "burn"
            schedules = arrayListOf(
                TrashSchedule().apply {
                    type = "biweek"
                    value = "1-1"   // 2020/1/6
                },
                TrashSchedule().apply {
                    type = "biweek"
                    value = "0-5"   // 2019/12/29
                },
                TrashSchedule().apply {
                    type = "biweek"
                    value = "6-1"   // 2020/2/1
                }
            )
            excludes = listOf(
                // 前月例外
                ExcludeDate().apply {
                    month = 12
                    date = 29
                },
                // 当月例外
                ExcludeDate().apply {
                    month = 1
                    date = 6
                },
                // 翌月例外
                ExcludeDate().apply {
                    month = 2
                    date = 1
                }
            )
        }
        val trash2 = TrashData().apply {
            type = "burn"
            schedules = arrayListOf(
                TrashSchedule().apply {
                    type = "biweek"
                    value = "1-1"   // 2020/1/6
                },
                TrashSchedule().apply {
                    type = "biweek"
                    value = "0-5"   // 2019/12/29
                },
                TrashSchedule().apply {
                    type = "biweek"
                    value = "6-1"   // 2020/2/1
                }
            )
        }

        Mockito.`when`(mockPersist.getAllTrashSchedule()).thenReturn(arrayListOf(trash1,trash2))
        target.refresh()
        val result = target.getEnableTrashList(2020, 1, dataSet)

        Assert.assertEquals(1, result[8].size)
        Assert.assertEquals(1, result[0].size)
        Assert.assertEquals(1, result[34].size)
    }

    @Test
    fun getEnableTrashList_ExcludeDate_Evweek() {
        val trash1 = TrashData().apply {
            type = "paper"
            trash_val = null
            schedules = arrayListOf(
                TrashSchedule().apply{
                    type = "evweek"
                    value = hashMapOf("start" to "2019-12-29","weekday" to "6","interval" to 2)
                },
                TrashSchedule().apply{
                    type = "evweek"
                    value = hashMapOf("start" to "2020-01-19","weekday" to "6","interval" to 2)
                },
                TrashSchedule().apply{
                    type = "evweek"
                    value = hashMapOf("start" to "2019-12-15","weekday" to "0","interval" to 2)
                }
            )
            excludes = listOf(
                // 前月例外
                ExcludeDate().apply {
                    month = 12
                    date = 29
                },
                // 当月例外
                ExcludeDate().apply {
                    month = 1
                    date = 4
                },
                // 翌月例外
                ExcludeDate().apply {
                    month = 2
                    date = 1
                }
            )
        }

        val trash2 = TrashData().apply {
            type = "paper"
            trash_val = null
            schedules = arrayListOf(
                TrashSchedule().apply{
                    type = "evweek"
                    value = hashMapOf("start" to "2019-12-29","weekday" to "6","interval" to 2)
                },
                TrashSchedule().apply{
                    type = "evweek"
                    value = hashMapOf("start" to "2020-01-19","weekday" to "6","interval" to 2)
                },
                TrashSchedule().apply{
                    type = "evweek"
                    value = hashMapOf("start" to "2019-12-15","weekday" to "0","interval" to 2)
                }
            )
        }

        Mockito.`when`(mockPersist.getAllTrashSchedule()).thenReturn(arrayListOf(trash1,trash2))
        target.refresh()
        val result = target.getEnableTrashList(2020, 1, dataSet)

        Assert.assertEquals(1, result[0].size)
        Assert.assertEquals(1, result[6].size)
        Assert.assertEquals(1, result[34].size)

    }

    @Test
    fun getActualMonth() {
        val method: Method = target.javaClass.getDeclaredMethod("getActualMonth",Int::class.java,Int::class.java,Int::class.java)
        method.isAccessible = true

        Assert.assertEquals(1,method.invoke(target,1,6,7))
        Assert.assertEquals(12,method.invoke(target,1,29,0))
        Assert.assertEquals(2,method.invoke(target,1,1,34))

        Assert.assertEquals(1,method.invoke(target,12,1,28))
        Assert.assertEquals(1,method.invoke(target,2,29,6))
    }
}