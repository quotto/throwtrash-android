package net.mythrowaway.app.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.view.get
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_edit_exclude_day.*
import kotlinx.android.synthetic.main.activity_edit_exclude_day.view.*
import net.mythrowaway.app.R
import net.mythrowaway.app.adapter.ExcludeDateViewModel
import java.util.*
import kotlin.collections.ArrayList

private  class MonthListArrayAdapter(context: Context): ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,arrayListOf()) {
    fun updateAllItems(newItems: ArrayList<String>) {
        clear()
        addAll(newItems)
    }
}

class EditExcludeDayActivity : AppCompatActivity(),OnExcludeDatePickerDialogListener {
    private val viewModel: ExcludeDateViewModel by lazy {
        ViewModelProviders.of(this).get(ExcludeDateViewModel::class.java)
    }

    private fun setRowColor(view: View,index:Int) {
        if (index%2 == 0) view.setBackgroundResource(R.color.tableRowEven) else view.setBackgroundColor(Color.WHITE)
    }

    private fun appendDate(month:Int=1, date:Int=1) {
        val excludeDate = layoutInflater.inflate(R.layout.exclude_date,null)

        excludeDate.findViewById<ImageButton>(R.id.buttonRemoveExcludeDate)
            .setOnClickListener {
                val position = listExcludeDate.indexOfChild(excludeDate)
                viewModel.remove(position)
                listExcludeDate.removeView(excludeDate)
            }
        val textExcludeDate = excludeDate.findViewById<TextView>(R.id.textExcludeDate)
        textExcludeDate.text = "$month 月 $date 日"
        textExcludeDate.tag = Calendar.getInstance().timeInMillis.toString()
        textExcludeDate.setOnClickListener {
            val index = listExcludeDate.indexOfChild(excludeDate)
            viewModel.excludeDateLiveData.value?.get(index)?.apply {
                val dialog = ExcludeDatePickerDialogFragment.getInstance(index, first,second)
                dialog.show(supportFragmentManager, "日付の選択")
            }
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
                    appendDate(it.first, it.second)
                }
            }
        }
    }

    companion object {
        const val EXTRA_TRASH_NAME = "TRASH_NAME"
        const val EXTRA_EXCLUDE_DATE_SET = "EXCLUDE_DATE_SET"
    }

    override fun notifySelectedValue(targetIndex: Int, selectedMonth: Int, selectedDate: Int) {
        val target: TextView = listExcludeDate[targetIndex].findViewById<TextView>(R.id.textExcludeDate)
        target.text = "$selectedMonth 月 $selectedDate 日"

        viewModel.updateMonth(targetIndex,selectedMonth)
        viewModel.updateDate(targetIndex,selectedDate)
    }
}

