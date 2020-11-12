package net.mythrowaway.app.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.view.children
import androidx.core.view.forEachIndexed
import androidx.core.view.get
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_edit_exclude_day.*
import kotlinx.android.synthetic.main.activity_edit_exclude_day.view.*
import net.mythrowaway.app.R
import net.mythrowaway.app.adapter.ExcludeDateViewModel

private  class MonthListArrayAdapter(context: Context): ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,arrayListOf()) {
    fun updateAllItems(newItems: ArrayList<String>) {
        clear()
        addAll(newItems)
    }
}

class EditExcludeDayActivity : AppCompatActivity() {
    private val viewModel: ExcludeDateViewModel by lazy {
        ViewModelProviders.of(this).get(ExcludeDateViewModel::class.java)
    }

    private fun getDateArray(month: Int): ArrayList<String> {
        val days30 = arrayListOf<String>(
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "10",
            "11",
            "12",
            "13",
            "14",
            "15",
            "16",
            "17",
            "18",
            "19",
            "20",
            "21",
            "22",
            "23",
            "24",
            "25",
            "26",
            "27",
            "28",
            "29",
            "30"
        )
        val days31 = arrayListOf<String>(
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "10",
            "11",
            "12",
            "13",
            "14",
            "15",
            "16",
            "17",
            "18",
            "19",
            "20",
            "21",
            "22",
            "23",
            "24",
            "25",
            "26",
            "27",
            "28",
            "29",
            "30",
            "31"
        )
        val days29 = arrayListOf<String>(
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "10",
            "11",
            "12",
            "13",
            "14",
            "15",
            "16",
            "17",
            "18",
            "19",
            "20",
            "21",
            "22",
            "23",
            "24",
            "25",
            "26",
            "27",
            "28",
            "29"
        )
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> {
                days31
            }
            2, 4, 6, 9, 11 -> {
                days30
            }
            else -> {
                days29
            }
        }
    }

    private fun setRowColor(view: View,index:Int) {
        if (index%2 == 0) view.setBackgroundResource(R.color.tableRowEven) else view.setBackgroundColor(Color.WHITE)
    }

    private fun appendDate(month_position:Int=0, date_position:Int=0) {
        val excludeDate = layoutInflater.inflate(R.layout.exclude_date,null)

        val excludeDateList = excludeDate.findViewById<Spinner>(R.id.spinnerDate)
        val excludeMonthList = excludeDate.findViewById<Spinner>(R.id.spinnerMonth)

        val adapter: MonthListArrayAdapter = MonthListArrayAdapter(this)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        excludeDateList.adapter = adapter
        adapter.updateAllItems(getDateArray(month_position+1))

        excludeMonthList.setSelection(month_position,false)
        excludeDateList.setSelection(date_position,false)

        excludeDateList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val listPosition = listExcludeDate.indexOfChild(excludeDate)
                viewModel.updateDate(listPosition, position + 1)
            }
        }

        excludeMonthList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                excludeDate.findViewById<Spinner>(excludeDateList.id)?.apply {
                    (this.adapter as MonthListArrayAdapter).updateAllItems(getDateArray(position+1))
                }
                val listPosition = listExcludeDate.indexOfChild(excludeDate)
                viewModel.updateMonth(listPosition, position + 1)
            }
        }

        excludeDate.findViewById<ImageButton>(R.id.buttonRemoveExcludeDate)
            .setOnClickListener {
                val position = listExcludeDate.indexOfChild(excludeDate)
                viewModel.remove(position)
                listExcludeDate.removeView(excludeDate)
            }

        setRowColor(excludeDate,listExcludeDate.childCount)
        listExcludeDate.addView(excludeDate,listExcludeDate.childCount - 1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_exclude_day)

        scrollViewExcludeDate.listExcludeDate.buttonAddExcludeDate.setOnClickListener {
            viewModel.add()
            appendDate()
        }

        trashName.text = intent.getStringExtra(EXTRA_TRASH_NAME)
        intent.getSerializableExtra(EXTRA_EXCLUDE_DATE_SET)?.apply {
            (this as ArrayList<Pair<Int, Int>>).forEachIndexed { index, pair ->
                viewModel.add()
                viewModel.updateMonth(index, pair.first)
                viewModel.updateDate(index, pair.second)
            }
            // EXTRA_EXCLUDE_DATE_SETはActivityの最初の起動時のみ必要なため削除する
            // 残したままにすると画面回転など2回目以降の開始でViewModelが変更されてしまう
            intent.removeExtra(EXTRA_EXCLUDE_DATE_SET)
        }

        buttonRegisterExcludeDate.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra(EXTRA_EXCLUDE_DATE_SET,viewModel.excludeDateLiveData.value)
            setResult(Activity.RESULT_OK,resultIntent)
            finish()
        }

        viewModel.excludeDateLiveData.observe(this, Observer {
            buttonAddExcludeDate.visibility = if(it.size < 10) View.VISIBLE else View.INVISIBLE
            buttonRegisterExcludeDate.isEnabled = it.size > 0

            for (index in 0 until listExcludeDate.childCount-1) {
                setRowColor(listExcludeDate[index],index+1)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.excludeDateLiveData.value?.apply {
            if (listExcludeDate.childCount == 1) {
                this.forEach {
                    appendDate(it.first - 1, it.second - 1)
                }
            }
        }
    }

    companion object {
        const val EXTRA_TRASH_NAME = "TRASH_NAME"
        const val EXTRA_EXCLUDE_DATE_SET = "EXCLUDE_DATE_SET"
    }
}

