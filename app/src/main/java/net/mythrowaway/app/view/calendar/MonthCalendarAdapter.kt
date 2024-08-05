package net.mythrowaway.app.view.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.mythrowaway.app.R
import net.mythrowaway.app.usecase.dto.CalendarDayDTO
import net.mythrowaway.app.view.common.ColorUtility
import java.time.LocalDate
import kotlin.collections.ArrayList
import kotlin.math.truncate

class MonthCalendarAdapter(
    private val mMonth: Int,
    private var mCalendarDayDTOS: List<CalendarDayDTO>
) :
    RecyclerView.Adapter<MonthCalendarAdapter.ViewHolder>() {

    interface CalendarAdapterListener {
        fun showDetailDialog(year:Int, month: Int, date: Int, trashList:ArrayList<String>)
    }

    class ViewHolder(cell: View, viewType: Int) : RecyclerView.ViewHolder(cell) {
        lateinit var dateText: TextView
        lateinit var trashTextList: LinearLayout
        private lateinit var labelText: TextView
        init {
            when(viewType) {
                VIEW_TYPE_LABEL ->
                    labelText = cell as TextView
                VIEW_TYPE_DATE -> {
                    dateText = cell.findViewById(R.id.dateText)
                    trashTextList = cell.findViewById(R.id.trashTextListLayout)
                }
            }
        }
    }

    private lateinit var mListener: CalendarAdapterListener
    private lateinit var context: Context
    private var mTodayPos = -1
    private lateinit var mWeekdayLabelArray: Array<String>

    fun setListener(listener: CalendarAdapterListener) {
        this.mListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(calendarDayDTOS: List<CalendarDayDTO>) {
        this.mCalendarDayDTOS = calendarDayDTOS
        // 表示されるカレンダー上で今日の日付の色を設定する
        val today = LocalDate.now()
        mCalendarDayDTOS.forEachIndexed{ index, calendarDay ->
            if(today.equals(LocalDate.of(calendarDay.getYear(), calendarDay.getMonth(), calendarDay.getDayOfMonth()))) {
                mTodayPos = index
                return@forEachIndexed
            }
        }
        notifyDataSetChanged()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        Log.d(this.javaClass.simpleName, "onAttachedToRecyclerView")
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
                val cellHeight = truncate(enabledHeight/5.toFloat()).toInt()

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
        } else {
            // 実データはviewHolderのポジションから曜日ラベル分を差し引いて処理する
            val actualPosition = position - 7
            val calendarDay = mCalendarDayDTOS[actualPosition]
            if (actualPosition == mTodayPos) {
                holder.itemView.setBackgroundResource(R.color.colorTodayCell)
            } else if (calendarDay.getMonth() != mMonth) {
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
            holder.dateText.text = calendarDay.getDayOfMonth().toString()

            // テンプレートからTextViewを生成する
            val detailTrashTextList = ArrayList<String>()

            // その日のゴミの種類を表示するTextViewを生成してListViewに追加する
            // ただしゴミの種類が4つ目は「...More」を表示する
            // それ以降は表示しない
            holder.trashTextList.removeAllViews()
            calendarDay.getTrashes().forEachIndexed {count,trashDataDto ->
                if(count < 3) {
                    val trashTextLayout = LayoutInflater.from(context)
                        .inflate(R.layout.text_calendar_trash_name, null) as FrameLayout
                    val textView = trashTextLayout.findViewById<TextView>(R.id.trashText)

                    textView.background =
                        context.getDrawable(ColorUtility.getTrashDrawableId(trashDataDto.type))

                    textView.text = trashDataDto.displayName
                    holder.trashTextList.addView(trashTextLayout)
                } else if(count == 3){
                    val textView = TextView(context)
                    textView.textSize = 10.0F
                    textView.text = "...+ ${mCalendarDayDTOS[actualPosition].getTrashes().size - 3}"
                    holder.trashTextList.addView(textView)
                }
                detailTrashTextList.add(trashDataDto.displayName)
            }

            holder.itemView.setOnClickListener {
                mListener.showDetailDialog(calendarDay.getYear(), calendarDay.getMonth(), calendarDay.getDayOfMonth(), detailTrashTextList)
            }

            holder.trashTextList.setOnClickListener {
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
        return 42;
    }

    companion object {
        private const val VIEW_TYPE_DATE:Int = 0
        private const val VIEW_TYPE_LABEL:Int = 1
    }

}