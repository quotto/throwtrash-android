package net.mythrowaway.app.adapter.repository

import net.mythrowaway.app.domain.TrashType

interface TrashDesignRepository {
    fun getColorCode(trashType: TrashType): String
    fun setColorCode(trashType: TrashType, colorCode: String)
    fun clearColorCode()
    fun getDrawableId(trashType: TrashType): Int
    fun setDrawableId(trashType: TrashType, drawableId: Int)
    fun clearDrawableId()
}