package net.mythrowaway.app.service

import net.mythrowaway.app.adapter.repository.PreferenceDataRepositoryImpl
import net.mythrowaway.app.domain.ExcludeDate
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.domain.TrashSchedule
import net.mythrowaway.app.domain.TrashType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.ArrayList

class TrashManagerTest {
    // 202001を想定したカレンダー日付
    private val dataSet: ArrayList<Int> = arrayListOf(29,30,31,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,1)
    private val  mockPersist: PreferenceDataRepositoryImpl = Mockito.mock(PreferenceDataRepositoryImpl::class.java)
    private val target: TrashManager = TrashManager(mockPersist)

    @Test
    fun getTrashName() {
        val method: Method = target.javaClass.getDeclaredMethod("getTrashName",TrashType::class.java,String::class.java)
        method.isAccessible = true
        assertEquals("もえるゴミ",method.invoke(target,TrashType.BURN, null))
        assertEquals("生ゴミ",method.invoke(target,TrashType.OTHER,"生ゴミ"))
        assertEquals("",method.invoke(target,TrashType.OTHER,null))
    }

    @Test
    fun getComputeCalendar() {
        val method: Method = target.javaClass.getDeclaredMethod("getComputeCalendar",Int::class.java,Int::class.java,Int::class.java,Int::class.java)
        method.isAccessible = true

        // 当月
        val result1: Calendar = method.invoke(target,2020,1,12,13) as Calendar
        assertEquals(0,result1.get(Calendar.MONTH))
        assertEquals(1,result1.get(Calendar.DAY_OF_WEEK))

        // 前月
        val result2: Calendar = method.invoke(target,2020,1,31,2) as Calendar
        assertEquals(11,result2.get(Calendar.MONTH))
        assertEquals(3,result2.get(Calendar.DAY_OF_WEEK))

        // 翌月
        val result3: Calendar = method.invoke(target,2020,1,1,34) as Calendar
        assertEquals(1,result3.get(Calendar.MONTH))
        assertEquals(7,result3.get(Calendar.DAY_OF_WEEK))
    }

    @Test
    fun getEnableTrashListByWeekday() {
        val trash1 = TrashData().apply {
            type = TrashType.BURN
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
            type = TrashType.PETBOTTLE
            schedules = arrayListOf(
                TrashSchedule().apply{
                type = "weekday"
                value = "1"
            })
        }

        Mockito.`when`(mockPersist.getAllTrashSchedule()).thenReturn(arrayListOf(trash1,trash2))
        target.refresh()

        val result: Array<ArrayList<TrashData>> = target.getEnableTrashList(2020,1,dataSet)
        assertEquals(2,result[8].size)
        assertEquals(TrashType.BURN,result[8][0].type)
        assertEquals(TrashType.PETBOTTLE,result[8][1].type)
        assertEquals(1,result[9].size)
        assertEquals(0,result[10].size)
    }

    @Test
    fun getEnableTrashListByMonth() {
        val trash1 = TrashData().apply {
            type = TrashType.BURN
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
            type = TrashType.OTHER
            trash_val = "家電"
            schedules = arrayListOf(
                TrashSchedule().apply{
                type = "month"
                value = "3"
            })
        }

        Mockito.`when`(mockPersist.getAllTrashSchedule()).thenReturn(arrayListOf(trash1,trash2))
        target.refresh()

        val result: Array<ArrayList<TrashData>> = target.getEnableTrashList(2020,1,dataSet)
        assertEquals(2,result[5].size)
        assertTrue(result[5][0].equals(trash1))
        assertTrue(result[5][1].equals(trash2))
        assertEquals(1,result[0].size)
        assertEquals(1,result[31].size)
        assertTrue(result[0][0].equals(trash1))
    }

    @Test
    fun getEnableTrashListByBiweek() {
        val trash1 = TrashData().apply {
            type = TrashType.BURN
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
            type = TrashType.OTHER
            trash_val = "家電"
            schedules = arrayListOf(
                TrashSchedule().apply{
                type = "biweek"
                value = "0-3"
            })
        }

        Mockito.`when`(mockPersist.getAllTrashSchedule()).thenReturn(arrayListOf(trash1,trash2))
        target.refresh()

        val result: Array<ArrayList<TrashData>> = target.getEnableTrashList(2020,1,dataSet)
        assertEquals(2,result[21].size)
        assertTrue(result[21][0].equals(trash1))
        assertTrue(result[21][1].equals(trash2))
        assertEquals(1,result[6].size)
        assertEquals(1,result[34].size)
        assertTrue(result[34][0].equals(trash1))
    }

    @Test
    fun getEnableTrashListByEvweek_interval2() {
        val trash1 = TrashData().apply {
            type = TrashType.BURN
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
            type = TrashType.OTHER
            trash_val = "家電"
            schedules = arrayListOf(
                TrashSchedule().apply{
                type = "evweek"
                value = hashMapOf("weekday" to "3", "start" to "2020-01-05", "interval" to 2)
            })
        }

        Mockito.`when`(mockPersist.getAllTrashSchedule()).thenReturn(arrayListOf(trash1,trash2))
        target.refresh()

        val result: Array<ArrayList<TrashData>> = target.getEnableTrashList(2020,1,dataSet)
        assertEquals(2,result[10].size)
        assertEquals(2,result[24].size)
        assertTrue(result[10][0].equals(trash1))
        assertTrue(result[24][1].equals(trash2))
        assertEquals(1,result[0].size)
        assertEquals(1,result[14].size)
        assertEquals(1,result[28].size)
        assertTrue(result[0][0].equals(trash1))
    }

    @Test
    fun getEnableTrashListByEvweek_interval3() {
        val trash1 = TrashData().apply {
            type = TrashType.BURN
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
            type = TrashType.OTHER
            trash_val = "家電"
            schedules = arrayListOf(
                TrashSchedule().apply{
                    type = "evweek"
                    value = hashMapOf("weekday" to "3", "start" to "2020-01-05", "interval" to 3)
                })
        }

        Mockito.`when`(mockPersist.getAllTrashSchedule()).thenReturn(arrayListOf(trash1,trash2))
        target.refresh()

        val result: Array<ArrayList<TrashData>> = target.getEnableTrashList(2020,1,dataSet)
        assertEquals(2,result[10].size)
        assertEquals(2,result[31].size)
        assertTrue(result[10][0].equals(trash1))
        assertTrue(result[31][1].equals(trash2))
        assertEquals(0,result[0].size)
        assertEquals(1,result[14].size)
        assertTrue(result[14][0].equals(trash1))
    }

    @Test
    fun getEnableTrashListByEvweek_interval4() {
        val trash1 = TrashData().apply {
            type = TrashType.BURN
            schedules = arrayListOf(
                TrashSchedule().apply{
                    type = "evweek"
                    value = hashMapOf("weekday" to "3", "start" to "2019-12-29", "interval" to 4)
                },  TrashSchedule().apply{
                    type = "evweek"
                    value = hashMapOf("weekday" to "0", "start" to "2019-12-01", "interval" to 4)
                })
        }
        val trash2 = TrashData().apply {
            type = TrashType.OTHER
            trash_val = "家電"
            schedules = arrayListOf(
                TrashSchedule().apply{
                    type = "evweek"
                    value = hashMapOf("weekday" to "3", "start" to "2019-12-29", "interval" to 4)
                })
        }

        Mockito.`when`(mockPersist.getAllTrashSchedule()).thenReturn(arrayListOf(trash1,trash2))
        target.refresh()

        val result: Array<ArrayList<TrashData>> = target.getEnableTrashList(2020,1,dataSet)
        assertEquals(2,result[3].size)
        assertEquals(2,result[31].size)
        assertTrue(result[3][0].equals(trash1))
        assertTrue(result[31][1].equals(trash2))
        assertEquals(1,result[0].size)
        assertEquals(0,result[14].size)
        assertEquals(1,result[28].size)
        assertTrue(result[0][0].equals(trash1))
        assertTrue(result[28][0].equals(trash1))
    }

    /**
     * 旧バージョンデータとの非互換用テスト
     * 隔週スケジュールにinterval設定がない場合はデフォルト値2とする
     */
    @Test
    fun getEnableTrashListByEvweek_intervalNone() {
        val trash1 = TrashData().apply {
            type = TrashType.BURN
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
            type = TrashType.OTHER
            trash_val = "家電"
            schedules = arrayListOf(
                TrashSchedule().apply{
                    type = "evweek"
                    value = hashMapOf("weekday" to "3", "start" to "2020-01-05")
                })
        }

        Mockito.`when`(mockPersist.getAllTrashSchedule()).thenReturn(arrayListOf(trash1,trash2))
        target.refresh()

        val result: Array<ArrayList<TrashData>> = target.getEnableTrashList(2020,1,dataSet)
        assertEquals(2,result[10].size)
        assertEquals(2,result[24].size)
        assertTrue(result[10][0].equals(trash1))
        assertTrue(result[24][1].equals(trash2))
        assertEquals(1,result[0].size)
        assertEquals(1,result[14].size)
        assertEquals(1,result[28].size)
        assertTrue(result[0][0].equals(trash1))
    }


    @Test
    fun isEvWeekTrue_interval2() {
        // 同じ週のためTrue
        assertTrue(target.isEvWeek("2020-03-01", "2020-03-05", 2))
        // 翌々週のためTrue
        assertTrue(target.isEvWeek("2020-03-01", "2020-03-21",2))
        // 月マタギでtrue
        assertTrue(target.isEvWeek("2020-05-03", "2020-05-31",2))

        // 前々週のためTrue
        assertTrue(target.isEvWeek("2020-03-01", "2020-02-19",2))
    }

    @Test
    fun isEvWeekFalse_interval2() {
        // 翌週のためFalse
        assertFalse(target.isEvWeek("2020-03-01", "2020-03-11",2))
        // 前週のためFalse
        assertFalse(target.isEvWeek("2020-03-01", "2020-02-29",2))
    }

    @Test
    fun isEvWeekTrue_interval3() {
        // 同じ週のためTrue
        assertTrue(target.isEvWeek("2020-03-01", "2020-03-05", 3))
        // 3週後のためTrue
        assertTrue(target.isEvWeek("2020-03-01", "2020-03-23",3))
        // 月マタギでtrue
        assertTrue(target.isEvWeek("2020-05-03", "2020-06-16",3))

        // 3週前のためTrue
        assertTrue(target.isEvWeek("2020-03-01", "2020-02-12",3))
    }

    @Test
    fun isEvWeekFalse_interval3() {
        // 翌週のためFalse
        assertFalse(target.isEvWeek("2020-03-01", "2020-03-11",3))
        // 翌々週のためFalse
        assertFalse(target.isEvWeek("2020-03-01", "2020-03-21",3))
        // 前週のためFalse
        assertFalse(target.isEvWeek("2020-03-01", "2020-02-29",3))
        // 2週前のためFalse
        assertFalse(target.isEvWeek("2020-03-01", "2020-02-21",3))
    }

    @Test
    fun isEvWeekTrue_interval4() {
        // 同じ週のためTrue
        assertTrue(target.isEvWeek("2020-03-01", "2020-03-05", 4))
        // 4週後のためTrue
        assertTrue(target.isEvWeek("2020-03-01", "2020-03-30",4))
        // 月マタギでtrue
        assertTrue(target.isEvWeek("2020-05-03", "2020-07-01",4))

        // 4週前のためTrue
        assertTrue(target.isEvWeek("2020-03-01", "2020-02-04",4))
    }

    @Test
    fun isEvWeekFalse_interval4() {
        // 翌週のためFalse
        assertFalse(target.isEvWeek("2020-03-01", "2020-03-11",4))
        // 翌々週のためFalse
        assertFalse(target.isEvWeek("2020-03-01", "2020-03-21",4))
        // 前週のためFalse
        assertFalse(target.isEvWeek("2020-03-01", "2020-02-29",4))
        // 2週前のためFalse
        assertFalse(target.isEvWeek("2020-03-01", "2020-02-21",4))
        // 3週後のためFalse
        assertFalse(target.isEvWeek("2020-03-01", "2020-03-28",4))
        // 3週前のためFalse
        assertFalse(target.isEvWeek("2020-03-01", "2020-02-14",4))
    }


    @Test
    fun getTodaysTrash() {
        val trash1 = TrashData().apply {
            type = TrashType.BURN
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
            type = TrashType.OTHER
            trash_val = "家電"
            schedules = arrayListOf(
                TrashSchedule().apply{
                type = "biweek"
                value = "3-1"
            })
        }
        val trash3 = TrashData().apply {
            type = TrashType.BOTTLE
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
            type = TrashType.PAPER
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
            type = TrashType.PETBOTTLE
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
        assertEquals(2,result.size)
        assertEquals(TrashType.BURN, result[0].type)
        assertEquals(TrashType.OTHER, result[1].type)

        result = target.getTodaysTrash(2020,3,5)
        assertEquals(3,result.size)
        assertEquals(TrashType.BURN, result[0].type)
        assertEquals(TrashType.BOTTLE, result[1].type)
        assertEquals(TrashType.PETBOTTLE, result[2].type)

        result = target.getTodaysTrash(2020,3,12)
        assertEquals(2,result.size)
        assertEquals(TrashType.BOTTLE, result[0].type)
        assertEquals(TrashType.PETBOTTLE, result[1].type)

        result = target.getTodaysTrash(2020,3,29)
        assertEquals(1,result.size)
        assertEquals(TrashType.PAPER, result[0].type)
    }

    @Test
    fun getTodaysTrash_BiWeek() {
        val trash1 = TrashData().apply {
            type = TrashType.BURN
            schedules = arrayListOf(
                TrashSchedule().apply {
                    type = "biweek"
                    value = "1-1"   // 2020/09/07は2週目の第1月曜
                })
        }

        val trash2 = TrashData().apply {
            type = TrashType.PETBOTTLE
            schedules = arrayListOf(
                TrashSchedule().apply {
                    type = "biweek"
                    value = "2-2"   // 2020/09/08は2週目の第2火曜
                })
        }

        val trash3 = TrashData().apply {
            type = TrashType.PAPER
            schedules = arrayListOf(
                TrashSchedule().apply {
                    type = "biweek"
                    value = "3-5"   // 2020/09/30は5週目の第5水曜
                })
        }

        Mockito.`when`(mockPersist.getAllTrashSchedule()).thenReturn(arrayListOf(trash1,trash2,trash3))
        target.refresh()

        val result1:ArrayList<TrashData> = target.getTodaysTrash(2020,9,7)
        assertEquals(1,result1.size)
        assertEquals(TrashType.BURN,result1[0].type)

        val result2:ArrayList<TrashData> = target.getTodaysTrash(2020,9,8)
        assertEquals(1,result2.size)
        assertEquals(TrashType.PETBOTTLE,result2[0].type)

        val result3:ArrayList<TrashData> = target.getTodaysTrash(2020,9,1)
        assertEquals(0,result3.size)

        val result4:ArrayList<TrashData> = target.getTodaysTrash(2020,9,30)
        assertEquals(1,result4.size)
        assertEquals(TrashType.PAPER,result4[0].type)
    }

    @Test
    fun getTodaysTrash_ExcludeDate() {
        val trash1 = TrashData().apply {
            type = TrashType.BURN
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
            type = TrashType.OTHER
            trash_val = "家電"
            schedules = arrayListOf(
                TrashSchedule().apply{
                    type = "biweek"
                    value = "3-1"
                })
        }

        val trash3 = TrashData().apply {
            type = TrashType.BOTTLE
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
        assertEquals(1,result.size)
        assertEquals(TrashType.OTHER, result[0].type)

        result = target.getTodaysTrash(2020,3,5)
        assertEquals(2,result.size)
        assertEquals(TrashType.BURN, result[0].type)
        assertEquals(TrashType.BOTTLE, result[1].type)

        result = target.getTodaysTrash(2020,3,12)
        assertEquals(1,result.size)
        assertEquals(TrashType.BOTTLE, result[0].type)
    }

    @Test
    fun getEnableTrashList_ExcludeDate_Weekday() {
        val trash1 = TrashData().apply {
            type = TrashType.BURN
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
            type = TrashType.UNBURN
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

        assertEquals(1,result[2].size)
        assertEquals(1,result[9].size)
        assertEquals(1,result[34].size)
    }

    @Test
    fun getEnableTrashList_ExcludeDate_Month(){
        val trash1 = TrashData().apply {
            type = TrashType.BURN
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
            type = TrashType.UNBURN
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

        assertEquals(1,result[0].size)
        assertEquals(1,result[3].size)
        assertEquals(1,result[34].size)
    }

    @Test
    fun getEnableTrashList_ExcludeDate_Biweek() {
        val trash1 = TrashData().apply {
            type = TrashType.BURN
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
            type = TrashType.BURN
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

        assertEquals(1, result[8].size)
        assertEquals(1, result[0].size)
        assertEquals(1, result[34].size)
    }

    @Test
    fun getEnableTrashList_ExcludeDate_Evweek() {
        val trash1 = TrashData().apply {
            type = TrashType.PAPER
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
            type = TrashType.PAPER
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

        assertEquals(1, result[0].size)
        assertEquals(1, result[6].size)
        assertEquals(1, result[34].size)

    }

    @Test
    fun getActualMonth() {
        val method: Method = target.javaClass.getDeclaredMethod("getActualMonth",Int::class.java,Int::class.java,Int::class.java)
        method.isAccessible = true

        assertEquals(1,method.invoke(target,1,6,7))
        assertEquals(12,method.invoke(target,1,29,0))
        assertEquals(2,method.invoke(target,1,1,34))

        assertEquals(1,method.invoke(target,12,1,28))
        assertEquals(1,method.invoke(target,2,29,6))
    }
}