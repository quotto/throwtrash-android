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
import net.mythrowaway.app.adapter.repository.TrashDesignRepository
import net.mythrowaway.app.domain.TrashData
import net.mythrowaway.app.service.CalendarManagerImpl
import net.mythrowaway.app.service.TrashManager
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.math.truncate

class CalendarAdapter @Inject constructor(
    private val calendarManager: CalendarManagerImpl,
    private val trashManager: TrashManager,
    private val trashDesignRepository: TrashDesignRepository) :
    RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

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
    private var mDateSet: ArrayList<Int> = ArrayList(35)
    private  var mTrashData: Array<ArrayList<TrashData>> = Array(35){arrayListOf()}
    private lateinit var context: Context
    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mTodayPos = -1
    private lateinit var mWeekdayLabelArray: Array<String>

    fun setListener(listener: CalendarAdapterListener) {
        this.mListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(year:Int, month:Int, dateSet: ArrayList<Int>, trashData: Array<ArrayList<TrashData>>) {
        this.mDateSet = dateSet
        this.mTrashData = trashData
        this.mYear = year
        this.mMonth = month
        val now = Calendar.getInstance()
        val nowYear = now.get(Calendar.YEAR)
        val nowMonth = now.get(Calendar.MONTH) + 1
        val nowDate = now.get(Calendar.DATE)

        // 表示されるカレンダー上で今日の日付の色を設定する
        val beforeMonth = calendarManager.subYM(mYear, mMonth, 1)
        val nextMonth = calendarManager.addYM(mYear, mMonth, 1)
        if (nowYear == mYear && nowMonth == mMonth) {
            Log.d(this.javaClass.simpleName, "Now is This Month($nowYear,$nowMonth)")
            dateSet.forEachIndexed { index, date ->
                if(
                    (
                            /* 1週目の日にちが最も多いのは当月が日曜始まりの場合で、第1週の土曜日が7日になるので
                                インデックスが0～6（つまり第1週）の場合は日にちが7以下なら当月の日にちとみなす
                             */
                            (index in 0..6 && date <= 7 ) ||
                                    /* 翌月の日にちが最も多く表示されるのは2月が28日間でかつ2月1日が日曜日の場合で、
                                        第5週が翌月の1日～7日になるため、インデックスが28以上（つまり当月の第5週目）なら
                                        日にちは7より大きいときだけ当月の日にちとみなす
                                     */
                                    (index >= 28 && date > 7) ||
                                    // 上記2パターン以外の場合は2週目の日曜～4週目の土曜日であること。それ以外は前月または翌月の日にちになる。
                                    index in 7..27) &&
                    date == nowDate
                ) {
                    mTodayPos = index
                    return@forEachIndexed
                }
            }
        } else if(nowYear == beforeMonth.first && nowMonth == beforeMonth.second) {
            dateSet.forEachIndexed { index, date ->
                // 1ヶ月あたり最も日数が短いのが2月の28日間
                // 当月の開始が1日の土曜日だった場合、直近の日曜日は2月23日
                if(index < 7 && date >= 23 && date == nowDate) {
                    mTodayPos = index
                    return@forEachIndexed
                }
            }
        } else if(nowYear == nextMonth.first && nowMonth == nextMonth.second) {
            // 現在の仕様では過去月のカレンダーは表示しないためこの条件判定には入らない
            dateSet.forEachIndexed { index, date ->
                if(index >= 28 && date <= 7 && date == nowDate) {
                    mTodayPos = index
                    return@forEachIndexed
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

            Log.d(this.javaClass.simpleName, "position $position=${label.text}")
        } else {
            // 実データはviewHolderのポジションから曜日ラベル分を差し引いて処理する
            val actualPosition = position - 7
            val date = mDateSet[actualPosition]
            var actualYear = mYear
            val actualMonth =
                if(actualPosition < 7 && date > 7) {if(mMonth - 1 == 0) {actualYear--; 12} else mMonth - 1}
                else if(actualPosition > 27 && date < 7) {if(mMonth + 12 == 13) {actualYear++; 1} else mMonth + 1}
                else {mMonth}
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

            // テンプレートからTextViewを生成する
            val detailTrashTextList = ArrayList<String>()

            // その日のゴミの種類を表示するTextViewを生成してListViewに追加する
            // ただしゴミの種類が4つ目は「...More」を表示する
            // それ以降は表示しない
            holder.trashTextList.removeAllViews()
            mTrashData[actualPosition].forEachIndexed {count,trashData ->
                val trashText = trashManager.getTrashName(trashData.type, trashData.trash_val)
                if(count < 3) {
                    val trashTextLayout = LayoutInflater.from(context)
                        .inflate(R.layout.text_calendar_trash_name, null) as FrameLayout
                    val textView = trashTextLayout.findViewById<TextView>(R.id.trashText)

                    textView.background =
                        context.getDrawable(trashDesignRepository.getDrawableId(trashData.type))

                    textView.text = trashText
                    holder.trashTextList.addView(trashTextLayout)
                } else if(count == 3){
                    val textView = TextView(context)
                    textView.textSize = 10.0F
                    textView.text = "...+ ${mTrashData[actualPosition].size - 3}"
                    holder.trashTextList.addView(textView)
                }
                detailTrashTextList.add(trashText)
            }

            holder.itemView.setOnClickListener {
                mListener.showDetailDialog(actualYear, actualMonth, date, detailTrashTextList)
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
        return mDateSet.size + 7
    }

    companion object {
        private const val VIEW_TYPE_DATE:Int = 0
        private const val VIEW_TYPE_LABEL:Int = 1
    }

}