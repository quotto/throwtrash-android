package com.example.mythrowtrash.view

import android.content.Context
import android.text.TextUtils
import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mythrowtrash.R
import kotlinx.android.synthetic.main.fragment_calendar.*
import kotlin.collections.ArrayList

class CalendarAdapter : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {
    class ViewHolder(cell: View) : RecyclerView.ViewHolder(cell) {
        val dateText: TextView = cell.findViewById(R.id.dateText)
        val trashText: TextView = cell.findViewById(R.id.trashText)
    }

    private var mDateSet: ArrayList<Int> = ArrayList(35)
    private  var mTrashData: Array<ArrayList<String>> = Array(35){arrayListOf<String>()}
    private lateinit var context: Context
    private var mCellHeight: Int = 0

    fun updateData(dateSet: ArrayList<Int>, trashData: Array<ArrayList<String>>) {
        this.mDateSet = dateSet
        this.mTrashData = trashData

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
    }

    override fun getItemCount(): Int {
        return mDateSet.size
    }

}