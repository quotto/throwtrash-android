package net.mythrowaway.app.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import net.mythrowaway.app.R
import kotlinx.android.synthetic.main.activity_edit.*
import net.mythrowaway.app.adapter.MyThrowTrash
import net.mythrowaway.app.adapter.di.EditComponent

class EditActivity : AppCompatActivity() {
    lateinit var editComponent: EditComponent
    override fun onCreate(savedInstanceState: Bundle?) {
        editComponent = (application as MyThrowTrash).appComponent.editComponent().create();
        editComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction().apply {
                replace(
                    editMainLayout.id,
                    EditMainFragment.getInstance(
                        intent.getStringExtra(EditMainFragment.ID)
                    )
                )
                commit()
            }
        }
    }
}
