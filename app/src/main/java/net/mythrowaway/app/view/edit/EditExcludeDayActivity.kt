package net.mythrowaway.app.view.edit

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import net.mythrowaway.app.R
import net.mythrowaway.app.databinding.ActivityEditExcludeDayBinding
import net.mythrowaway.app.viewmodel.ExcludeDateViewModel
import java.util.*
import kotlin.collections.ArrayList

class EditExcludeDayActivity : AppCompatActivity(), OnExcludeDatePickerDialogListener {
    private lateinit var activityEditExcludeDayBinding: ActivityEditExcludeDayBinding

    private val viewModel: ExcludeDateViewModel by lazy {
        ViewModelProvider(this).get(ExcludeDateViewModel::class.java)
    }

    private fun setRowColor(view: View,index:Int) {
        if (index%2 == 0) view.setBackgroundResource(R.color.tableRowEven) else view.setBackgroundColor(Color.WHITE)
    }

    private fun appendDate(month:Int=1, date:Int=1) {
        val excludeDate = layoutInflater.inflate(R.layout.exclude_date,null)

        excludeDate.findViewById<ImageButton>(R.id.buttonRemoveExcludeDate)
            .setOnClickListener {
                val position = activityEditExcludeDayBinding.listExcludeDate.indexOfChild(excludeDate)
                viewModel.remove(position)
                activityEditExcludeDayBinding.listExcludeDate.removeView(excludeDate)
            }
        val textExcludeDate = excludeDate.findViewById<TextView>(R.id.textExcludeDate)
        textExcludeDate.text = "$month 月 $date 日"
        textExcludeDate.tag = Calendar.getInstance().timeInMillis.toString()
        textExcludeDate.setOnClickListener {
            val index = activityEditExcludeDayBinding.listExcludeDate.indexOfChild(excludeDate)
            viewModel.excludeDateLiveData.value?.get(index)?.apply {
                val dialog = ExcludeDatePickerDialogFragment.getInstance(index, first, second)
                dialog.show(supportFragmentManager, "日付の選択")
            }
        }
        setRowColor(excludeDate,activityEditExcludeDayBinding.listExcludeDate.childCount)
        activityEditExcludeDayBinding.listExcludeDate.addView(
            excludeDate,activityEditExcludeDayBinding.listExcludeDate.childCount - 1
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityEditExcludeDayBinding = ActivityEditExcludeDayBinding.inflate(layoutInflater)
        setContentView(activityEditExcludeDayBinding.root)

        activityEditExcludeDayBinding.buttonAddExcludeDate.setOnClickListener {
            viewModel.add()
            appendDate()
        }

        activityEditExcludeDayBinding.trashName.text = intent.getStringExtra(EXTRA_TRASH_NAME)
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

        activityEditExcludeDayBinding.buttonRegisterExcludeDate.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra(EXTRA_EXCLUDE_DATE_SET,viewModel.excludeDateLiveData.value)
            setResult(Activity.RESULT_OK,resultIntent)
            finish()
        }

        viewModel.excludeDateLiveData.observe(this, {
            activityEditExcludeDayBinding.buttonAddExcludeDate.visibility =
                if(it.size < 10) View.VISIBLE else View.INVISIBLE
            activityEditExcludeDayBinding.buttonRegisterExcludeDate.isEnabled = it.size > 0

            for (index in 0 until activityEditExcludeDayBinding.listExcludeDate.childCount-1) {
                setRowColor(activityEditExcludeDayBinding.listExcludeDate[index],index+1)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.excludeDateLiveData.value?.apply {
            if (activityEditExcludeDayBinding.listExcludeDate.childCount == 1) {
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
        val target: TextView =
            activityEditExcludeDayBinding.listExcludeDate[targetIndex].findViewById(R.id.textExcludeDate)
        target.text = "$selectedMonth 月 $selectedDate 日"

        viewModel.updateMonth(targetIndex,selectedMonth)
        viewModel.updateDate(targetIndex,selectedDate)
    }
}

