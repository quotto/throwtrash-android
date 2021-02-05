package net.mythrowaway.app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ExcludeDateViewModel: ViewModel() {
    private val _excludeDateLiveData: MutableLiveData<ArrayList<Pair<Int,Int>>> by lazy{
        MutableLiveData<ArrayList<Pair<Int,Int>>>()
    }
    val excludeDateLiveData: LiveData<ArrayList<Pair<Int,Int>>> get() = _excludeDateLiveData

    init {
        _excludeDateLiveData.value = arrayListOf()
    }

    fun add() {
        Log.d(this.javaClass.simpleName, "add exclude date")
        val newDateSet = _excludeDateLiveData.value
        newDateSet?.add(Pair(1,1))
        _excludeDateLiveData.postValue(newDateSet)
    }

    fun remove(position: Int) {
        Log.d(this.javaClass.simpleName, "remove exclude date @$position")
        val newDateSet = _excludeDateLiveData.value
        newDateSet?.removeAt(position)
        _excludeDateLiveData.postValue((newDateSet))
    }

    fun updateMonth(position:Int,month: Int) {
        Log.d(this.javaClass.simpleName, "update exclude month @$position -> $month")
        val newDateSet = _excludeDateLiveData.value
        newDateSet?.set(position,Pair(month,newDateSet[position].second))
        _excludeDateLiveData.postValue(newDateSet)
    }

    fun updateDate(position: Int, date: Int) {
        Log.d(this.javaClass.simpleName, "update exclude date @$position -> $date")
        val newDateSet = _excludeDateLiveData.value
        newDateSet?.set(position, Pair(newDateSet[position].first,date))
        _excludeDateLiveData.postValue((newDateSet))
    }
}