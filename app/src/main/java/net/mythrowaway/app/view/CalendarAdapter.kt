package net.mythrowaway.app.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.mythrowaway.app.R
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
        lateinit var labelText: TextView
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
//        val dateText: TextView = cell.findViewById(R.id.dateText)
//        val trashText: TextView = cell.findViewById(R.id.trashText)
    }

    private var mDateSet: ArrayList<Int> = ArrayList(35)
    private  var mTrashData: Array<ArrayList<String>> = Array(35){arrayListOf<String>()}
    private lateinit var context: Context
    private var mYear: Int = 0
    private var mMonth: Int = 0
    private lateinit var mWeekdayLabelArray: Array<String>

    fun updateData(year:Int, month:Int, dateSet: ArrayList<Int>, trashData: Array<ArrayList<String>>) {
        this.mDateSet = dateSet
        this.mTrashData = trashData
        this.mYear = year
        this.mMonth = month

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
            if ((actualPosition < 7 && date > 7) || (actualPosition > 27 && date < 7)) {
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
            holder.trashText.textSize = 12.0F
            holder.trashText.ellipsize = TextUtils.TruncateAt.END
            holder.trashText.maxLines = 3

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