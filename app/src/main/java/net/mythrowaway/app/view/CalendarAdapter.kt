package net.mythrowaway.app.view

import android.content.Context
import android.text.TextUtils
import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import net.mythrowaway.app.R
import kotlin.collections.ArrayList

class CalendarAdapter(private val mListener: CalendarAdapter.CalendarAdapterListener) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {
    interface CalendarAdapterListener {
        fun showDetailDialog(year:Int, month: Int, date: Int, trashList:ArrayList<String>)
    }

    class ViewHolder(cell: View) : RecyclerView.ViewHolder(cell) {
        val dateText: TextView = cell.findViewById(R.id.dateText)
        val trashText: TextView = cell.findViewById(R.id.trashText)
    }

    private var mDateSet: ArrayList<Int> = ArrayList(35)
    private  var mTrashData: Array<ArrayList<String>> = Array(35){arrayListOf<String>()}
    private lateinit var context: Context
    private var mCellHeight: Int = 0
    private var mYear: Int = 0
    private var mMonth: Int = 0

    fun updateData(year:Int, month:Int, dateSet: ArrayList<Int>, trashData: Array<ArrayList<String>>) {
        this.mDateSet = dateSet
        this.mTrashData = trashData
        this.mYear = year
        this.mMonth = month

        notifyDataSetChanged()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        // 縦幅の6割を5等分する
        context = recyclerView.context
        mCellHeight = Util.getEqualHeight(
            context,
            0.6f,
            5
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.date_view, parent,false)
        val params = view.getLayoutParams()
        params.height = mCellHeight
        view.setLayoutParams(params)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val date = mDateSet[position]
        if((position < 7 && date > 7) || (position > 27 && date < 7)) {
            holder.itemView.setBackgroundResource(R.color.colorDivider)
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context,android.R.color.transparent))
        }
        when(position) {
             0,7,14,21,28 -> holder.dateText.setTextColor(ContextCompat.getColor(context,
                 R.color.colorSundayText
             ))
            6,13,20,27,34 -> holder.dateText.setTextColor(ContextCompat.getColor(context,
                R.color.colorSaturdayText
            ))
            else -> holder.dateText.setTextColor(ContextCompat.getColor(context,android.R.color.black))
        }
        holder.dateText.text = mDateSet[position].toString()

        // 他Activityで削除された場合を考慮して一旦クリアする
        holder.trashText.text = ""
        holder.trashText.text = mTrashData[position].joinToString(separator = "/")
        holder.trashText.textSize = 12.0F
        holder.trashText.ellipsize = TextUtils.TruncateAt.END
        holder.trashText.maxLines = 3

        holder.itemView.setOnClickListener {
            mListener.showDetailDialog(mYear,mMonth,date, mTrashData[position])
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

    override fun getItemCount(): Int {
        return mDateSet.size
    }

}