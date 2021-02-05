package net.mythrowaway.app.viewmodel

import androidx.lifecycle.ViewModel

/**
 * EditUseCase用のViewModel
 * このViewModelでは直接スケジュールアイテムは保持せずスケジュール総数と除外日設定だけを保持する
 * スケジュールの内容は子Fragmentで管理する
 */
class EditViewModel: ViewModel() {
    var itemCount: Int = 0
    var excludes: ArrayList<Pair<Int,Int>> = arrayListOf()
}