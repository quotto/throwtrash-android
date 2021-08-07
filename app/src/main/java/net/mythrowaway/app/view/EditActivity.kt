package net.mythrowaway.app.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import net.mythrowaway.app.databinding.ActivityEditBinding
import net.mythrowaway.app.adapter.MyThrowTrash
import net.mythrowaway.app.adapter.di.EditComponent

class EditActivity : AppCompatActivity() {
    private lateinit var  activityEditBinding: ActivityEditBinding

    lateinit var editComponent: EditComponent
    override fun onCreate(savedInstanceState: Bundle?) {
        editComponent = (application as MyThrowTrash).appComponent.editComponent().create();
        editComponent.inject(this)
        super.onCreate(savedInstanceState)
        activityEditBinding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(activityEditBinding.root)

        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction().apply {
                replace(
                    activityEditBinding.editMainLayout.id,
                    EditMainFragment.getInstance(
                        intent.getStringExtra(EditMainFragment.ID)
                    )
                )
                commit()
            }
        }
    }
}
