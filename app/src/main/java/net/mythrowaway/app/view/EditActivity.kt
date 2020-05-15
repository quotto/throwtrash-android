package net.mythrowaway.app.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import net.mythrowaway.app.R
import kotlinx.android.synthetic.main.activity_edit.*

class EditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
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
