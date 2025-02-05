package net.mythrowaway.app.module.trash.presentation.view.calendar

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class TrashOfDayDialog: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val builder = AlertDialog.Builder(context)
        builder.setTitle(arguments?.getString(YMD, ""))
        builder.setMessage(arguments?.getString(MESSAGE, ""))
        return builder.create()
    }
    companion object {
        fun newInstance(year:Int,month:Int,date:Int, trashList:ArrayList<String>): TrashOfDayDialog {
            val bundle = Bundle()
            bundle.putString(YMD, "${year}年${month}月${date}日")
            val message = when {
                trashList.isNotEmpty() -> trashList.joinToString("\n")
                else -> "出せるゴミはありません"
            }
            bundle.putString(MESSAGE, message)
            val dialog = TrashOfDayDialog()
            dialog.arguments = bundle
            return dialog
        }
        private const val YMD = "YMD"
        private const val MESSAGE = "MESSAGE"
    }
}