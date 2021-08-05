package net.mythrowaway.app.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import net.mythrowaway.app.databinding.ActivityEditBinding

class EditActivity : AppCompatActivity() {
    private lateinit var  activityEditBinding: ActivityEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
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
