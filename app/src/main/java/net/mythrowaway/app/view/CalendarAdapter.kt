package net.mythrowaway.app.view

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.mythrowaway.app.R
import net.mythrowaway.app.adapter.DIContainer
import net.mythrowaway.app.usecase.ICalendarManager
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.truncate

class CalendarAdapter(private val mListener:CalendarAdapterListener) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {
    interface CalendarAdapterListener {
        fun showDetailDialog(year:Int, month: Int, date: Int, trashList:ArrayList<String>)
    }

    companion object {
        private const val VIEW_TYPE_DATE:Int = 0
        private const val VIEW_TYPE_LABEL:Int = 1
    }

    class ViewHolder(cell: View, viewType: Int) : RecyclerView.ViewHolder(cell) {
        lateinit var dateText: TextView
        lateinit var trashText: TextView
        private lateinit var labelText: TextView
        init {
            when(viewType) {
                VIEW_TYPE_LABEL ->
                    labelText = cell as TextView
                VIEW_TYPE_DATE -> {
                    dateText = cell.findViewById(R.id.dateText)
                    trashText = cell.findViewById(R.id.trashText)
                }
            }
        }
    }

    private var mDateSet: ArrayList<Int> = ArrayList(35)
    private  var mTrashData: Array<ArrayList<String>> = Array(35){arrayListOf<String>()}
    private lateinit var context: Context
    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mTodayPos = -1
    private lateinit var mWeekdayLabelArray: Array<String>

    fun updateData(year:Int, month:Int, dateSet: ArrayList<Int>, trashData: Array<ArrayList<String>>) {
        this.mDateSet = dateSet
        this.mTrashData = trashData
        this.mYear = year
        this.mMonth = month
        val now = Calendar.getInstance()
        val nowYear = now.get(Calendar.YEAR)
        val nowMonth = now.get(Calendar.MONTH) + 1
        val nowDate = now.get(Calendar.DATE)

        DIContainer.resolve(ICalendarManager::class.java)?.let { cm ->
            val beforeMonth = cm.subYM(mYear, mMonth, 1)
            val nextMonth = cm.addYM(mYear, mMonth, 1)
            if (nowYear == mYear && nowMonth == mMonth) {
                Log.d(this.javaClass.simpleName, "Now is This Month($nowYear,$nowMonth)")
                dateSet.forEachIndexed { index, date ->
                    if(!(index < 7 && date > 7) && date == nowDate) {
                        mTodayPos = index
                        return@forEachIndexed
                    }
                }
            } else if(nowYear == beforeMonth.first && nowMonth == beforeMonth.second) {
                dateSet.forEachIndexed { index, date ->
                    if(index < 7 && date == nowDate) {
                        mTodayPos = index
                        return@forEachIndexed
                    }
                }
            } else if(nowYear == nextMonth.first && nowMonth == nextMonth.second) {
                // 現在の仕様では過去月のカレンダーは表示しないためこの条件判定には入らない
                dateSet.forEachIndexed { index, date ->
                    if(index >= 28 && date == nowDate) {
                        mTodayPos = index
                        return@forEachIndexed
                    }
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
        mWeekdayLabelArray = context.resources.getStringArray(R.array.list_weekday_label)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when(viewType) {
            VIEW_TYPE_LABEL -> {
                val view = TextView(parent.context)
                view.textSize = 14.0F
                view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                view.textAlignment = View.TEXT_ALIGNMENT_CENTER
                ViewHolder(view, viewType)
            }
            VIEW_TYPE_DATE -> {
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(R.layout.viewholder_date_cell, parent,false)
                val layoutManager: GridLayoutManager = (parent as RecyclerView).layoutManager as GridLayoutManager
                val labelHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,14.0F,parent.context.resources.displayMetrics)
                val dividerHeight = 11 * parent.context.resources.displayMetrics.density
                val enabledHeight = layoutManager.height - labelHeight - dividerHeight
                Log.d(this.javaClass.simpleName,"RecyclerView Height -> $enabledHeight(${layoutManager.height} - $labelHeight - $dividerHeight)")
                val cellHeight = truncate(enabledHeight/5.toFloat()).toInt()
                Log.d(this.javaClass.simpleName, "Calendar Cell Height -> $cellHeight")

                view.layoutParams.height = cellHeight
                ViewHolder(view, viewType)
            }
            else -> {
                Log.e(this.javaClass.simpleName, "Unknown view type -> $viewType")
                throw Error()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(position < 7) {
            VIEW_TYPE_LABEL
        } else {
            VIEW_TYPE_DATE
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(position < 7) {
            val label = holder.itemView as TextView
            // 曜日ラベル
            when(position) {
                0 -> label.setTextColor(ContextCompat.getColor(context,R.color.colorSundayText))
                6 -> label.setTextColor(ContextCompat.getColor(context,R.color.colorSaturdayText))
                else -> label.setTextColor(ContextCompat.getColor(context,android.R.color.black))
            }
            label.text = mWeekdayLabelArray[position]

            Log.d(this.javaClass.simpleName, "position $position=${label.text}")
        } else {
            // 実データはviewHolderのポジションから曜日ラベル分を差し引いて処理する
            val actualPosition = position - 7
            val date = mDateSet[actualPosition]
            if (actualPosition == mTodayPos) {
                holder.itemView.setBackgroundResource(R.color.colorTodayCell)
            } else if ((actualPosition < 7 && date > 7) || (actualPosition > 27 && date < 7)) {
                holder.itemView.setBackgroundResource(R.color.colorDivider)
            } else {
                holder.itemView.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        android.R.color.transparent
                    )
                )
            }
            when (actualPosition) {
                0, 7, 14, 21, 28 -> holder.dateText.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorSundayText
                    )
                )
                6, 13, 20, 27, 34 -> holder.dateText.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorSaturdayText
                    )
                )
                else -> holder.dateText.setTextColor(
                    ContextCompat.getColor(
                        context,
                        android.R.color.black
                    )
                )
            }
            holder.dateText.text = mDateSet[actualPosition].toString()

            // 他Activityで削除された場合を考慮して一旦クリアする
            holder.trashText.text = ""
            holder.trashText.text = mTrashData[actualPosition].joinToString(separator = "/")

            holder.itemView.setOnClickListener {
                mListener.showDetailDialog(mYear, mMonth, date, mTrashData[actualPosition])
            }
            holder.trashText.setOnClickListener {
                // itemViewのクリックイベントを発火する
                holder.itemView.callOnClick()
            }
            holder.dateText.setOnClickListener {
                // itemViewのクリックイベントを発火する
                holder.itemView.callOnClick()
            }
        }
    }

    override fun getItemCount(): Int {
        // 日付の数+曜日ラベル
        return mDateSet.size + 7
    }

}